package javareact.overlay;

import java.util.Set;

import protopeer.network.NetworkAddress;

/**
 * This is an interface for an Overlay Manager Peerlet.
 * The purpose of implementors of this interface is to give access to the 
 * list of neighbor brokers and components.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 *
 */
public interface IOverlayPeerlet {
	
	/**
	 * Add a broker to the the local view of the overlay of this broker.
	 * 
	 * @param broker New broker to add
	 * @return true if the broker has been added, false if it is already present
	 */
	public boolean addBroker(NetworkAddress broker);
	
	/**
	 * Removes a broker from the local view of the overlay of this broker.
	 * 
	 * @param broker Existing broker to be removed
	 * @return true if the broker has been removed, false if it is not present
	 */
	public boolean removeBroker(NetworkAddress broker);
	
	/**
	 * Add a component to the the local view of the overlay of this broker.
	 * 
	 * @param component New component to add
	 * @return true if the broker has been added, false if it is already present
	 */
	public boolean addComponent(NetworkAddress component);
	
	/**
	 * Removes a component from the local view of the overlay of this broker.
	 * 
	 * @param component Existing component to be removed
	 * @return true if the broker has been removed, false if it is not present
	 */
	public boolean removeComponent(NetworkAddress component);
	
	/**
	 * Get the set of broker and component neighbors of this broker.
	 * 
	 * @return Set of broker and component neighbors
	 */
	public Set<NetworkAddress> getNeighbors();
	
	/**
	 * Get the set of broker neighbors of this broker.
	 * 
	 * @return Set of broker neighbors
	 */
	public Set<NetworkAddress> getBrokers();
	
	/**
	 * Get the set of component neighbors of this broker.
	 * 
	 * @return Set of component neighbors
	 */
	public Set<NetworkAddress> getComponents();
	
}
