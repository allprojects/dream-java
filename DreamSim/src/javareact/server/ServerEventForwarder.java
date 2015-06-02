package javareact.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javareact.common.Outbox;
import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.Subscription;
import javareact.common.packets.registry.RegistryAdvertisePacket;
import javareact.common.packets.token_service.TokenAckPacket;
import javareact.common.packets.token_service.TokenServiceAdvertisePacket;
import javareact.experiments.JavaReactConfiguration;
import javareact.overlay.IOverlayPeerlet;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;

public class ServerEventForwarder extends BasePeerlet {
  protected final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final SubscriptionTable clientsSubTable = new SubscriptionTable();
  protected final SubscriptionTable brokersSubTable = new SubscriptionTable();
  protected final AdvertisementTable advTable = new AdvertisementTable();
  protected final DependencyDetector dependencyDetector = new DependencyDetector();

  protected IOverlayPeerlet overlay;

  protected NetworkAddress registry = null;
  protected NetworkAddress tokenService = null;

  @Override
  public void init(Peer peer) {
    super.init(peer);
    overlay = (IOverlayPeerlet) peer.getPeerletOfType(IOverlayPeerlet.class);
  }

  @Override
  public final void handleIncomingMessage(Message packet) {
    Outbox outbox = new Outbox();
    if (packet instanceof SubscriptionPacket) {
      SubscriptionPacket subPkt = (SubscriptionPacket) packet;
      logger.fine("Received a subscription packet: " + subPkt);
      processSubscription(subPkt.getSourceAddress(), subPkt, outbox);
    } else if (packet instanceof EventPacket) {
      EventPacket evPkt = (EventPacket) packet;
      logger.finer("Received an event packet: " + evPkt);
      processEvent(evPkt.getSourceAddress(), evPkt, outbox);
    } else if (packet instanceof AdvertisementPacket) {
      AdvertisementPacket advPkt = (AdvertisementPacket) packet;
      logger.fine("Received an advertisement packet: " + advPkt);
      processAdvertisement(advPkt.getSourceAddress(), advPkt, outbox);
    } else if (packet instanceof RegistryAdvertisePacket) {
      RegistryAdvertisePacket regAdvPkt = (RegistryAdvertisePacket) packet;
      logger.fine("Received a registry advertise packet: " + regAdvPkt);
      processRegistryAdvertise(regAdvPkt.getSourceAddress(), regAdvPkt);
    } else if (packet instanceof TokenServiceAdvertisePacket) {
      TokenServiceAdvertisePacket tokenServiceAdvPkt = (TokenServiceAdvertisePacket) packet;
      logger.fine("Received a token service advertise packet: " + tokenServiceAdvPkt);
      processTokenServiceAdvertise(tokenServiceAdvPkt.getSourceAddress(), tokenServiceAdvPkt);
    }
    deliverPacketsInOutbox(outbox);
  }

  private final void processSubscription(NetworkAddress sender, SubscriptionPacket packet, Outbox box) {
    updateSubscriptionTables(sender, packet);
    partitionAndDeliverSubscriptions(sender, packet, box);
    sendSubscriptionToRegistryIfNeeded(packet, sender, box);
  }

  private final void updateSubscriptionTables(NetworkAddress sender, SubscriptionPacket subPkt) {
    SubscriptionTable table = isClient(sender) ? clientsSubTable : brokersSubTable;
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

  private final void partitionAndDeliverSubscriptions(NetworkAddress sender, SubscriptionPacket subPkt, Outbox box) {
    Map<NetworkAddress, Set<Subscription>> subPartitions = partitionSubscriptions(subPkt);
    Map<NetworkAddress, SubscriptionPacket> subPackets = generateSubscriptionPackets(subPkt, subPartitions);
    for (NetworkAddress node : subPackets.keySet()) {
      SubscriptionPacket pkt = subPackets.get(node);
      sendTo(SubscriptionPacket.subject, pkt, box, node);
    }
  }

  private final Map<NetworkAddress, Set<Subscription>> partitionSubscriptions(SubscriptionPacket subPkt) {
    Map<NetworkAddress, Set<Subscription>> result = new HashMap<NetworkAddress, Set<Subscription>>();
    for (Subscription sub : subPkt) {
      Collection<NetworkAddress> nodesToDeliverTo = advTable.getMatchingNodes(sub);
      for (NetworkAddress node : nodesToDeliverTo) {
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

  private final Map<NetworkAddress, SubscriptionPacket> generateSubscriptionPackets(SubscriptionPacket subPkt, Map<NetworkAddress, Set<Subscription>> subs) {
    Map<NetworkAddress, SubscriptionPacket> result = new HashMap<NetworkAddress, SubscriptionPacket>();
    for (NetworkAddress node : subs.keySet()) {
      Set<Subscription> subSet = subs.get(node);
      SubscriptionPacket pkt = new SubscriptionPacket(subSet, subPkt.getSubType());
      result.put(node, pkt);
    }
    return result;
  }

  private void processEvent(NetworkAddress sender, EventPacket packet, Outbox outbox) {
    // In case of atomic consistency, each initial event must be first delivered to the token service
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC && !packet.isApprovedByTokenService()) {
      sendToTokenService(EventPacket.subject, packet, outbox);
    } else {
      if ((isClient(sender) || sender == tokenService) && (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.GLITCH_FREE || JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC)) {
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
    Map<NetworkAddress, Integer> matchingClients = clientsSubTable.getMatchingNodes(pkt.getEvent());
    Map<NetworkAddress, Integer> matchingBrokers = brokersSubTable.getMatchingNodes(pkt.getEvent());
    sendTo(EventPacket.subject, pkt, outbox, matchingClients.keySet());
    sendTo(EventPacket.subject, pkt, outbox, matchingBrokers.keySet());
    sendTokenAckIfNeeded(pkt, matchingClients, outbox);
  }

  private final void sendTokenAckIfNeeded(EventPacket pkt, Map<NetworkAddress, Integer> clients, Outbox outbox) {
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC && pkt.isFinal() && !clients.isEmpty()) {
      TokenAckPacket tokenAck = new TokenAckPacket(pkt.getEvent().getSignature(), getSubscribersCount(clients));
      sendToTokenService(TokenAckPacket.subject, tokenAck, outbox);
    }
  }

  private final int getSubscribersCount(Map<NetworkAddress, Integer> clients) {
    int count = 0;
    for (Integer val : clients.values()) {
      count += val;
    }
    return count;
  }

  private final void processAdvertisement(NetworkAddress sender, AdvertisementPacket packet, Outbox outbox) {
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.GLITCH_FREE || JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC) {
      dependencyDetector.processAdvertisementPacket(packet);
      dependencyDetector.consolidate();
    }
    if (JavaReactConfiguration.getSingleton().consistencyType == JavaReactConfiguration.ATOMIC && isClient(sender)) {
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
    outbox.add(AdvertisementPacket.subject, packet, getAllBrokersExcept(sender));
  }

  private final void processRegistryAdvertise(NetworkAddress sender, RegistryAdvertisePacket packet) {
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

  private final void processTokenServiceAdvertise(NetworkAddress sender, TokenServiceAdvertisePacket packet) {
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

  private final void sendSubscriptionToRegistryIfNeeded(SubscriptionPacket subPkt, NetworkAddress sender, Outbox box) {
    if (registry != null && nodeIsLastBroker(sender)) {
      sendToRegistry(SubscriptionPacket.subject, subPkt, box);
    }
  }

  private final void sendToRegistry(String subject, Message packet, Outbox box) {
    sendTo(subject, packet, box, registry);
  }

  private final void sendToTokenService(String subject, Message packet, Outbox box) {
    sendTo(subject, packet, box, tokenService);
  }

  private final void sendTo(String subject, Message packet, Outbox box, Collection<NetworkAddress> recipients) {
    box.add(subject, packet, recipients);
  }

  private final void sendTo(String subject, Message packet, Outbox box, NetworkAddress recipient) {
    Collection<NetworkAddress> recipients = new ArrayList<NetworkAddress>(1);
    recipients.add(recipient);
    box.add(subject, packet, recipients);
  }

  private final boolean nodeIsLastBroker(NetworkAddress sender) {
    return getAllBrokersExcept(sender).isEmpty();
  }

  private final Collection<NetworkAddress> getAllBrokersExcept(NetworkAddress nodeToSkip) {
    Collection<NetworkAddress> result = new ArrayList<NetworkAddress>();
    for (NetworkAddress broker : overlay.getBrokers()) {
      if (broker.equals(nodeToSkip)) continue;
      if (registry != null && broker.equals(registry)) continue;
      if (tokenService != null && broker.equals(tokenService)) continue;
      result.add(broker);
    }
    return result;
  }

  private final void deliverPacketsInOutbox(Outbox outbox) {
    for (Message packet : outbox.getPacketsToSend()) {
      for (NetworkAddress recipient : outbox.getRecipientsFor(packet)) {
        getPeer().sendMessage(recipient, packet);
      }
    }
  }

  private final boolean isClient(NetworkAddress address) {
    return overlay.getComponents().contains(address);
  }

}
