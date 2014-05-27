package javareact.overlay;

import java.util.Random;
import java.util.Set;

/**
 * This is an interface for classes that implement associations
 * between brokers and components.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 */
public interface IComponentAssociationGenerator {
	
	/**
	 * Get a list of associations between brokers and components.
	 * 
	 * @param minBroker Lowest broker id
	 * @param maxBroker Highest broker id
	 * @param minComponent Lowest component id (must be larger than maxBroker)
	 * @param maxComponent Highest component id
	 * @param random Randomness source
	 * 
	 * @return List of links that associate brokers to components
	 */
	public Set<Link> getAssociation(int minBroker, int maxBroker, int minComponent, int maxComponent, Random random);
	
}
