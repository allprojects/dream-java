package dream.overlay;

import java.util.Set;

/**
 * This is an interface for classes that implement associations between brokers
 * and components.
 *
 * @author Daniel Dubois <daniel@dubois.it>
 */
public interface IClientAssociationGenerator {

  /**
   * Get a list of associations between brokers and components.
   */
  public Set<Link> getAssociation();

  /**
   * Reset the association.
   */
  public void clean();

}
