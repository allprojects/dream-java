package protopeer.network.flowbased.accessbottleneck;

import java.util.*;

import protopeer.network.*;
import protopeer.network.flowbased.*;

/**
 * This type of topology models a network where only the uplink and
 * downlink of the peers can be the bottleneck of the connection.
 * It assumes that the core network is overdimensioned.
 * 
 */
public class AccessBottleneckTopology extends Topology {

	
	/**
	 * Holds a mapping between a source address and the
	 * corresponding uplink
	 */
	private Map<NetworkAddress,Link> uplinkMap;

	/**
	 * Holds a mapping between a destination address and the
	 * corresponding downlink
	 */
	private Map<NetworkAddress,Link> downlinkMap;
	
	/**
	 * Constructor
	 */
	public AccessBottleneckTopology() {
		uplinkMap = new LinkedHashMap<NetworkAddress, Link>();
		downlinkMap = new LinkedHashMap<NetworkAddress, Link>();
	}
	
	/**
	 * Adds the link <code>link</code> to the topology which servers
	 * as uplink to the node with address <code>address</code>.
	 * 
	 * @param address
	 * @param link
	 */
	public void addUplink(NetworkAddress address, Link link) {
		uplinkMap.put(address, link);
		super.addLink(link);
	}

	/**
	 * Adds the link <code>link</code> to the topology which servers
	 * as downlink to the node with address <code>address</code>.
	 * 
	 * @param address
	 * @param link
	 */
	public void addDownlink(NetworkAddress address, Link link) {
		downlinkMap.put(address, link);
		super.addLink(link);
	}
	
	/**
	 * @return a list containing the uplink of the source and 
	 *         the downlink of the destination
	 */
	@Override
	public List<Link> getLinks(NetworkAddress source, NetworkAddress destination) {
		List<Link> links = new ArrayList<Link>();
		links.add(getUplink(source));		
		links.add(getDownlink(destination));
		return links;
	}
	
	/**
	 * @return the uplink of the peer at <code>address</code>
	 */
	public Link getUplink(NetworkAddress address) {
		return uplinkMap.get(address);
	}

	/**
	 * @return the downlink of the peer at <code>address</code>
	 */
	public Link getDownlink(NetworkAddress address) {
		return downlinkMap.get(address);
	}

}
