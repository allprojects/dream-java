package dream.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.registry.RegistryAdvertisePacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.NeighborhoodChangeListener;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class ServerEventForwarder implements PacketForwarder, NeighborhoodChangeListener {
  protected final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final SubscriptionTable clientsSubTable = new SubscriptionTable();
  protected final SubscriptionTable brokersSubTable = new SubscriptionTable();
  protected final AdvertisementTable advTable = new AdvertisementTable();

  protected NodeDescriptor registry = null;
  protected NodeDescriptor tokenService = null;

  @Override
  public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    if (subject.equals(SubscriptionPacket.subject)) {
      assert packet instanceof SubscriptionPacket;
      final SubscriptionPacket subPkt = (SubscriptionPacket) packet;
      logger.fine("Received a subscription packet: " + subPkt);
      processSubscription(sender, subPkt, neighbors, outbox);
    } else if (subject.equals(EventPacket.subject)) {
      assert packet instanceof EventPacket;
      final EventPacket evPkt = (EventPacket) packet;
      logger.finer("Received an event packet: " + evPkt);
      processEvent(sender, evPkt, neighbors, outbox);
    } else if (subject.equals(AdvertisementPacket.subject)) {
      assert packet instanceof AdvertisementPacket;
      final AdvertisementPacket advPkt = (AdvertisementPacket) packet;
      logger.fine("Received an advertisement packet: " + advPkt);
      processAdvertisement(sender, advPkt, neighbors, outbox);
    } else if (subject.equals(RegistryAdvertisePacket.subject)) {
      assert packet instanceof RegistryAdvertisePacket;
      final RegistryAdvertisePacket regAdvPkt = (RegistryAdvertisePacket) packet;
      logger.fine("Received a registry advertise packet: " + regAdvPkt);
      processRegistryAdvertise(sender, regAdvPkt);
    } else {
      assert false;
      logger.warning("Received an unknown packet subject");
    }
    return new ArrayList<NodeDescriptor>();
  }

  private final void processSubscription(NodeDescriptor sender, SubscriptionPacket packet, Collection<NodeDescriptor> neighbors, Outbox box) {
    updateSubscriptionTables(sender, packet);
    final Set<NodeDescriptor> matchingNodes = advTable.getMatchingNodes(packet.getSubscription());
    if (!matchingNodes.isEmpty()) {
      sendTo(SubscriptionPacket.subject, packet, box, matchingNodes);
    }
  }

  private final void updateSubscriptionTables(NodeDescriptor sender, SubscriptionPacket subPkt) {
    final SubscriptionTable table = sender.isClient() ? clientsSubTable : brokersSubTable;
    switch (subPkt.getSubType()) {
    case SUB:
      table.addSubscription(sender, subPkt.getSubscription());
      break;
    case UNSUB:
      table.removeSubscription(sender, subPkt.getSubscription());
      break;
    default:
      assert false : subPkt.getSubType();
    }
  }

  private void processEvent(NodeDescriptor sender, EventPacket packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    // In case of atomic consistency, each initial event must be first delivered
    // to the token service
    if (Consts.consistencyType == ConsistencyType.ATOMIC && !packet.isApprovedByTokenService()) {
      sendToTokenService(EventPacket.subject, packet, outbox);
    } else {
      sendEvent(packet, outbox);
    }
  }

  private final void sendEvent(EventPacket pkt, Outbox outbox) {
    final Map<NodeDescriptor, Integer> matchingClients = clientsSubTable.getMatchingNodes(pkt.getEvent());
    final Map<NodeDescriptor, Integer> matchingBrokers = brokersSubTable.getMatchingNodes(pkt.getEvent());
    sendTo(EventPacket.subject, pkt, outbox, matchingClients.keySet());
    sendTo(EventPacket.subject, pkt, outbox, matchingBrokers.keySet());
  }

  private final int getSubscribersCount(Map<NodeDescriptor, Integer> clients) {
    int count = 0;
    for (final Integer val : clients.values()) {
      count += val;
    }
    return count;
  }

  private final void processAdvertisement(NodeDescriptor sender, AdvertisementPacket packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    if (Consts.consistencyType == ConsistencyType.ATOMIC && sender.isClient()) {
      assert tokenService != null;
      sendToTokenService(AdvertisementPacket.subject, packet, outbox);
    }
    if (packet.isPublic()) {
      switch (packet.getAdvType()) {
      case ADV:
        advTable.addAdvertisement(sender, packet.getAdvertisement());
        break;
      case UNADV:
        advTable.removeAdvertisement(sender, packet.getAdvertisement());
        break;
      }
    }
    outbox.add(AdvertisementPacket.subject, packet, getAllNodesExcept(sender, neighbors));
  }

  private final void processRegistryAdvertise(NodeDescriptor sender, RegistryAdvertisePacket packet) {
    switch (packet.getType()) {
    case ADV:
      registry = sender;
      break;
    case UNADV:
      registry = null;
      break;
    default:
      assert false : packet.getType();
    }
  }

  private final void sendToTokenService(String subject, Serializable packet, Outbox box) {
    sendTo(subject, packet, box, tokenService);
  }

  private final void sendTo(String subject, Serializable packet, Outbox box, Collection<NodeDescriptor> recipients) {
    box.add(subject, packet, recipients);
  }

  private final void sendTo(String subject, Serializable packet, Outbox box, NodeDescriptor recipient) {
    final Collection<NodeDescriptor> recipients = new ArrayList<NodeDescriptor>(1);
    recipients.add(recipient);
    box.add(subject, packet, recipients);
  }

  private final Collection<NodeDescriptor> getAllNodesExcept(NodeDescriptor nodeToSkip, Collection<NodeDescriptor> neighbors) {
    final Collection<NodeDescriptor> result = new ArrayList<>(neighbors);
    result.remove(nodeToSkip);
    return result;
  }

  private final void reactToRemovedNeighbor(NodeDescriptor node) {
    logger.fine("Removing neighbor");
    if (node.isBroker()) {
      brokersSubTable.removeAllSubscriptionsFor(node);
      advTable.removeAllAdvertisementsFor(node);
    } else {
      clientsSubTable.removeAllSubscriptionsFor(node);
    }
  }

  @Override
  public final void notifyNeighborAdded(NodeDescriptor node) {
    // Nothing to do
  }

  @Override
  public final void notifyNeighborDead(NodeDescriptor node) {
    reactToRemovedNeighbor(node);
  }

  @Override
  public final void notifyNeighborRemoved(NodeDescriptor node) {
    reactToRemovedNeighbor(node);
  }

}
