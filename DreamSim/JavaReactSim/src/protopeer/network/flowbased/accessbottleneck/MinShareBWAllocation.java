package protopeer.network.flowbased.accessbottleneck;

import java.util.*;

import protopeer.network.*;
import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

/**
 * Implements the bandwidth allocation algorithm called "min-share" which
 * is presented in the paper "Narses: A Scalable Flow-Based Network Simulator"
 * of TJ Giuli and Mary Baker, February 2008
 * 
 * The algorithm is a simplified version of the min-max-fair-share algorithm
 * and adjusts only the bandwidth of connections which are one hop away. It is no
 * exact calculation but only an approximation. Details can be found in the paper.
 * 
 * The algorithm assumes the bottleneck of the connection is either the uplink or
 * the downlink of the connection, i.e., it assumes that the class or a subclass 
 * of <code>UpAndDownlinkBottleneckTopology</code> is used.
 * 
 */
public class MinShareBWAllocation extends BandwidthAllocationAlgorithm {

	public MinShareBWAllocation(AccessBottleneckTopology topology) {
		super(topology);
	}
	
	/**
	 * Overwrites <code>getTopology()</code> of the superclass and 
	 * casts the topology the class which is used here.
	 * 
	 * @return topology
	 */
	@Override
	public AccessBottleneckTopology getTopology() {
		return (AccessBottleneckTopology) super.getTopology();
	}
	
	@Override
	public Map<Connection, Bandwidth> connectionAdded(Connection newConnection,
			Set<Connection> activeConnections) {

		return adjustRates(newConnection);
	}
	
	@Override
	public Map<Connection, Bandwidth> connectionTerminated(Connection connection,
			Set<Connection> activeConnections) {
		
		return adjustRates(connection);
	}
	
	private Map<Connection, Bandwidth> adjustRates(Connection changedConnection) {

		Map<Connection, Bandwidth> newRates = new LinkedHashMap<Connection, Bandwidth>();
		
		/*
		 *  According to the paper, all the bandwidth of all connections 
		 *  originating at the source and ending at the destination have 
		 *  to be adjusted
		 */
		
		Set<Connection> toAdjust = getAdjacentConnections(changedConnection, getTopology());
		
		for (Connection connection : toAdjust) {
			
			NetworkAddress source = connection.getSourceAddress();
			Link uplink = getTopology().getUplink(source);
			int nrOfOutgoingCons = uplink.getConnections().size();

			NetworkAddress destination = connection.getDestinationAddress();
			Link downlink = getTopology().getDownlink(destination);
			int nrOfIncomingCons = downlink.getConnections().size();
			
			Bandwidth minUplinkRate = uplink.getCapacity().divideBy(nrOfOutgoingCons);
			Bandwidth minDownlinkRate = downlink.getCapacity().divideBy(nrOfIncomingCons); 
			
			Bandwidth newRate = minUplinkRate;
			if (minDownlinkRate.isLowerThan(minUplinkRate)) {
				newRate = minDownlinkRate;
			}
			
			newRates.put(connection, newRate);
		}
		
		return newRates;
	}
	
	private Set<Connection> getAdjacentConnections(
			Connection connection,
			AccessBottleneckTopology topology) {
		
		Set<Connection> adjacentCons = new LinkedHashSet<Connection>();
		
		NetworkAddress source = connection.getSourceAddress();
		Link uplink = topology.getUplink(source);
		adjacentCons.addAll(uplink.getConnections());
		
		NetworkAddress destination = connection.getDestinationAddress();
		Link downlink = topology.getDownlink(destination);
		adjacentCons.addAll(downlink.getConnections());
		
		return adjacentCons;
	}


}
