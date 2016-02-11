package dream.overlay;

import java.io.Serializable;

/**
 * This class represents a generic Link between two nodes.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 * 
 */
public class Link implements Serializable {

  private static final long serialVersionUID = -8557693370724758574L;

  private final Node node1;
  private final Node node2;

  /**
   * Create a new link representation between node1 and node2. Note: the first endpoint will always have the smallest
   * id, thus if node2 has a lower id than node1 the two nodes will be SWAPPED!
   * 
   * @param node1 First endpoint of the link
   * @param node2 Second endpoint of the link
   */
  public Link(Node node1, Node node2) {

    if (node1.getId() < node2.getId()) {
      this.node1 = node1;
      this.node2 = node2;
    } else {
      this.node2 = node1;
      this.node1 = node2;
    }
  }

  /**
   * Get the first endpoint of this link.
   * 
   * @return the first endpoint of this link
   */
  public Node getNode1() {
    return node1;
  }

  /**
   * Get the second endpoint of this link.
   * 
   * @return the second endpoint of this link
   */
  public Node getNode2() {
    return node2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof Link) {
      Link link = (Link) o;
      return link.getNode1().equals(node1) && link.getNode2().equals(node2);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Integer.MIN_VALUE + node1.getId() + 1000001 * node2.getId();
  }

  @Override
  public String toString() {
    return "(" + node1 + " - " + node2 + ")";
  }

}
