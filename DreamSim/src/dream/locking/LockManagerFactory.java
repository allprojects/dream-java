package dream.locking;

import dream.measurement.LockManagerMeasurementPeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;

public class LockManagerFactory implements PeerFactory {

	@Override
	public Peer createPeer(int peerIndex, Experiment experiment) {
		final Peer newPeer = new Peer(peerIndex);
		newPeer.addPeerlet(new LockManagerForwarder());
		newPeer.addPeerlet(new LockManagerMeasurementPeerlet());
		return newPeer;
	}

}
