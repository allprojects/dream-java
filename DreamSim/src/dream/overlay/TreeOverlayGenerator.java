package dream.overlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dream.experiments.DreamConfiguration;
import protopeer.util.RandomnessSource;
import protopeer.util.RandomnessSourceType;

public class TreeOverlayGenerator implements IOverlayGenerator {
  public static final int LINEAR = 1;
  public static final int STAR = 2;
  public static final int SCALEFREE = 3;

  private final int type;

  private static TreeOverlayGenerator instance = null;
  private final Set<Link> links = new HashSet<>();
  private boolean linksGenerated = false;

  public static final IOverlayGenerator get() {
    if (instance == null) {
      instance = new TreeOverlayGenerator();
    }
    return instance;
  }

  private TreeOverlayGenerator() {
    this.type = DreamConfiguration.get().brokersTopologyType;
    switch (type) {
    case LINEAR:
    case STAR:
    case SCALEFREE:
      break;
    default:
      throw new IllegalArgumentException("Unknown Tree Topology Type");
    }
  }

  @Override
  public void clean() {
    linksGenerated = false;
    links.clear();
  }

  @Override
  public Set<Link> generateOverlay() {
    final DreamConfiguration conf = DreamConfiguration.get();
    final int brokersMin = 1;
    final int brokersMax = conf.numberOfBrokers;
    final Random random = RandomnessSource.getRandom(RandomnessSourceType.TOPOLOGY);

    if (!linksGenerated) {
      linksGenerated = true;
      final Set<Node> nodes = IntStream.rangeClosed(brokersMin, brokersMax)//
          .mapToObj(Node::new)//
          .collect(Collectors.toSet());

      switch (type) {
      case LINEAR:
        return generateLinearOverlay(nodes, random);
      case STAR:
        return generateStarOverlay(nodes, random);
      case SCALEFREE:
        return generateScaleFreeOverlay(nodes, random);
      }
    }
    return links;
  }

  private Set<Link> generateLinearOverlay(Set<Node> nodes, Random random) {
    Node previousNode = null;
    Node currentNode = null;
    for (final Node n : nodes) {
      previousNode = currentNode;
      currentNode = n;
      if (previousNode != null && currentNode != null) {
        links.add(new Link(previousNode, currentNode));
      }
    }
    return links;
  }

  private Set<Link> generateStarOverlay(Set<Node> nodes, Random random) {
    Node firstNode = null;
    for (final Node n : nodes) {
      if (n.getId() == 1) {
        firstNode = n;
        break;
      }
    }
    for (final Node n : nodes) {
      if (n.equals(firstNode)) {
        continue;
      }
      links.add(new Link(firstNode, n));
    }
    return links;
  }

  private Set<Link> generateScaleFreeOverlay(Set<Node> nodes, Random random) {
    final Integer nlinks = nodes.size() - 1;
    if (nodes.size() < 2) {
      return links;
    }
    final Integer d = 2 * nlinks / nodes.size();
    final List<Node> tmpNodes = new ArrayList<Node>(nodes);
    final List<Node> addedNodes = new ArrayList<Node>();
    for (Integer i = 0; i <= d; i++) {
      addedNodes.add(tmpNodes.remove(0));
    }
    for (Integer i = 0; i < d; i++) {
      for (Integer j = i + 1; j <= d; j++) {
        links.add(new Link(addedNodes.get(i), addedNodes.get(j)));
      }
    }
    while (tmpNodes.size() > 0) {
      final Node sourceNode = tmpNodes.remove(0);
      final Node destinationNode = getCandidateNode(addedNodes, links, random);
      links.add(new Link(sourceNode, destinationNode));
      addedNodes.add(sourceNode);
    }
    return links;
  }

  private Node getCandidateNode(List<Node> tmpNodes, Set<Link> links, Random random) {
    final Double p = random.nextDouble();
    Double previous_p = 0.0;
    Double current_p = 0.0;
    for (Integer i = 0; i < tmpNodes.size(); i++) {
      previous_p = current_p;
      current_p += getDegree(tmpNodes.get(i), links) / (2.0 * links.size());
      if (p >= previous_p && p <= current_p) {
        return tmpNodes.get(i);
      }
    }
    return null;
  }

  private Integer getDegree(Node node, Set<Link> links) {
    Integer degree = 0;
    for (final Link l : links) {
      if (l.getNode1().equals(node) || l.getNode2().equals(node)) {
        degree++;
      }
    }
    return degree;
  }

}
