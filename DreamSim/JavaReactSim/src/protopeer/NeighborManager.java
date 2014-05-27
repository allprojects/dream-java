package protopeer;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.*;

import protopeer.network.*;

/**
 * A peerlet that manages the set of neighbors of the peer. Neighbors can be
 * added or removed, iterated over or retrieved either by their peer ID or their
 * network address. The <code>NeighborManager</code> maintains a set of
 * <code>NeighborChangeListener</code>s and calls them on neighbor set
 * changes.
 * 
 */
public class NeighborManager extends BasePeerlet {

	private static final Logger logger = Logger.getLogger(NeighborManager.class);

	private CopyOnWriteArrayList<NeighborChangeListener> neighborChangeListeners = new CopyOnWriteArrayList<NeighborChangeListener>();

	private CopyOnWriteArrayList<Finger> neighbors = new CopyOnWriteArrayList<Finger>();

	private ConcurrentHashMap<PeerIdentifier, Finger> id2neighborMap = new ConcurrentHashMap<PeerIdentifier, Finger>();

	private ConcurrentHashMap<NetworkAddress, Finger> address2neighborMap = new ConcurrentHashMap<NetworkAddress, Finger>();
	
	private boolean notifyNeighborsOnStop = true;

	public void addNeighborChangeListener(NeighborChangeListener listener) {
		neighborChangeListeners.add(listener);
	}

	public void removeNeighborChangeListener(NeighborChangeListener listener) {
		neighborChangeListeners.remove(listener);
	}

	private void fireNeighborAdded(Finger neighbor) {
		for (NeighborChangeListener listener : neighborChangeListeners) {
			listener.neighborAdded(neighbor);
		}
	}

	private void fireNeighborRemoved(Finger neighbor) {
		for (NeighborChangeListener listener : neighborChangeListeners) {
			listener.neighborRemoved(neighbor);
		}
	}

	private void fireNeighborChangedIdentifier(Finger oldFinger, Finger newFinger) {
		for (NeighborChangeListener listener : neighborChangeListeners) {
			listener.neighborChangedIdentifier(oldFinger, newFinger);
		}
	}

	private boolean addFingerToDataStructures(Finger neighbor) {
		synchronized (neighbors) {
			if (isNeighbor(neighbor)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Neighbor exists already: " + neighbor);
				}
				return false;
			}

			neighbors.add(neighbor);
			id2neighborMap.put(neighbor.getIdentifier(), neighbor);
			address2neighborMap.put(neighbor.getNetworkAddress(), neighbor);
			return true;
		}
	}

	/**
	 * Adds the <code>neighbor</code> to the neighbor set, notifies
	 * <code>NeighborChangeListener</code>s.
	 * 
	 * @param neighbor
	 */
	public void addNeighbor(Finger neighbor) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding neighbor: " + neighbor);
		}
		if (addFingerToDataStructures(neighbor)) {
			fireNeighborAdded(neighbor);
		}
	}

	private boolean removeFingerFromDataStructures(Finger neighbor) {
		synchronized (neighbors) {
			if (!isNeighbor(neighbor)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Neighbor does not exist: " + neighbor);
				}
				return false;
			}

			neighbors.remove(neighbor);
			id2neighborMap.remove(neighbor.getIdentifier());
			address2neighborMap.remove(neighbor.getNetworkAddress());
			return true;
		}
	}

	/**
	 * Remove the <code>neighbor</code> to the neighbor set, notifies
	 * <code>NeighborChangeListener</code>s.
	 * 
	 * @param neighbor
	 * @return true if removal was successful
	 */
	public void removeNeighbor(Finger neighbor) {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing neighbor: " + neighbor);
		}
		if (removeFingerFromDataStructures(neighbor)) {
			fireNeighborRemoved(neighbor);
		}
	}

	/**
	 * Stops the peerlet, sends a <code>DisconnectNotification</code> to all
	 * the neighbors and removes all the neighbors
	 */
	@Override
	public void stop() {
		super.stop();
		synchronized (neighbors) {
			// forget all neighbors
			neighbors.clear();
			id2neighborMap.clear();
			address2neighborMap.clear();
		}
	}

	/**
	 * Returns the neighbor set. Warning: the return collection may change after
	 * <code>getNeighbors()</code> returns.
	 * 
	 * @return
	 */
	public Collection<Finger> getNeighbors() {
		return Collections.unmodifiableCollection(neighbors);
	}

	/**
	 * Returns the set of neighbor's addresses.
	 * 
	 * @return
	 */
	public Collection<NetworkAddress> getNeighborAddresses() {
		LinkedList<NetworkAddress> addresses = new LinkedList<NetworkAddress>();
		synchronized (neighbors) {
			for (Finger neighbor : getNeighbors()) {
				addresses.add(neighbor.getNetworkAddress());
			}
		}
		return addresses;
	}

	/**
	 * Replaces the <code>oldFinger</code> neighbor with a
	 * <code>newFinger</code>, notifies <code>NeighborChangeListener</code>s.
	 * This operation is thread-safe and atomic.
	 * 
	 * @param oldFinger
	 * @param newFinger
	 */
	public void updateFinger(Finger oldFinger, Finger newFinger) {
		synchronized (neighbors) {
			removeFingerFromDataStructures(oldFinger);
			addFingerToDataStructures(newFinger);
			fireNeighborChangedIdentifier(oldFinger, newFinger);
		}
	}

	public Finger getNeighborByNetworkAddress(NetworkAddress networkAddress) {
		return address2neighborMap.get(networkAddress);
	}

	public Finger getNeighborByIdentifier(PeerIdentifier neighborID) {
		return id2neighborMap.get(neighborID);
	}

	public boolean isNeighbor(Finger neighbor) {
		return neighbors.contains(neighbor);
	}

	public boolean isNeighbor(NetworkAddress neighborAddress) {
		return address2neighborMap.containsKey(neighborAddress);
	}

	public boolean isNeighbor(PeerIdentifier neighborID) {
		return id2neighborMap.containsKey(neighborID);
	}

	public int getNumNeighbors() {
		return neighbors.size();
	}

	public Collection<Finger> getNeighborsByNetworkAddresses(Collection<NetworkAddress> networkAddresses) {
		LinkedList<Finger> neighbors = new LinkedList<Finger>();
		for (NetworkAddress networkAddress : networkAddresses) {
			Finger finger = getNeighborByNetworkAddress(networkAddress);
			if (finger != null) {
				neighbors.add(finger);
			}
		}
		return neighbors;
	}

	public Collection<Finger> getNeighborsByPeerIdentifiers(Collection<PeerIdentifier> peerIdentifiers) {
		LinkedList<Finger> neighbors = new LinkedList<Finger>();
		for (PeerIdentifier peerIdentifier : peerIdentifiers) {
			Finger finger = getNeighborByIdentifier(peerIdentifier);
			if (finger != null) {
				neighbors.add(finger);
			}
		}
		return neighbors;
	}

	@Override
	public void networkExceptionHappened(NetworkAddress remoteAddress, Message message, Throwable cause) {
		//remove the neighbor automatically if the exception is not recoverable
		if (cause instanceof IrrecoverableNetworkException) {
			if (remoteAddress != null) {
				Finger neighbor = getNeighborByNetworkAddress(remoteAddress);
				if (neighbor != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("exception happened, removing neighbor: " + neighbor + " myID: "
								+ getPeer().getIdentifier() + ", cause: " + cause);
					}
					removeNeighbor(neighbor);
				}
			}
		}
	}
	
	public void setNotifyNeighborsOnStop(boolean notifyOnStop) {
		this.notifyNeighborsOnStop = notifyOnStop;
	}
	
	public boolean isNotifyNeighborsOnStop() {
		return notifyNeighborsOnStop;
	}

}
