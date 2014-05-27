package protopeer;

public interface PeerFactory {
	
	/**
	 * Creates a peer within an experiment.
	 * 
	 * @param peerIndex
	 *            the index of the created peer
	 * @param experiment           
	 *            the experiment to create the peer for
	 */	
	public Peer createPeer(int peerIndex, Experiment experiment);
	
}
