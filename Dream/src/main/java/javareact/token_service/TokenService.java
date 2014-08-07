package javareact.token_service;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.token_service.TokenAckPacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

/**
 * A token service ensures that only one event (called active event) is propagated through the broker network at any
 * given time. In particular, it stores incoming events in a queue and delivers them only when no more forwarding are
 * planned for the currently active event.
 */
public class TokenService implements PacketForwarder {
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final Queue<PendingEvent> queue = new ArrayDeque<PendingEvent>();
  private final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();

  @Override
  public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox box) {
    if (subject.equals(EventPacket.subject)) {
      assert (packet instanceof EventPacket);
      logger.fine("Received an event packet");
      processEventPacket((EventPacket) packet, sender, box);
    } else if (subject.equals(AdvertisementPacket.subject)) {
      assert (packet instanceof AdvertisementPacket);
      logger.fine("Received an advertisement packet");
      processAdvertisementPacket((AdvertisementPacket) packet, sender, box);
    } else if (subject.equals(TokenAckPacket.subject)) {
      assert (packet instanceof TokenAckPacket);
      logger.fine("Received a token ack packet");
      processTokenAckPacket((TokenAckPacket) packet, box);
    } else {
      logger.warning("Received a message of unknown type");
    }
    return new ArrayList<NodeDescriptor>();
  }

  private final void processEventPacket(EventPacket pkt, NodeDescriptor sender, Outbox box) {
    PendingEvent pending = new PendingEvent(pkt, sender);
    boolean canStartProcessing = queue.isEmpty();
    queue.add(pending);
    if (canStartProcessing) {
      forwardNextEventIfAny(box);
    }
  }

  private final void processAdvertisementPacket(AdvertisementPacket pkt, NodeDescriptor sender, Outbox box) {
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

  private class PendingEvent {
    private final EventPacket eventPkt;
    private final Collection<NodeDescriptor> recipients = new ArrayList<NodeDescriptor>(1);
    private final Map<String, Integer> waitingReplies = new HashMap<String, Integer>();

    PendingEvent(EventPacket eventPkt, NodeDescriptor sender) {
      this.eventPkt = eventPkt;
      recipients.add(sender);
      waitingReplies.putAll(finalExpressionsDetector.getFinalExpressionsFor(eventPkt.getEvent().getSignature()));
      addFinalExpressionsToPacket();
    }

    final EventPacket getEventPkt() {
      eventPkt.tokenServiceApproves();
      return eventPkt;
    }

    final Collection<NodeDescriptor> getRecipients() {
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
