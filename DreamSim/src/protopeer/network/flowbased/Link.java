package protopeer.network.flowbased;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import protopeer.util.quantities.Bandwidth;
import protopeer.util.quantities.Time;


/**
 * Up to now, a link only consists of a capacity and a set 
 * of connections which currently use this link.
 * 
 */
public class Link {
	
	/**
	 * Contains all active connections using this link.
	 */
	private Set<Connection> connections;

	/**
	 * Capacity of the link
	 */
	private Bandwidth capacity;
	
	/**
	 * Propagation delay of the link
	 */
	private Time delay = Time.ZERO;
	
	/**
	 * Contains the listeners that are notified when a connection is added
	 * to or removed from this link 
	 */
	private List<ILinkListener> listeners;
	
	/**
	 * Can be used for debugging to get a string representation of this link
	 */
	private String description;

	/**
	 * Constructor
	 */
	public Link() {
		connections = new LinkedHashSet<Connection>();
		listeners = new CopyOnWriteArrayList<ILinkListener>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param capacity capacity of the link
	 */
	public Link(Bandwidth capacity) {
		this();
		setCapacity(capacity);
	}

	/**
	 * 
	 * @return the capacity of the link
	 */
	public Bandwidth getCapacity() {
		return capacity;
	}

	/**
	 * Sets the capacity of the link to <code>capacity</code>
	 * @param capacity capacity of the link
	 */
	public void setCapacity(Bandwidth capacity) {
		this.capacity = capacity;
	}
	
	/**
	 * 
	 * @return the propagation delay of the link
	 */
	public Time getDelay() {
		return delay;
	}

	/**
	 * Sets the propagation delay of the link
	 * @param delay propagation delay of the link
	 */
	public void setDelay(Time delay) {
		this.delay = delay;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public void addConnection(Connection connection) {
		connections.add(connection);
		for (ILinkListener listener : listeners) {
			listener.addedConnection(connection);
		}
	}
	
	public void removeConnection(Connection connection) {
		connections.remove(connection);
		for (ILinkListener listener : listeners) {
			listener.removedConnection(connection);
		}
	}
	
	public Set<Connection> getConnections() {
		return connections;
	}
	
	public void addLinkListener(ILinkListener listener) {
		this.listeners.add(listener);
	}

	public void removeLinkListener(ILinkListener listener) {
		this.listeners.remove(listener);
	}

}
