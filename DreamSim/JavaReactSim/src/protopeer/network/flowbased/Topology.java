package protopeer.network.flowbased;

import java.util.*;

import protopeer.network.*;

/**
 * Holds the topology of the peers and the links and calculates the list
 * of all links which are involved in a connection from <code>source</code>
 * to <code>destination</code>.
 * 
 * Concrete subclasses can implement topologies with different complexities.
 */
public abstract class Topology {
	
	/**
	 * Contains all links in the network. Subclasses are responsible
	 * to call <code>addLink()</code>.
	 */
	private LinkedHashSet<Link> allLinks;
	
	/**
	 * Default constructor.
	 */
	public Topology() {
		allLinks = new LinkedHashSet<Link>();
	}
	
	/**
	 * Returns a set containing all links of the topology
	 * 
	 * @return set of all links of the topology
	 */
	public LinkedHashSet<Link> getAllLinks() {
		return allLinks;
	}
	
	/**
	 * Adds the link <code>link</code> to the set of all
	 * links. 
	 * 
	 * @param link link to be added
	 */
	protected void addLink(Link link) {
		allLinks.add(link);
	}
	
	/**
	 * Returns the list of links involved in a connection from <code>source</code>
	 * to <code>destination</code>. 
	 * 
	 * Subclasses have to take care that 
	 * 1) the list does not contain "null" 
	 * 2) the list contains at least one link
	 * 
	 * @param source source address
	 * @param destination destination address
	 * @return list of all links involved in a connection from 
	 *         <code>source</code> to <code>destination</code>
	 */
	public abstract List<Link> getLinks(NetworkAddress source, NetworkAddress destination); 

}
