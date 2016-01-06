package javareact.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class ClientEventForwarder implements PacketForwarder {
  private static ClientEventForwarder self = null;

  private final ConnectionManager connectionManager;
  private final ClientSubscriptionTable subTable;
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public static final ClientEventForwarder get() {
    if (self == null) {
      self = new ClientEventForwarder();
    }
    return self;
  }

  public static final void stop() {
    if (self != null) {
      self.stopClient();
      self = null;
    }
  }

  private final void stopClient() {
    connectionManager.stop();
  }

  private ClientEventForwarder() {
    connectionManager = new ConnectionManager();
    subTable = new ClientSubscriptionTable();
    connectionManager.registerForwarder(this, EventPacket.subject);
    connectionManager.registerForwarder(this, SubscriptionPacket.subject);
  }

  @Override
  public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    final Collection<NodeDescriptor> result = new ArrayList<NodeDescriptor>();
    if (subject.equals(EventPacket.subject)) {
      assert packet instanceof EventPacket;
      logger.finer("Received an event packet " + packet);
      processEventFromServer((EventPacket) packet);
    } else if (subject.equals(SubscriptionPacket.subject)) {
      assert packet instanceof SubscriptionPacket;
      logger.fine("Received a subscription packet " + packet);
      processSubscriptionFromServer((SubscriptionPacket) packet);
    } else {
      assert false : subject;
    }
    return result;
  }

  public final void sendEvent(UUID id, Event ev, String initialVar, boolean approvedByTokenService) {
    sendEvent(id, ev, initialVar, new HashSet<String>(), approvedByTokenService);
  }

  public final void sendEvent(UUID id, Event ev, String initialVar, Set<String> finalExpressions, boolean approvedByTokenService) {
    logger.finer("Sending an event " + ev);
    // Local forward occurs only if glitch freedom is not guaranteed.
    // Indeed, to ensure glitch freedom, all events, including local ones,
    // need to be pass through the server before being delivered
    if (Consts.consistencyType != ConsistencyType.GLITCH_FREE && Consts.consistencyType != ConsistencyType.ATOMIC) {
      subTable.getMatchingSubscribers(ev).forEach(sub -> sub.notifyEventReceived(new EventPacket(ev, id, initialVar, approvedByTokenService)));
    }
    if (subTable.needsToDeliverToServer(ev)) {
      connectionManager.sendEvent(id, ev, initialVar, finalExpressions, approvedByTokenService);
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

  public final void unadvertise(Advertisement adv, Set<Subscription<?>> subs, boolean isPublic) {
    logger.fine("Sending unadvertisement " + adv + " with subscriptions " + subs);
    connectionManager.sendUnadvertisement(adv, isPublic);
  }

  public final void addSubscription(Subscriber subscriber, Subscription<?> subscription) {
    logger.fine("Adding subscription " + subscription);
    subTable.addSubscription(subscriber, subscription);
    if (needToSendToServer(subscription)) {
      connectionManager.sendSubscription(subscription);
    }
  }

  public final void removeSubscription(Subscriber subscriber, Subscription<?> subscription) {
    logger.fine("Adding subscription " + subscription);
    subTable.addSubscription(subscriber, subscription);
    if (needToSendToServer(subscription)) {
      connectionManager.sendSubscription(subscription);
    }
  }

  private final boolean needToSendToServer(Subscription<?> sub) {
    return //
    !isLocal(sub) || //
        Consts.consistencyType == ConsistencyType.GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC;
  }

  private final boolean isLocal(Subscription<?> sub) {
    return sub.getHostId().equals(Consts.hostName);
  }

  private final void processEventFromServer(EventPacket evPkt) {
    subTable.getMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
    if (Consts.consistencyType == ConsistencyType.GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      subTable.getSignatureOnlyMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
    }
  }

  private final void processSubscriptionFromServer(SubscriptionPacket subPkt) {
    switch (subPkt.getSubType()) {
    case SUB:
      subTable.addServerSubscription(subPkt.getSubscription());
      break;
    case UNSUB:
      subTable.removeServerSubscription(subPkt.getSubscription());
      break;
    default:
      assert false : subPkt.getSubType();
    }
  }

}
