package dream.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.content.AdvType;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.SubType;
import dream.common.packets.content.Subscription;
import dream.common.packets.discovery.LockManagerHelloPacket;
import dream.common.packets.discovery.ServerHelloPacket;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.NotRunningException;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.overlay.SimpleTopologyManager;
import polimi.reds.broker.overlay.TCPTransport;
import polimi.reds.broker.overlay.TopologyManager;
import polimi.reds.broker.overlay.Transport;
import polimi.reds.broker.routing.GenericRouter;
import polimi.reds.broker.routing.Outbox;
import polimi.reds.broker.routing.PacketForwarder;

class ConnectionManager implements PacketForwarder {
  private final Overlay overlay;
  private final GenericRouter router;

  private NodeDescriptor server;
  private NodeDescriptor lockManager;

  private final Queue<PacketSubjectPair> serverQueue = new LinkedList<>();
  private final Queue<PacketSubjectPair> lockManagerQueue = new LinkedList<>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
    router.setPacketForwarder(ServerHelloPacket.subject, this);
    router.setPacketForwarder(LockManagerHelloPacket.subject, this);
    overlay.start();
    try {
      overlay.addNeighbor(Consts.serverAddr);
      if (Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
          Consts.consistencyType == ConsistencyType.ATOMIC) {
        overlay.addNeighbor(Consts.lockManagerAddr);
      }
    } catch (ConnectException | MalformedURLException | NotRunningException e) {
      e.printStackTrace();
    }
  }

  final NodeDescriptor getNodeDescriptor() {
    return overlay.getNodeDescriptor();
  }

  final void sendEvent(UUID id, Event<? extends Serializable> event, String initialVar, Set<String> lockReleaseNodes) {
    final EventPacket pkt = new EventPacket(event, id, initialVar);
    pkt.setLockReleaseNodes(lockReleaseNodes);
    sendToServer(EventPacket.subject, pkt);
  }

  final void sendSubscription(Subscription sub) {
    final SubscriptionPacket pkt = new SubscriptionPacket(sub, SubType.SUB);
    sendToServer(SubscriptionPacket.subject, pkt);
  }

  final void sendUnsubscription(Subscription sub) {
    final SubscriptionPacket pkt = new SubscriptionPacket(sub, SubType.UNSUB);
    sendToServer(SubscriptionPacket.subject, pkt);
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

  final void sendLockRequest(LockRequestPacket req) {
    sendToLockManager(LockRequestPacket.subject, req);
  }

  final void sendLockRelease(LockReleasePacket rel) {
    sendToLockManager(LockReleasePacket.subject, rel);
  }

  private final void sendAdvertisement(Advertisement adv, AdvType advType, Set<Subscription> subs, boolean isPublic) {
    final AdvertisementPacket pkt = subs != null ? new AdvertisementPacket(adv, advType, subs, isPublic) : new AdvertisementPacket(adv, advType, isPublic);
    sendToServer(AdvertisementPacket.subject, pkt);
  }

  final void registerForwarder(PacketForwarder forwarder, String subject) {
    router.setPacketForwarder(subject, forwarder);
  }

  final void stop() {
    overlay.stop();
  }

  private final void sendToServer(String subject, Serializable packet) {
    if (server == null) {
      serverQueue.add(new PacketSubjectPair(subject, packet));
    } else {
      try {
        overlay.send(subject, packet, server);
      } catch (IOException | NotRunningException e) {
        e.printStackTrace();
      }
    }
  }

  private final void sendToLockManager(String subject, Serializable packet) {
    if (server == null) {
      lockManagerQueue.add(new PacketSubjectPair(subject, packet));
    } else {
      try {
        overlay.send(subject, packet, lockManager);
      } catch (IOException | NotRunningException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public final Collection<NodeDescriptor> forwardPacket(String subject, NodeDescriptor sender, Serializable packet, Collection<NodeDescriptor> neighbors, Outbox outbox) {
    if (subject.equals(ServerHelloPacket.subject)) {
      logger.info("Received server hello packet");
      assert packet instanceof ServerHelloPacket;
      server = sender;
      serverQueue.forEach(p -> sendToServer(p.getSubject(), p.getPacket()));
    } else if (subject.equals(LockManagerHelloPacket.subject)) {
      logger.info("Received lock manager hello packet");
      assert packet instanceof LockManagerHelloPacket;
      lockManager = sender;
      lockManagerQueue.forEach(p -> sendToLockManager(p.getSubject(), p.getPacket()));
    } else {
      assert false : subject;
    }
    return new ArrayList<>();
  }

  private class PacketSubjectPair {
    private final String subject;
    private final Serializable packet;

    public PacketSubjectPair(String subject, Serializable packet) {
      super();
      this.subject = subject;
      this.packet = packet;
    }

    final String getSubject() {
      return subject;
    }

    final Serializable getPacket() {
      return packet;
    }

  }

}
