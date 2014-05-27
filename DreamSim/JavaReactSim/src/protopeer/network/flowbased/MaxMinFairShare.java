package protopeer.network.flowbased;

import java.util.*;

import protopeer.util.quantities.*;

/**
 * Allocates the bandwidth according to the Max-Min-Fair-Share principle, i.e., 
 * no connection can increase its bandwidth without decreasing the bandwidth of 
 * another connection.
 * 
 * This algorithm does not make any assumptions on the underlaying topology. 
 * In other words, it can be used with every kind (subclass) of <code>Topology</code>. 
 * However, it is rather slow because it recalculates the bandwidths of all connection 
 * every time.
 *
 * Still experimental status, not tested in depth, some refactoring necessary for 
 * better readability.
 * 
 */
public class MaxMinFairShare extends BandwidthAllocationAlgorithm {

	/**
	 * Constructor
	 * 
	 * All subclasses of topology can be used here.
	 * 
	 * @param topology
	 */
	public MaxMinFairShare(Topology topology) {
		super(topology);
	}
	
	@Override
	public Map<Connection, Bandwidth> connectionAdded(Connection newConnection,
			Set<Connection> activeConnections) {
		
		return calculateRates(activeConnections);
	}

	@Override
	public Map<Connection, Bandwidth> connectionTerminated(Connection removedConnection,
			Set<Connection> activeConnections) {
		
		return calculateRates(activeConnections);
	}
	
	
	/**
	 * Adjusts the rates of the connections according to the
	 * Max-Min-Fair-Share algorithm.
	 * 
	 * @param activeConnections all active connections in the network
	 */
	private Map<Connection, Bandwidth> calculateRates(Set<Connection> activeConnections) {

		/*
		 * Initialization
		 */
		Map<Connection, Bandwidth> newTransmissionRates = new LinkedHashMap<Connection, Bandwidth>();
		
		// the map contains all links which carry a connection and the 
		// corresponding set of connections
		Map<Link, Set<Connection>> unassignedConMap = getConnectionMap(getTopology().getAllLinks());

		Set<Link> linksWithSpareCapacity = new LinkedHashSet<Link>(unassignedConMap.keySet());
		
		
		// create a map with the already assigned rates to each link
		Map<Link, Bandwidth> assignedRates = new LinkedHashMap<Link, Bandwidth>();
		for (Link link : linksWithSpareCapacity) {
			assignedRates.put(link, Bandwidth.ZERO);
		}
		
		// get all the connection for which the rate will be assigned
		Set<Connection> unassignedConnections = new LinkedHashSet<Connection>(
				activeConnections);
		

		while (!unassignedConnections.isEmpty()) {

			/*
			 *  Identify the bottleneck link, 
			 *  i.e., the link with the smallest fair rate for its connection 
			 *  which have not yet been assigned a rate.
			 */
			
			Bandwidth minFairRate = Bandwidth.inBitPerSecond(Double.POSITIVE_INFINITY);
			Set<Connection> bottleneckConnections = new LinkedHashSet<Connection>(0);
			
			Link bottleneckLink = null;
			for (Link link : linksWithSpareCapacity) {
				Bandwidth assignedRate = assignedRates.get(link);
				Bandwidth remainingCap = link.getCapacity().subtract(assignedRate);
				Set<Connection> unassigned = unassignedConMap.get(link);
				Bandwidth remFairRate = remainingCap.divideBy(unassigned.size());
				if (remFairRate.isLowerThan(minFairRate)) {
					minFairRate = remFairRate;
					bottleneckConnections = unassigned;
					bottleneckLink = link;
				}
			}

			// the total capacity of this link will be assigned in this step
			linksWithSpareCapacity.remove(bottleneckLink); 
			
			
			/*
			 * Assign this minFairRate to all unassigned connections of the
			 * corresponding link and notify the other links of the connections
			 */
			
			for (Connection con : new LinkedHashSet<Connection>(bottleneckConnections)) {
				// set the rate of the connection
				newTransmissionRates.put(con, minFairRate);
				unassignedConnections.remove(con);
				
				// adjust already assigend rates and the unassigned connections
				// on all links which this connection uses
				for (Link link : con.getLinks()) {
					Bandwidth assignedRate = assignedRates.get(link);
					assignedRate = assignedRate.add(minFairRate);
					assignedRates.put(link, assignedRate);
					unassignedConMap.get(link).remove(con);
				}
			}
		}
		return newTransmissionRates;
	}
	
	/**
	 * Returns a map containing the set of all connections for every
	 * link in the set <code>links</code>. If a link carries no connection,
	 * the link is not contained in the map.
	 * 
	 * @param links
	 * @return
	 */
	private Map<Link, Set<Connection>> getConnectionMap(LinkedHashSet<Link> links) {
		LinkedHashMap<Link, Set<Connection>> result = new LinkedHashMap<Link, Set<Connection>>();
		for (Link link : links) {
			if (link.getConnections().size() > 0 ) {
				result.put(link, new LinkedHashSet<Connection>(link.getConnections()));
			}
		}
		return result;
	}

}
