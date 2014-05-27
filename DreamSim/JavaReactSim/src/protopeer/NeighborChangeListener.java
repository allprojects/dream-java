package protopeer;

/**
 * Receives callback whenever any changes in the set of the peer's neighbors occur.
 * 
 *
 */
public interface NeighborChangeListener {
	
	/**
	 * Called when a neighbor is added.
	 * @param neighbor the newly added neighbor
	 */
	public abstract void neighborAdded(Finger neighbor);

	/**
	 * Called when a neighbor is removed.
	 * @param neighbor the removed neighbor
	 */
	public abstract void neighborRemoved(Finger neighbor);

	/**
	 * Called when a neighbor's address or peerID changes
	 * @param oldFinger 
	 * @param newFinger
	 */
	public abstract void neighborChangedIdentifier(Finger oldFinger, Finger newFinger);

}
