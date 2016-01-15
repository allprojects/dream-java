package dream.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import dream.common.utils.DependencyGraph;
import dream.common.utils.IntraSourceDependencyDetector;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class ClientEventForwarder implements PacketForwarder {
  private static ClientEventForwarder self = null;

  private final ConnectionManager connectionManager;
  private final ClientSubscriptionTable subTable;
  private final DependencyGraph dependencyGraph = DependencyGraph.instance;
  private final IntraSourceDependencyDetector intraDepDetector = IntraSourceDependencyDetector.instance;

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
    connectionManager.registerForwarder(this, AdvertisementPacket.subject);
    connectionManager.registerForwarder(this, SubscriptionPacket.subject);
    connectionManager.registerForwarder(this, EventPacket.subject);
  }

  @Override
  public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    final Collection<NodeDescriptor> result = new ArrayList<NodeDescriptor>();
    if (subject.equals(AdvertisementPacket.subject)) {
      assert packet instanceof AdvertisementPacket;
      logger.finer("Received an advertisement packet " + packet);
      processAdvertisementFromServer((AdvertisementPacket) packet);
    } else if (subject.equals(SubscriptionPacket.subject)) {
      assert packet instanceof SubscriptionPacket;
      logger.fine("Received a subscription packet " + packet);
      processSubscriptionFromServer((SubscriptionPacket) packet);
    } else if (subject.equals(EventPacket.subject)) {
      assert packet instanceof EventPacket;
      logger.finer("Received an event packet " + packet);
      processEventFromServer((EventPacket) packet);
    } else {
      assert false : subject;
    }
    return result;
  }

  public final void sendEvent(UUID id, Event ev, String initialVar, boolean approvedByTokenService) {
    sendEvent(id, ev, initialVar, new HashSet<>(), approvedByTokenService);
  }

  public final void sendEvent(UUID id, Event ev, String initialVar, Set<String> finalExpressions, boolean approvedByTokenService) {
    logger.finer("Sending an event " + ev);
    if (subTable.needsToDeliverToServer(ev)) {
      connectionManager.sendEvent(id, ev, initialVar, finalExpressions, approvedByTokenService);
    }
  }

  public final void advertise(Advertisement adv, boolean isPublic) {
    logger.fine("Sending advertisement " + adv);
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      dependencyGraph.processAdv(adv);
      intraDepDetector.consolidate();
    }
    connectionManager.sendAdvertisement(adv, isPublic);
  }

  public final void unadvertise(Advertisement adv, boolean isPublic) {
    logger.fine("Sending unadvertisement " + adv);
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      dependencyGraph.processUnAdv(adv);
      intraDepDetector.consolidate();
    }
    connectionManager.sendUnadvertisement(adv, isPublic);
  }

  public final void advertise(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    logger.fine("Sending advertisement " + adv + " with subscriptions " + subs);
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      dependencyGraph.processAdv(adv, subs);
      intraDepDetector.consolidate();
    }
    connectionManager.sendAdvertisement(adv, subs, isPublic);
  }

  public final void unadvertise(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    logger.fine("Sending unadvertisement " + adv + " with subscriptions " + subs);
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      dependencyGraph.processUnAdv(adv, subs);
      intraDepDetector.consolidate();
    }
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
    return !isLocal(sub);
  }

  private final boolean isLocal(Subscription<?> sub) {
    return sub.getHostId().equals(Consts.hostName);
  }

  private final void processEventFromServer(EventPacket evPkt) {
    subTable.getMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      subTable.getSignatureOnlyMatchingSubscribers(evPkt.getEvent()).forEach(sub -> sub.notifyEventReceived(evPkt));
    }
  }

  private final void processAdvertisementFromServer(AdvertisementPacket advPkt) {
    if (Consts.consistencyType == ConsistencyType.SINGLE_SOURCE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
        Consts.consistencyType == ConsistencyType.ATOMIC) {
      final Set<Subscription> subs = advPkt.getSubscriptions();
      switch (advPkt.getAdvType()) {
      case ADV:
        if (subs.isEmpty()) {
          dependencyGraph.processAdv(advPkt.getAdvertisement());
        } else {
          dependencyGraph.processAdv(advPkt.getAdvertisement(), subs);
        }
        intraDepDetector.consolidate();
        break;
      case UNADV:
        if (subs.isEmpty()) {
          dependencyGraph.processUnAdv(advPkt.getAdvertisement());
        } else {
          dependencyGraph.processUnAdv(advPkt.getAdvertisement(), subs);
        }
        intraDepDetector.consolidate();
        break;
      }
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