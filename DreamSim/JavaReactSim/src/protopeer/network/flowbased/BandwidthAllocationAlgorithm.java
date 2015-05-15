package protopeer.network.flowbased;

import java.util.*;

import protopeer.util.quantities.*;

/**
 * Abstract base class for bandwidth allocation algorithms. 
 * 
 * The strategy pattern is used. Different subclasses can implement
 * different allocation strategies. The context, i.e. the network
 * model, is passed as a parameter to the abstract methods.  
 * 
 * Basic structure: Every subclass needs to implement the methods
 * <code>connectionAdded()</code> and <code>connectionTerminated()</code>.
 * The corresponding method is called after a new connection is 
 * established or after an existing connection is removed. The two methods 
 * need to calculate the bandwidth of some or all connections in the 
 * network and return the new bandwidth of at least those
 * connections which have changed. The return-format is a map
 * which contains the connection as key and the new bandwidth 
 * as value. The map must contain all connections whose
 * bandwidth has changed, but it also may contain all connections.
 * 
 * There's a tight connection between the allocation algorithm and the 
 * network topology. Hence, the allocation algorithm has to fit to the
 * given topology. This should be ensured by the constructor of the subclasses
 * which should accept only those topologies to which the algorithm applies.
 * 
 */
public abstract class BandwidthAllocationAlgorithm {
	
	/**
	 * The topology on which the allocation algorithm operates
	 */
	private Topology topology;
	
	/**
	 * Constructor. Ensures that a <code>Topology</code> is 
	 * given when the object is created.
	 * 
	 * @param topology the topology on which the allocation algorithm operates
	 */
	public BandwidthAllocationAlgorithm(Topology topology) {
		this.topology = topology;
	}
	
	/**
	 * May be overwritten by subclasses which cast the topology
	 * object to the concrete class they deal with
	 * 
	 * @return the topology on which the allocation algorithm operates
	 */
	public Topology getTopology() {
		return topology;
	}
	
	/**
	 * This abstract method is called after <code>newConnection</code> is
	 * established in the network.
	 * 
	 * Subclasses need to recalculate the available bandwidth for the 
	 * connections and call <code>connection.adjustRate(newRate)</code> to set
	 * the new available rate for a connection.
	 *  
	 * @param newConnection just estalished connection
	 * @param activeConnections set of active connections, 
	 *            does already contain newConnection
	 * @return the new rates of the changed or all connection.
	 */
	public abstract Map<Connection, Bandwidth> connectionAdded(
			Connection newConnection, Set<Connection> activeConnections);

	/**
	 * This abstract method is called after <code>removedConnection</code> is
	 * removed from the network.
	 * 
 	 * Subclasses need to recalculate the available bandwidth for the 
	 * connections and call <code>connection.adjustRate(newRate)</code> to set
	 * the new available rate for a connection.
	 * 
	 * @param removedConnection just removed connection
	 * @param activeConnections set of active connections, does not contain removedConnection
	 * 
	 * @return the new rates of the changed or all connection.
	 */
	public abstract Map<Connection, Bandwidth> connectionTerminated(
			Connection removedConnection, 
			Set<Connection> activeConnections);
	
}
