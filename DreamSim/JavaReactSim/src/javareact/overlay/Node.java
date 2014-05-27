package javareact.overlay;

import java.io.Serializable;

/**
 * This is a generic node used to by the Overlay Generator.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 * 
 */
public class Node implements Serializable {

  private static final long serialVersionUID = 1096390051568045115L;
  private final int id;

  /**
   * Creates a generic node with the given id.
   * 
   * @param id Id of the new generic node
   */
  public Node(int id) {
    this.id = id;
  }

  /**
   * Get the id of this generic node.
   * 
   * @return id of this generic node
   */
  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Node) {
      return ((Node) o).getId() == id;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.valueOf(id);
  }

}
