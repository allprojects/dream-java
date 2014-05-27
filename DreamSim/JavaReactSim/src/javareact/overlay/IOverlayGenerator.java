package javareact.overlay;

import java.util.Random;
import java.util.Set;

/**
 * This is the interface for classes able to generating initial topologies.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 *
 */
public interface IOverlayGenerator {
	
	/**
	 * Generates a new random topology with the default number
	 * of links. The default number of links depends on the implementing
	 * classes.
	 * 
	 * @param nMin Minimum id of the generated nodes.
	 * @param nMax Maximum id of the generated nodes.
	 * @param random Pseudo-random values generator.
	 * 
	 * @return a set of links that constitutes the generated topology
	 */
	public Set<Link> generateOverlay(int nMin, int nMax, Random random);
}
