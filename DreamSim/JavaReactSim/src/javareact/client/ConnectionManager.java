package javareact.client;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.EventPacket;
import javareact.common.packets.SubscriptionPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.SubType;
import javareact.common.packets.content.Subscription;
import javareact.experiments.JavaReactConfiguration;
import javareact.overlay.ComponentAssociationGenerator;
import javareact.overlay.IComponentAssociationGenerator;
import javareact.overlay.Link;
import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.util.RandomnessSource;
import protopeer.util.RandomnessSourceType;

class ConnectionManager extends BasePeerlet {
  private NetworkAddress broker;
  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  @Override
  public void init(Peer peer) {
    super.init(peer);
    IComponentAssociationGenerator associationGenerator = new ComponentAssociationGenerator(JavaReactConfiguration.getSingleton().componentsAssociationType, JavaReactConfiguration.getSingleton().percentageOfPureForwarders);
    Set<Link> componentAssociations = associationGenerator.getAssociation(1, JavaReactConfiguration.getSingleton().numberOfBrokers, JavaReactConfiguration.getSingleton().numberOfBrokers + 1, JavaReactConfiguration.getSingleton().numberOfBrokers + JavaReactConfiguration.getSingleton().numberOfComponents, RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY));
    for (Link l : componentAssociations) {
      if (l.getNode2().getId() == peer.getIndexNumber()) {
        broker = Experiment.getSingleton().getAddressToBindTo(l.getNode1().getId());
        logger.fine("Associating component " + peer.getIndexNumber() + " to " + broker);
        break;
      }
    }
    if (broker == null) {
      logger.warning("broker is null");
    }
  }

  final void sendEvent(UUID id, Event event, Set<String> computedFrom, Set<String> finalExpressions, double timestamp, boolean approvedByTokenService) {
    EventPacket pkt = new EventPacket(event, id, computedFrom, timestamp, approvedByTokenService);
    for (String finalExpression : finalExpressions) {
      pkt.addFinalExpression(finalExpression);
    }
    send(pkt);
  }

  final void sendSubscription(Set<Subscription> subs) {
    SubscriptionPacket pkt = new SubscriptionPacket(subs, SubType.SUB);
    send(pkt);
  }

  final void sendUnsubscription(Set<Subscription> subs) {
    SubscriptionPacket pkt = new SubscriptionPacket(subs, SubType.UNSUB);
    send(pkt);
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
    AdvertisementPacket pkt = (subs != null) ? new AdvertisementPacket(adv, advType, subs, isPublic) : new AdvertisementPacket(adv, advType, isPublic);
    send(pkt);
  }

  private final void send(Message packet) {
    getPeer().sendMessage(broker, packet);
  }

}
