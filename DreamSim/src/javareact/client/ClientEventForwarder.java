package javareact.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;
import javareact.experiments.JavaReactConfiguration;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.time.Timer;
import protopeer.time.TimerListener;
import protopeer.util.quantities.Time;

public class ClientEventForwarder extends BasePeerlet {
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private final double timeBeforeSendingSubscriptionsInSeconds = 1;

  private final ClientSubscriptionTable subTable = new ClientSubscriptionTable();
  private ConnectionManager connectionManager;

  @Override
  public void init(Peer peer) {
    super.init(peer);
    connectionManager = (ConnectionManager) peer.getPeerletOfType(ConnectionManager.class);
  }

  @Override
  public void handleIncomingMessage(Message packet) {
    if (packet instanceof EventPacket) {
      logger.finer("Received an event packet " + packet);
      processEventFromServer((EventPacket) packet);
    } else if (packet instanceof SubscriptionPacket) {
      logger.fine("Received a subscription packet " + packet);
      processSubscriptionFromServer((SubscriptionPacket) packet);
    }
  }

  public final void sendEvent(UUID id, Event ev, Set<String> computedFrom, double timestamp, boolean approvedByTokenService) {
    sendEvent(id, ev, computedFrom, new HashSet<String>(), timestamp, approvedByTokenService);
  }

  public final void sendEvent(UUID id, Event ev, Set<String> computedFrom, Set<String> finalExpressions, double timestamp, boolean approvedByTokenService) {
    logger.finer("Sending an event " + ev);
    // Local forward occurs only if glitch freedom is not guaranteed.
    // Indeed, to ensure glitch freedom, all events, including local ones,
    // need to be pass through the server before being delivered
    if (JavaReactConfiguration.getSingleton().consistencyType != JavaReactConfiguration.GLITCH_FREE && JavaReactConfiguration.getSingleton().consistencyType != JavaReactConfiguration.ATOMIC) {
      for (Subscriber sub : subTable.getMatchingSubscribers(ev)) {
        sub.notifyValueChanged(new EventPacket(ev, id, computedFrom, timestamp, approvedByTokenService));
      }
    }
    if (subTable.needsToDeliverToServer(ev) || ev.isPersistent()) {
      connectionManager.sendEvent(id, ev, computedFrom, finalExpressions, timestamp, approvedByTokenService);
    }
  }

  public final void advertise(Advertisement adv, boolean isPublic) {
    logger.fine("Sending advertisement " + adv);
    connectionManager.sendAdvertisement(adv, isPublic);
  }

  public final void unadvertise(Advertisement adv, boolean isPublic) {
    logger.fine("Sending unadvertisement " + adv);
    connectionManager.sendUnadvertisement(adv, isPublic);
  }

  public final void advertise(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    logger.fine("Sending advertisement " + adv + " with subscriptions " + subs);
    connectionManager.sendAdvertisement(adv, subs, isPublic);
  }

  public final void unadvertise(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    logger.fine("Sending unadvertisement " + adv + " with subscriptions " + subs);
    connectionManager.sendUnadvertisement(adv, isPublic);
  }

  public final void addSubscriptions(Subscriber subscriber, Set<Subscription> subscriptions) {
    logger.fine("Adding subscriptions " + subscriptions);
    subTable.addSubscriptions(subscriber, subscriptions);
    final Set<Subscription> subsToSendToServer = getSubscriptionsToForwardToServer(subscriptions);
    // Timer used to delay the delivery of subscriptions after all advertisements have been received
    Timer timer = getPeer().getClock().createNewTimer();
    timer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        connectionManager.sendSubscription(subsToSendToServer);
      }
    });
    timer.schedule(Time.inSeconds(timeBeforeSendingSubscriptionsInSeconds));
  }

  public final void removeSubscriptions(Subscriber subscriber, Set<Subscription> subscriptions) {
    logger.fine("Removing subscriptions " + subscriptions);
    subTable.removeSubscriptions(subscriber, subscriptions);
    Set<Subscription> subsToSendToServer = getSubscriptionsToForwardToServer(subscriptions);
    connectionManager.sendUnsubscription(subsToSendToServer);
  }

  private final Set<Subscription> getSubscriptionsToForwardToServer(Set<Subscription> subscriptions) {
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.GLITCH_FREE || JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC) {
      return subscriptions;
    }
    Set<Subscription> result = new HashSet<Subscription>();
    for (Subscription sub : subscriptions) {
      if (!isLocal(sub)) {
        result.add(sub);
      }
    }
    return result;
  }

  private final boolean isLocal(Subscription sub) {
    int clientId = ((TrafficGeneratorPeerlet) getPeer().getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    String hostId = Consts.hostPrefix + clientId;
    return sub.getHostId().equals(hostId);
  }

  private final void processEventFromServer(EventPacket evPkt) {
    for (Subscriber sub : subTable.getMatchingSubscribers(evPkt.getEvent())) {
      sub.notifyValueChanged(evPkt);
    }
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.GLITCH_FREE || JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC) {
      for (Subscriber sub : subTable.getSignatureOnlyMatchingSubscribers(evPkt.getEvent())) {
        sub.notifyValueChanged(evPkt);
      }
    }
  }

  private final void processSubscriptionFromServer(SubscriptionPacket subPkt) {
    for (Subscription sub : subPkt) {
      switch (subPkt.getSubType()) {
      case SUB:
        subTable.addServerSubscription(sub);
        break;
      case UNSUB:
        subTable.removeServerSubscription(sub);
        break;
      default:
        assert false : subPkt.getSubType();
      }
    }
  }

}
