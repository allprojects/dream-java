package javareact.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.UUID;

import javareact.common.Consts;
import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.SubType;
import javareact.common.packets.content.Subscription;
import javareact.common.utils.WaitRecommendations;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.NotRunningException;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.overlay.SimpleTopologyManager;
import polimi.reds.broker.overlay.TCPTransport;
import polimi.reds.broker.overlay.TopologyManager;
import polimi.reds.broker.overlay.Transport;
import polimi.reds.broker.routing.GenericRouter;
import polimi.reds.broker.routing.PacketForwarder;

class ConnectionManager {
  private final Overlay overlay;
  private final GenericRouter router;

  ConnectionManager() {
    Transport tr = null;
    try {
      tr = new TCPTransport();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    final TopologyManager tm = new SimpleTopologyManager();
    overlay = new GenericOverlay(tm, tr, false);
    router = new GenericRouter(overlay);
    overlay.start();
    try {
      overlay.addNeighbor(Consts.serverAddr);
    } catch (ConnectException | MalformedURLException | NotRunningException e) {
      e.printStackTrace();
    }
  }

  final void sendEvent(UUID id, Event<? extends Serializable> event, String initialVar, Set<WaitRecommendations> waitRecomendations, Set<String> finalExpressions, boolean approvedByTokenService) {
    final EventPacket pkt = new EventPacket(event, id, initialVar, approvedByTokenService);
    finalExpressions.forEach(pkt::addFinalExpression);
    waitRecomendations.forEach(pkt::addWaitRecommendations);
    send(EventPacket.subject, pkt);
  }

  final void sendSubscription(Subscription sub) {
    final SubscriptionPacket pkt = new SubscriptionPacket(sub, SubType.SUB);
    send(SubscriptionPacket.subject, pkt);
  }

  final void sendUnsubscription(Subscription sub) {
    final SubscriptionPacket pkt = new SubscriptionPacket(sub, SubType.UNSUB);
    send(SubscriptionPacket.subject, pkt);
  }

  final void sendAdvertisement(Advertisement adv, boolean isPublic) {
    sendAdvertisement(adv, AdvType.ADV, null, isPublic);
  }

  final void sendAdvertisement(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    sendAdvertisement(adv, AdvType.ADV, subs, isPublic);
  }

  final void sendUnadvertisement(Advertisement adv, boolean isPublic) {
    sendAdvertisement(adv, AdvType.UNADV, null, isPublic);
  }

  final void sendUnadvertisement(Advertisement adv, Set<Subscription> subs, boolean isPublic) {
    sendAdvertisement(adv, AdvType.UNADV, subs, isPublic);
  }

  private final void sendAdvertisement(Advertisement adv, AdvType advType, Set<Subscription> subs, boolean isPublic) {
    final AdvertisementPacket pkt = subs != null ? new AdvertisementPacket(adv, advType, subs, isPublic) : new AdvertisementPacket(adv, advType, isPublic);
    send(AdvertisementPacket.subject, pkt);
  }

  final void registerForwarder(PacketForwarder forwarder, String subject) {
    router.setPacketForwarder(subject, forwarder);
  }

  final void stop() {
    overlay.stop();
  }

  private final void send(String subject, Serializable packet) {
    assert overlay.getNumberOfBrokers() == 1;
    assert overlay.getNumberOfClients() == 0;
    for (final NodeDescriptor node : overlay.getNeighbors()) {
      try {
        overlay.send(subject, packet, node);
      } catch (IOException | NotRunningException e) {
        e.printStackTrace();
      }
    }
  }

}
