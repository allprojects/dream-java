package dream.overlay;

import java.util.Set;

/**
 * This is the interface for classes able to generating initial topologies.
 *
 * @author Daniel Dubois <daniel@dubois.it>
 *
 */
public interface IOverlayGenerator {

  /**
   * Generates a new random topology with the default number of links. The
   * default number of links depends on the implementing classes.
   *
   * @return a set of links that constitutes the generated topology
   */
  public Set<Link> generateOverlay();
}
