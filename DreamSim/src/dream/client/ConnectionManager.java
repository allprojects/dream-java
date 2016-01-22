package dream.client;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import dream.common.packets.AdvertisementPacket;
import dream.common.packets.EventPacket;
import dream.common.packets.SubscriptionPacket;
import dream.common.packets.content.AdvType;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.SubType;
import dream.common.packets.content.Subscription;
import dream.common.packets.locking.LockReleasePacket;
import dream.common.packets.locking.LockRequestPacket;
import dream.experiments.DreamConfiguration;
import dream.overlay.ClientAssociationGenerator;
import dream.overlay.IClientAssociationGenerator;
import dream.overlay.Link;
import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;

class ConnectionManager extends BasePeerlet {
  private NetworkAddress server = null;
  private final NetworkAddress lockManager = null;
  private DreamConfiguration conf;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  @Override
  public void init(Peer peer) {
    super.init(peer);
    conf = DreamConfiguration.get();
    final IClientAssociationGenerator associationGenerator = new ClientAssociationGenerator(conf.clientsAssociationType, conf.percentageOfPureForwarders);
    final Set<Link> componentAssociations = associationGenerator.getAssociation();
    for (final Link l : componentAssociations) {
      if (l.getNode2().getId() == peer.getIndexNumber()) {
        server = Experiment.getSingleton().getAddressToBindTo(l.getNode1().getId());
        logger.fine("Associating component " + peer.getIndexNumber() + " to " + server);
        break;
      }
    }
    if (server == null) {
      logger.warning("server is null");
    }
  }

  final void sendEvent(UUID id, Event event, String initialVar, double creationTime, Set<String> lockReleaseNodes) {
    final EventPacket pkt = new EventPacket(event, id, creationTime, initialVar);
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

  final void sendAdvertisement(Advertisement adv) {
    sendAdvertisement(adv, AdvType.ADV, null);
  }

  final void sendAdvertisement(Advertisement adv, Set<Subscription> subs) {
    sendAdvertisement(adv, AdvType.ADV, subs);
  }

  final void sendUnadvertisement(Advertisement adv) {
    sendAdvertisement(adv, AdvType.UNADV, null);
  }

  final void sendUnadvertisement(Advertisement adv, Set<Subscription> subs) {
    sendAdvertisement(adv, AdvType.UNADV, subs);
  }

  final void sendLockRequest(LockRequestPacket req) {
    sendToLockManager(LockRequestPacket.subject, req);
  }

  final void sendLockRelease(LockReleasePacket rel) {
    sendToLockManager(LockReleasePacket.subject, rel);
  }

  private final void sendAdvertisement(Advertisement adv, AdvType advType, Set<Subscription> subs) {
    final AdvertisementPacket pkt = subs != null ? new AdvertisementPacket(adv, advType, subs) : new AdvertisementPacket(adv, advType);
    sendToServer(AdvertisementPacket.subject, pkt);
  }

  private final void sendToServer(String subject, Message packet) {
    getPeer().sendMessage(server, packet);
  }

  private final void sendToLockManager(String subject, Message packet) {
    getPeer().sendMessage(lockManager, packet);
  }

}
