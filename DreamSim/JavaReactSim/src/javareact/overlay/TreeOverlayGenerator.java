package javareact.overlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TreeOverlayGenerator implements IOverlayGenerator {
  public static final int LINEAR = 1;
  public static final int STAR = 2;
  public static final int SCALEFREE = 3;

  private final int type;

  private static TreeOverlayGenerator instance = null;
  private final Set<Link> links = new HashSet<Link>();
  private boolean linksGenerated = false;

  public static final TreeOverlayGenerator get(int type) {
    if (instance == null) {
      instance = new TreeOverlayGenerator(type);
    }
    return instance;
  }

  private TreeOverlayGenerator(int type) {
    this.type = type;
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
  public Set<Link> generateOverlay(int nMin, int nMax, Random random) {
    if (!linksGenerated) {
      linksGenerated = true;
      HashSet<Node> nodes = new HashSet<Node>();
      for (int i = nMin; i <= nMax; i++) {
        nodes.add(new Node(i));
      }
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
    for (Node n : nodes) {
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
    for (Node n : nodes) {
      if (n.getId() == 1) {
        firstNode = n;
        break;
      }
    }
    for (Node n : nodes) {
      if (n.equals(firstNode)) {
        continue;
      }
      links.add(new Link(firstNode, n));
    }
    return links;
  }

  private Set<Link> generateScaleFreeOverlay(Set<Node> nodes, Random random) {
    Integer nlinks = nodes.size() - 1;
    if (nodes.size() < 2) {
      return links;
    }
    Integer d = 2 * nlinks / nodes.size();
    List<Node> tmpNodes = new ArrayList<Node>(nodes);
    List<Node> addedNodes = new ArrayList<Node>();
    for (Integer i = 0; i <= d; i++) {
      addedNodes.add(tmpNodes.remove(0));
    }
    for (Integer i = 0; i < d; i++) {
      for (Integer j = i + 1; j <= d; j++) {
        links.add(new Link(addedNodes.get(i), addedNodes.get(j)));
      }
    }
    while (tmpNodes.size() > 0) {
      Node sourceNode = tmpNodes.remove(0);
      Node destinationNode = getCandidateNode(addedNodes, links, random);
      links.add(new Link(sourceNode, destinationNode));
      addedNodes.add(sourceNode);
    }
    return links;
  }

  private Node getCandidateNode(List<Node> tmpNodes, Set<Link> links, Random random) {
    Double p = random.nextDouble();
    Double previous_p = 0.0;
    Double current_p = 0.0;
    for (Integer i = 0; i < tmpNodes.size(); i++) {
      previous_p = current_p;
      current_p += getDegree(tmpNodes.get(i), links) / (2.0 * links.size());
      if ((p >= previous_p) && (p <= current_p)) {
        return tmpNodes.get(i);
      }
    }
    return null;
  }

  private Integer getDegree(Node node, Set<Link> links) {
    Integer degree = 0;
    for (Link l : links) {
      if (l.getNode1().equals(node) || l.getNode2().equals(node)) {
        degree++;
      }
    }
    return degree;
  }

}
