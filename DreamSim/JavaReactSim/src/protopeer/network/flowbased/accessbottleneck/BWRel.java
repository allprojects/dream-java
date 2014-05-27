package protopeer.network.flowbased.accessbottleneck;

import protopeer.network.flowbased.*;
import protopeer.util.quantities.*;

/**
 * Java portation of the BWRel class in the original C++ implementation
 * from (https://minerva.netgroup.uniroma2.it/svn/p2p/opss/trunk).
 * 
 * Here, it directly corresponds to the class <code>Connection</code>.
 */
public class BWRel {
	
	/**
	 * Holds the next id to assigned to a new object of this class
	 */
	private static long IDCounter = 0;

	/**
	 * Every new <code>BWRel</code> is assigned a unique id
	 * which is used for comparison purpose.
	 */
	private long id;

	/**
	 * This is the corresponding <code>Connection</code> object to this
	 * BWRel (as it is called in original implementation).
	 */
	private Connection connection;
	
	private double rate;
	
	/**
	 * Maintains the status of this BWRel
	 */
	private boolean frozen;
	
	private BWNode server;
	private BWNode client;
	
	

	public BWRel(Connection connection, BWNode server, BWNode client) {
		this.rate = Bandwidth.inBitPerMillisecond(connection.getRate());
		this.connection = connection;
		this.server = server;
		this.client = client;
		frozen = false;
		id = IDCounter++;
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the status of this connection to not frozen
	 */
	public void defreeze() {
		frozen = false;
	}
	
	/**
	 * Freezes this connection, i.e.
	 *  - sets <code>frozen = true</code>
	 *  - removes the connenction from the r.b. BWNode
	 *  - adjusts the rate
	 *  - adds this connection to the set of r.b. cons at the r.b. BWNode
	 *  
	 * This method is only called by <code>freeze()</code> of the 
	 * class <code>BWNode</code>.
	 *  
	 * @param newRate
	 * @param lbNode
	 */
	public void freeze(double newRate, BWNode lbNode) {
		frozen = true;
		BWNode rbNode = getOtherNode(lbNode);
		
		rbNode.removeCon(this); // do not adjust the rate while the relation is in rbCons
		
		rate = newRate;
		OriginalImpl.newRates.put(connection, Bandwidth.inBitPerMillisecond(newRate));
		
		rbNode.addRBCon(this);
	}
	
	public double getRate() {
		return rate;
	}
	
	/**
	 * Returns that one of the two nodes of this
	 * connection which is not equal to <code>node</code>
	 * 
	 * @param node
	 * @return the other node
	 */
	public BWNode getOtherNode(BWNode node) {
		return node.equals(server) ? client : server;
	}
	
	/*
	 * Getter and setter ...
	 */
	
	public BWNode getServer() {
		return server;
	}

	public BWNode getClient() {
		return client;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public boolean isFrozen() {
		return frozen;
	}
	
	public String toString() {
		return connection.toString();
	}

}
