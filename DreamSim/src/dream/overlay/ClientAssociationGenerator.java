package dream.overlay;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dream.experiments.DreamConfiguration;
import protopeer.util.RandomnessSource;
import protopeer.util.RandomnessSourceType;

/**
 * This class allows the creation of links between brokers and clients.
 *
 * @author Daniel Dubois <daniel@dubois.it>
 */
public class ClientAssociationGenerator implements IClientAssociationGenerator {
  public final int UNIFORM_LOWEST_ID = 1;
  public final int UNIFORM_HIGHEST_ID = 2;
  public final int UNIFORM_ALTERNATE_ID = 3;
  public final int UNIFORM_RANDOM_ID = 4;

  private final int type;
  private final double percentageOfPureForwarders;

  private Set<Link> association;
  private final boolean associationGenerated = false;

  public ClientAssociationGenerator(int type, double percentageOfPureForwarders) {
    if (percentageOfPureForwarders < 0 || percentageOfPureForwarders > 1) {
      throw new IllegalArgumentException("percentageOfPureForwarders is not valid for ComponentAssociationGenerator");
    }
    this.type = type;
    this.percentageOfPureForwarders = percentageOfPureForwarders;
    switch (type) {
    case UNIFORM_LOWEST_ID:
    case UNIFORM_HIGHEST_ID:
    case UNIFORM_ALTERNATE_ID:
    case UNIFORM_RANDOM_ID:
      break;
    default:
      throw new IllegalArgumentException("Type is not valid for ComponentAssociationGenerator");
    }
  }

  @Override
  public Set<Link> getAssociation() {
    if (!associationGenerated) {
      final DreamConfiguration conf = DreamConfiguration.get();
      final int minBroker = 1;
      final int maxBroker = conf.numberOfBrokers;
      final int minClient = maxBroker + 1;
      final int maxClient = minClient + conf.numberOfClients - 1;
      final Random random = RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY);

      final List<Node> brokers = IntStream.rangeClosed(minBroker, maxBroker)//
          .mapToObj(i -> new Node(i))//
          .collect(Collectors.toList());

      final List<Node> clients = IntStream.rangeClosed(minClient, maxClient)//
          .mapToObj(i -> new Node(i))//
          .collect(Collectors.toList());

      int brokersToBeRemoved = new Double(brokers.size() * percentageOfPureForwarders).intValue();
      if (brokers.size() - brokersToBeRemoved > clients.size()) {
        brokersToBeRemoved = brokers.size() - clients.size();
      }
      switch (type) {
      case UNIFORM_LOWEST_ID:
        for (int i = brokers.size() - 1; i >= 0 && brokersToBeRemoved > 0; i--) {
          brokers.remove(i);
          brokersToBeRemoved--;
        }
        break;
      case UNIFORM_RANDOM_ID:
        Collections.shuffle(brokers, random);
      case UNIFORM_HIGHEST_ID:
        while (brokersToBeRemoved > 0) {
          brokers.remove(0);
          brokersToBeRemoved--;
        }
        break;
      case UNIFORM_ALTERNATE_ID:
        while (brokersToBeRemoved > 0) {
          for (int i = brokers.size() - 1; i >= 0 && brokersToBeRemoved > 0; i = i - 2) {
            brokers.remove(i);
            brokersToBeRemoved--;
          }
        }
        break;
      }
      association = getAssociation(brokers, clients);
    }
    return association;
  }

  private Set<Link> getAssociation(final List<Node> brokers, final List<Node> components) {
    final Set<Link> links = new HashSet<Link>();
    int i = 0;
    while (links.size() < components.size()) {
      // TODO: was links.add(new Link(brokers.get(i%components.size()),
      // components.get(i)));
      links.add(new Link(brokers.get(i % brokers.size()), components.get(i)));
      i++;
    }
    return links;
  }

}
