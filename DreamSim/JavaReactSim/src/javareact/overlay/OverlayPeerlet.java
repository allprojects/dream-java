package javareact.overlay;

import java.util.HashSet;
import java.util.Set;

import javareact.common.packets.overlay.AddBrokerMessage;
import javareact.common.packets.overlay.RemoveBrokerMessage;
import javareact.common.packets.overlay.ReplaceBrokerMessage;
import javareact.experiments.JavaReactConfiguration;

import org.apache.log4j.Logger;

import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.util.RandomnessSource;
import protopeer.util.RandomnessSourceType;

public class OverlayPeerlet extends BasePeerlet implements IOverlayPeerlet {
  private static final Logger logger = Logger.getLogger(OverlayPeerlet.class);
  private Set<NetworkAddress> components;
  private Set<NetworkAddress> brokers;
  private Peer peer;

  @Override
  public void init(Peer peer) {
    this.peer = peer;
    components = new HashSet<NetworkAddress>();
    brokers = new HashSet<NetworkAddress>();
    IOverlayGenerator overlayGenerator = TreeOverlayGenerator.get(JavaReactConfiguration.getSingleton().brokersTopologyType);
    Set<Link> initialTopology = overlayGenerator.generateOverlay(1, JavaReactConfiguration.getSingleton().numberOfBrokers, RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY));
    for (Link l : initialTopology) {
      if (l.getNode1().getId() == peer.getIndexNumber()) {
        addBroker(Experiment.getSingleton().getAddressToBindTo(l.getNode2().getId()));
      } else if (l.getNode2().getId() == peer.getIndexNumber()) {
        addBroker(Experiment.getSingleton().getAddressToBindTo(l.getNode1().getId()));
      }
    }

    IComponentAssociationGenerator associationGenerator = new ComponentAssociationGenerator(JavaReactConfiguration.getSingleton().componentsAssociationType, JavaReactConfiguration.getSingleton().percentageOfPureForwarders);
    Set<Link> componentAssociations = associationGenerator.getAssociation(1, JavaReactConfiguration.getSingleton().numberOfBrokers, JavaReactConfiguration.getSingleton().numberOfBrokers + 1, JavaReactConfiguration.getSingleton().numberOfBrokers + JavaReactConfiguration.getSingleton().numberOfComponents, RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY));
    for (Link l : componentAssociations) {
      if (l.getNode1().getId() == peer.getIndexNumber()) {
        addComponent(Experiment.getSingleton().getAddressToBindTo(l.getNode2().getId()));
      }
    }
  }

  @Override
  public boolean addBroker(NetworkAddress broker) {
    logger.info(peer.getIndexNumber() + ": adding broker " + broker);
    return brokers.add(broker);
  }

  @Override
  public boolean removeBroker(NetworkAddress broker) {
    logger.info(peer.getIndexNumber() + ": removing broker " + broker);
    return brokers.remove(broker);
  }

  @Override
  public boolean addComponent(NetworkAddress component) {
    logger.info(peer.getIndexNumber() + ": adding component " + component);
    return components.add(component);
  }

  @Override
  public boolean removeComponent(NetworkAddress component) {
    logger.info(peer.getIndexNumber() + ": removing component " + component);
    return components.remove(component);
  }

  @Override
  public Set<NetworkAddress> getNeighbors() {
    HashSet<NetworkAddress> neighbors = new HashSet<NetworkAddress>(brokers);
    neighbors.addAll(components);
    return neighbors;
  }

  @Override
  public Set<NetworkAddress> getBrokers() {
    return new HashSet<NetworkAddress>(brokers);
  }

  @Override
  public Set<NetworkAddress> getComponents() {
    return new HashSet<NetworkAddress>(components);
  }

  @Override
  public void handleIncomingMessage(Message message) {
    if (message instanceof AddBrokerMessage) {
      addBroker(((AddBrokerMessage) message).getNewBroker());
    } else if (message instanceof RemoveBrokerMessage) {
      removeBroker(((RemoveBrokerMessage) message).getExistingBroker());
    } else if (message instanceof ReplaceBrokerMessage) {
      addBroker(((ReplaceBrokerMessage) message).getNewBroker());
      removeBroker(((ReplaceBrokerMessage) message).getExistingBroker());
    }
  }
}
