package dream.server;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dream.common.Outbox;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.overlay.IOverlayPeerlet;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;

public class ServerEventForwarder extends BasePeerlet {
  protected final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final SubscriptionTable clientsSubTable = new SubscriptionTable();
  protected final SubscriptionTable brokersSubTable = new SubscriptionTable();
  protected final AdvertisementTable advTable = new AdvertisementTable();

  protected IOverlayPeerlet overlay;

  @Override
  public void init(Peer peer) {
    super.init(peer);
    overlay = (IOverlayPeerlet) peer.getPeerletOfType(IOverlayPeerlet.class);
  }

  @Override
  public final void handleIncomingMessage(Message packet) {
    final Outbox outbox = new Outbox();
    if (packet instanceof SubscriptionPacket) {
      final SubscriptionPacket subPkt = (SubscriptionPacket) packet;
      logger.fine("Received a subscription packet: " + subPkt);
      processSubscription(packet.getSourceAddress(), subPkt, outbox);
    } else if (packet instanceof EventPacket) {
      final EventPacket evPkt = (EventPacket) packet;
      logger.finer("Received an event packet: " + evPkt);
      processEvent(packet.getSourceAddress(), evPkt, outbox);
    } else if (packet instanceof AdvertisementPacket) {
      final AdvertisementPacket advPkt = (AdvertisementPacket) packet;
      logger.fine("Received an advertisement packet: " + advPkt);
      processAdvertisement(packet.getSourceAddress(), advPkt, outbox);
    }
    deliverPacketsInOutbox(outbox);
  }

  private final void processSubscription(NetworkAddress sender, SubscriptionPacket packet, Outbox box) {
    updateSubscriptionTables(sender, packet);
    final Set<NetworkAddress> matchingNodes = advTable.getMatchingNodes(packet.getSubscription());
    if (!matchingNodes.isEmpty()) {
      sendTo(SubscriptionPacket.subject, packet, box, matchingNodes);
    }
  }

  private final void updateSubscriptionTables(NetworkAddress sender, SubscriptionPacket subPkt) {
    final SubscriptionTable table = isClient(sender) ? clientsSubTable : brokersSubTable;
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

  private void processEvent(NetworkAddress sender, EventPacket packet, Outbox outbox) {
    final Map<NetworkAddress, Integer> matchingClients = clientsSubTable.getMatchingNodes(packet.getEvent());
    final Map<NetworkAddress, Integer> matchingBrokers = brokersSubTable.getMatchingNodes(packet.getEvent());
    sendTo(EventPacket.subject, packet, outbox, matchingClients.keySet());
    sendTo(EventPacket.subject, packet, outbox, matchingBrokers.keySet());
  }

  private final void processAdvertisement(NetworkAddress sender, AdvertisementPacket packet, Outbox outbox) {
    switch (packet.getAdvType()) {
    case ADV:
      advTable.addAdvertisement(sender, packet.getAdvertisement());
      break;
    case UNADV:
      advTable.removeAdvertisement(sender, packet.getAdvertisement());
      break;
    }
    outbox.add(AdvertisementPacket.subject, packet, getAllNodesExcept(sender));
  }

  private final void sendTo(String subject, Message packet, Outbox box, Collection<NetworkAddress> recipients) {
    box.add(subject, packet, recipients);
  }

  private final Collection<NetworkAddress> getAllNodesExcept(NetworkAddress nodeToSkip) {
    return overlay.getNeighbors().stream()//
        .filter(node -> !node.equals(nodeToSkip))//
        .collect(Collectors.toSet());
  }

  private final void deliverPacketsInOutbox(Outbox outbox) {
    outbox.getPacketsToSend().forEach(p -> {
      outbox.getRecipientsFor(p).forEach(r -> getPeer().sendMessage(r, p));
    });
  }

  private final boolean isClient(NetworkAddress address) {
    return overlay.getComponents().contains(address);
  }

}
