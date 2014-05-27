package javareact.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.Subscription;
import javareact.common.packets.registry.RegistryAdvertisePacket;
import javareact.common.packets.token_service.TokenAckPacket;
import javareact.common.packets.token_service.TokenServiceAdvertisePacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.NeighborhoodChangeListener;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

public class ServerEventForwarder implements PacketForwarder, NeighborhoodChangeListener {
  protected final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final SubscriptionTable clientsSubTable = new SubscriptionTable();
  protected final SubscriptionTable brokersSubTable = new SubscriptionTable();
  protected final AdvertisementTable advTable = new AdvertisementTable();
  protected final DependencyDetector dependencyDetector = new DependencyDetector();

  protected NodeDescriptor registry = null;
  protected NodeDescriptor tokenService = null;

  @Override
  public Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    if (subject.equals(SubscriptionPacket.subject)) {
      assert (packet instanceof SubscriptionPacket);
      SubscriptionPacket subPkt = (SubscriptionPacket) packet;
      logger.fine("Received a subscription packet: " + subPkt);
      processSubscription(sender, subPkt, neighbors, outbox);
    } else if (subject.equals(EventPacket.subject)) {
      assert (packet instanceof EventPacket);
      EventPacket evPkt = (EventPacket) packet;
      logger.finer("Received an event packet: " + evPkt);
      processEvent(sender, evPkt, neighbors, outbox);
    } else if (subject.equals(AdvertisementPacket.subject)) {
      assert (packet instanceof AdvertisementPacket);
      AdvertisementPacket advPkt = (AdvertisementPacket) packet;
      logger.fine("Received an advertisement packet: " + advPkt);
      processAdvertisement(sender, advPkt, neighbors, outbox);
    } else if (subject.equals(RegistryAdvertisePacket.subject)) {
      assert (packet instanceof RegistryAdvertisePacket);
      RegistryAdvertisePacket regAdvPkt = (RegistryAdvertisePacket) packet;
      logger.fine("Received a registry advertise packet: " + regAdvPkt);
      processRegistryAdvertise(sender, regAdvPkt);
    } else if (subject.equals(TokenServiceAdvertisePacket.subject)) {
      assert (packet instanceof TokenServiceAdvertisePacket);
      TokenServiceAdvertisePacket tokenServiceAdvPkt = (TokenServiceAdvertisePacket) packet;
      logger.fine("Received a token service advertise packet: " + tokenServiceAdvPkt);
      processTokenServiceAdvertise(sender, tokenServiceAdvPkt);
    } else {
      assert false;
      logger.warning("Received an unknown event subject");
    }
    return new ArrayList<NodeDescriptor>();
  }

  private final void processSubscription(NodeDescriptor sender, SubscriptionPacket packet, Collection<NodeDescriptor> neighbors, Outbox box) {
    updateSubscriptionTables(sender, packet);
    partitionAndDeliverSubscriptions(sender, neighbors, packet, box);
    sendSubscriptionToRegistryIfNeeded(packet, sender, neighbors, box);
  }

  private final void updateSubscriptionTables(NodeDescriptor sender, SubscriptionPacket subPkt) {
    SubscriptionTable table = sender.isClient() ? clientsSubTable : brokersSubTable;
    for (Subscription sub : subPkt) {
      switch (subPkt.getSubType()) {
      case SUB:
        table.addSubscription(sender, sub);
        break;
      case UNSUB:
        table.removeSubscription(sender, sub);
        break;
      }
    }
  }

  private final void partitionAndDeliverSubscriptions(NodeDescriptor sender, Collection<NodeDescriptor> neighbors, SubscriptionPacket subPkt, Outbox box) {
    Map<NodeDescriptor, Set<Subscription>> subPartitions = partitionSubscriptions(subPkt);
    Map<NodeDescriptor, SubscriptionPacket> subPackets = generateSubscriptionPackets(subPkt, subPartitions);
    for (NodeDescriptor node : subPackets.keySet()) {
      SubscriptionPacket pkt = subPackets.get(node);
      sendTo(SubscriptionPacket.subject, pkt, box, node);
    }
  }

  private final Map<NodeDescriptor, Set<Subscription>> partitionSubscriptions(SubscriptionPacket subPkt) {
    Map<NodeDescriptor, Set<Subscription>> result = new HashMap<NodeDescriptor, Set<Subscription>>();
    for (Subscription sub : subPkt) {
      Collection<NodeDescriptor> nodesToDeliverTo = advTable.getMatchingNodes(sub);
      for (NodeDescriptor node : nodesToDeliverTo) {
        Set<Subscription> subSet = result.get(node);
        if (subSet == null) {
          subSet = new HashSet<Subscription>();
          result.put(node, subSet);
        }
        subSet.add(sub);
      }
    }
    return result;
  }

  private final Map<NodeDescriptor, SubscriptionPacket> generateSubscriptionPackets(SubscriptionPacket subPkt, Map<NodeDescriptor, Set<Subscription>> subs) {
    Map<NodeDescriptor, SubscriptionPacket> result = new HashMap<NodeDescriptor, SubscriptionPacket>();
    for (NodeDescriptor node : subs.keySet()) {
      Set<Subscription> subSet = subs.get(node);
      SubscriptionPacket pkt = new SubscriptionPacket(subSet, subPkt.getSubType());
      result.put(node, pkt);
    }
    return result;
  }

  private void processEvent(NodeDescriptor sender, EventPacket packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    // In case of atomic consistency, each initial event must be first delivered to the token service
    if (Consts.consistencyType == ConsistencyType.ATOMIC && !packet.isApprovedByTokenService()) {
      sendToTokenService(EventPacket.subject, packet, outbox);
    } else {
      if ((sender.isClient() || sender.equals(tokenService)) && (Consts.consistencyType == ConsistencyType.GLITCH_FREE || Consts.consistencyType == ConsistencyType.ATOMIC)) {
        Set<WaitRecommendations> waitRecommendations = dependencyDetector.getWaitRecommendations(packet.getEvent(), packet.getComputedFrom());
        sendEvent(packet, waitRecommendations, outbox);
      } else {
        sendEvent(packet, outbox);
      }
    }
  }

  private final void sendEvent(EventPacket pkt, Outbox outbox) {
    sendEvent(pkt, new HashSet<WaitRecommendations>(), outbox);
  }

  private final void sendEvent(EventPacket pkt, Set<WaitRecommendations> waitRecommendations, Outbox outbox) {
    for (WaitRecommendations wr : waitRecommendations) {
      pkt.addWaitRecommendations(wr);
    }
    sendEventToRegistryIfNeeded(pkt, outbox);
    Map<NodeDescriptor, Integer> matchingClients = clientsSubTable.getMatchingNodes(pkt.getEvent());
    Map<NodeDescriptor, Integer> matchingBrokers = brokersSubTable.getMatchingNodes(pkt.getEvent());
    sendTo(EventPacket.subject, pkt, outbox, matchingClients.keySet());
    sendTo(EventPacket.subject, pkt, outbox, matchingBrokers.keySet());
    sendTokenAckIfNeeded(pkt, matchingClients, outbox);
  }

  private final void sendTokenAckIfNeeded(EventPacket pkt, Map<NodeDescriptor, Integer> clients, Outbox outbox) {
    if (Consts.consistencyType == ConsistencyType.ATOMIC && pkt.isFinal()) {
      TokenAckPacket tokenAck = new TokenAckPacket(pkt.getEvent().getSignature(), getSubscribersCount(clients));
      sendToTokenService(TokenAckPacket.subject, tokenAck, outbox);
    }
  }

  private final int getSubscribersCount(Map<NodeDescriptor, Integer> clients) {
    int count = 0;
    for (Integer val : clients.values()) {
      count += val;
    }
    return count;
  }

  private final void processAdvertisement(NodeDescriptor sender, AdvertisementPacket packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    if (Consts.consistencyType == ConsistencyType.GLITCH_FREE || Consts.consistencyType == ConsistencyType.ATOMIC) {
      dependencyDetector.processAdvertisementPacket(packet);
      dependencyDetector.consolidate();
    }
    if (Consts.consistencyType == ConsistencyType.ATOMIC && sender.isClient()) {
      assert (tokenService != null);
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
    outbox.add(AdvertisementPacket.subject, packet, getAllBrokersExcept(sender, neighbors));
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

  private final void processTokenServiceAdvertise(NodeDescriptor sender, TokenServiceAdvertisePacket packet) {
    switch (packet.getType()) {
    case ADV:
      tokenService = sender;
      break;
    case UNADV:
      tokenService = null;
      break;
    default:
      assert false : packet.getType();
    }

  }

  private final void sendEventToRegistryIfNeeded(EventPacket pkt, Outbox outbox) {
    if (registry != null && pkt.getEvent().isPersistent()) {
      sendToRegistry(EventPacket.subject, pkt, outbox);
    }
  }

  private final void sendSubscriptionToRegistryIfNeeded(SubscriptionPacket subPkt, NodeDescriptor sender, Collection<NodeDescriptor> neighbors, Outbox box) {
    if (registry != null && nodeIsLastBroker(sender, neighbors)) {
      sendToRegistry(SubscriptionPacket.subject, subPkt, box);
    }
  }

  private final void sendToRegistry(String subject, Serializable packet, Outbox box) {
    sendTo(subject, packet, box, registry);
  }

  private final void sendToTokenService(String subject, Serializable packet, Outbox box) {
    sendTo(subject, packet, box, tokenService);
  }

  private final void sendTo(String subject, Serializable packet, Outbox box, Collection<NodeDescriptor> recipients) {
    box.add(subject, packet, recipients);
  }

  private final void sendTo(String subject, Serializable packet, Outbox box, NodeDescriptor recipient) {
    Collection<NodeDescriptor> recipients = new ArrayList<NodeDescriptor>(1);
    recipients.add(recipient);
    box.add(subject, packet, recipients);
  }

  private final boolean nodeIsLastBroker(NodeDescriptor sender, Collection<NodeDescriptor> neighbors) {
    return getAllBrokersExcept(sender, neighbors).isEmpty();
  }

  private final Collection<NodeDescriptor> getAllBrokersExcept(NodeDescriptor nodeToSkip, Collection<NodeDescriptor> neighbors) {
    Collection<NodeDescriptor> result = new ArrayList<NodeDescriptor>();
    for (NodeDescriptor neighbor : neighbors) {
      if (!neighbor.isBroker()) continue;
      if (neighbor.equals(nodeToSkip)) continue;
      if (registry != null && neighbor.equals(registry)) continue;
      if (tokenService != null && neighbor.equals(tokenService)) continue;
      result.add(neighbor);
    }
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
