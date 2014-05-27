package javareact.token_service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import javareact.common.Consts;
import javareact.common.Outbox;
import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.token_service.TokenAckPacket;
import javareact.common.packets.token_service.TokenServiceAdvertisePacket;
import javareact.experiments.JavaReactConfiguration;
import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.time.Timer;
import protopeer.time.TimerListener;
import protopeer.util.quantities.Time;

/**
 * A token service ensures that only one event (called active event) is propagated through the broker network at any
 * given time. In particular, it stores incoming events in a queue and delivers them only when no more forwarding are
 * planned for the currently active event.
 */
public class TokenService extends BasePeerlet {
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Queue<PendingEvent> queue = new ArrayDeque<PendingEvent>();
  private final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
  private final Set<NetworkAddress> brokers = new HashSet<NetworkAddress>();

  @Override
  public void init(Peer peer) {
    super.init(peer);
    connectToBrokers();
    sendTokenServiceAdvertisement();
  }

  @Override
  public void handleIncomingMessage(Message packet) {
    Outbox outbox = new Outbox();
    if (packet instanceof EventPacket) {
      logger.fine("Received an event packet");
      processEventPacket((EventPacket) packet, packet.getSourceAddress(), outbox);
    } else if (packet instanceof AdvertisementPacket) {
      logger.fine("Received an advertisement packet");
      processAdvertisementPacket((AdvertisementPacket) packet, packet.getSourceAddress(), outbox);
    } else if (packet instanceof TokenAckPacket) {
      logger.fine("Received a token ack packet");
      processTokenAckPacket((TokenAckPacket) packet, outbox);
    }
    deliverPacketsInOutbox(outbox);
  }

  private final void connectToBrokers() {
    for (int i = 1; i <= JavaReactConfiguration.getSingleton().numberOfBrokers; i++) {
      NetworkAddress address = Experiment.getSingleton().getAddressToBindTo(i);
      brokers.add(address);
    }
  }

  private final void sendTokenServiceAdvertisement() {
    Timer tokenServiceAdvertisementTimer = getPeer().getClock().createNewTimer();
    tokenServiceAdvertisementTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        for (NetworkAddress broker : brokers) {
          getPeer().sendMessage(broker, new TokenServiceAdvertisePacket(AdvType.ADV));
        }
      }
    });
    tokenServiceAdvertisementTimer.schedule(Time.inSeconds(Consts.startTokenServiceAdvertiseAtSecond));
  }

  private final void processEventPacket(EventPacket pkt, NetworkAddress sender, Outbox box) {
    PendingEvent pending = new PendingEvent(pkt, sender);
    boolean canStartProcessing = queue.isEmpty();
    queue.add(pending);
    if (canStartProcessing) {
      forwardNextEventIfAny(box);
    }
  }

  private final void processAdvertisementPacket(AdvertisementPacket pkt, NetworkAddress sender, Outbox box) {
    finalExpressionsDetector.processAdvertisementPacket(pkt);
    finalExpressionsDetector.consolidate();
  }

  private final void processTokenAckPacket(TokenAckPacket pkt, Outbox box) {
    if (queue.isEmpty()) {
      assert (false);
      logger.warning("Received a TokenAck but the queue is empty");
    }
    PendingEvent pending = queue.peek();
    pending.processTokenAck(pkt);
    if (pending.hasFinishedWaiting()) {
      queue.poll();
      forwardNextEventIfAny(box);
    }
  }

  private final void forwardNextEventIfAny(Outbox box) {
    if (!queue.isEmpty()) {
      PendingEvent pending = queue.peek();
      box.add(EventPacket.subject, pending.getEventPkt(), pending.getRecipients());
    }
  }

  private final void deliverPacketsInOutbox(Outbox outbox) {
    for (Message packet : outbox.getPacketsToSend()) {
      for (NetworkAddress recipient : outbox.getRecipientsFor(packet)) {
        getPeer().sendMessage(recipient, packet);
      }
    }
  }

  private class PendingEvent {
    private final EventPacket eventPkt;
    private final Collection<NetworkAddress> recipients = new ArrayList<NetworkAddress>(1);
    private final Map<String, Integer> waitingReplies = new HashMap<String, Integer>();

    PendingEvent(EventPacket eventPkt, NetworkAddress sender) {
      this.eventPkt = eventPkt;
      recipients.add(sender);
      waitingReplies.putAll(finalExpressionsDetector.getFinalExpressionsFor(eventPkt.getEvent().getSignature()));
      addFinalExpressionsToPacket();
    }

    final EventPacket getEventPkt() {
      eventPkt.tokenServiceApproves();
      return eventPkt;
    }

    final Collection<NetworkAddress> getRecipients() {
      return recipients;
    }

    final void processTokenAck(TokenAckPacket pkt) {
      removeFromWaiting(pkt.getFinalExpression(), pkt.getCount());
    }

    final boolean hasFinishedWaiting() {
      return waitingReplies.isEmpty();
    }

    private final void removeFromWaiting(String finalExpression, int count) {
      assert (waitingReplies.containsKey(finalExpression));
      int currentCount = waitingReplies.get(finalExpression);
      int newCount = currentCount - count;
      assert (newCount >= 0);
      waitingReplies.remove(finalExpression);
      if (newCount > 0) {
        waitingReplies.put(finalExpression, newCount);
      }
    }

    private final void addFinalExpressionsToPacket() {
      for (String finalExpr : waitingReplies.keySet()) {
        eventPkt.addFinalExpression(finalExpr);
      }
    }
  }

}
