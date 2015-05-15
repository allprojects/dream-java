package protopeer.network.flowbased.accessbottleneck;

import java.util.*;

import org.apache.log4j.*;

import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

/**
 * Stub for implementation of the incremental max-min-fair-share algorithm
 * described in "Efficient Simulation of Bandwidth Allocation Dynamics
 * in P2P Networks" by Francesca Lo Piccolo et al.
 * 
 * Original implementation: https://minerva.netgroup.uniroma2.it/svn/p2p/opss/trunk 
 * 
 * It relies on the <code>UpAndDownlinkBottleneckTopology</code> where only
 * the access links can be the bottlenecks of the connections.
 * 
 */
public class IncrementalMaxMinFairShare extends BandwidthAllocationAlgorithm {

	private static final Logger logger = Logger.getLogger(IncrementalMaxMinFairShare.class);
	
	/**
	 * The class <code>BWNode</code> is only a helper class. It is used to keep the
	 * algorithm similar to the original one in C++. Therefore, we need a mapping
	 * between the links (up- and downlinks in our scenario) and the "bandwidth
	 * nodes" (BWNode) in the scenario of the original implementation.
	 */
	private Map<Link, BWNode> bwNodes;
	
	/**
	 * The same as above. A "bandwidth relation" (BWRel) corresponds to a connection
	 * in our scenario. This map contains the mapping.
	 */
	private Map<Connection, BWRel> bwRels;
	
	/**
	 * Both mappings from above have to be initialized when the algorithm is
	 * called the first time. This is recorded in <code>initialized</code>.
	 */
	private boolean initialized = false;
	
	/* Here begins the stuff of the original implementation */

	private OriginalImpl impl;

	
	/**
	 * Constructor.
	 * 
	 * Ensures that this algorithm is only used in conjunction with the
	 * <code>UpAndDownlinkBottleneckTopology</code>
	 * 
	 * @param topology
	 */
	public IncrementalMaxMinFairShare(AccessBottleneckTopology topology) {
		super(topology);
		impl = new OriginalImpl();
	}
	
	public AccessBottleneckTopology getTopology() {
		return (AccessBottleneckTopology) super.getTopology();
	}
	
	private void init() {
		bwNodes = new LinkedHashMap<Link, BWNode>(getTopology().getAllLinks().size());
		for (Link link : getTopology().getAllLinks()) {
			bwNodes.put(link, new BWNode(link));
		}
		bwRels = new LinkedHashMap<Connection, BWRel>();
	}
	
	@Override
	public Map<Connection, Bandwidth> connectionAdded(Connection newConnection,
			Set<Connection> activeConnections) {

		if (!initialized) {
			init();
			initialized = true;
		}

		/*
		 * Get the corresponding BWNodes for that connection 
		 */
		Link uplink = getTopology().getUplink(newConnection.getSourceAddress());
		BWNode sourceNode = bwNodes.get(uplink);
		
		Link downlink = getTopology().getDownlink(newConnection.getDestinationAddress());
		BWNode destNode = bwNodes.get(downlink);

		if (logger.isDebugEnabled()) {
			sourceNode.setDescription("Uplink from " + newConnection.getSourceAddress());
			destNode.setDescription("Downlink to " + newConnection.getDestinationAddress());	
		}
		

		/*
		 * Create a new relation and remember the mapping between 
		 * newConnection and the corresponding BWRel r
		 */
		BWRel r = new BWRel(newConnection, sourceNode, destNode);
		bwRels.put(newConnection, r);
		
		OriginalImpl.newRates.clear();
		
		impl.addBWRel(r);
		
		return OriginalImpl.newRates;
	}

	@Override
	public Map<Connection, Bandwidth> connectionTerminated(Connection removedConnection,
			Set<Connection> activeConnections) {
		
		BWRel r = bwRels.get(removedConnection);
		
		OriginalImpl.newRates.clear();
		
		bwRels.remove(removedConnection);
	
		impl.removeBWRel(r);
		
		return OriginalImpl.newRates;
	}
	

}
