package protopeer.servers.bootstrap;

import java.util.*;

import protopeer.*;


public interface TopologyGenerator {

	public int getMinimalNumberOfPeers();

	public Set<Finger> getInitialNeighbors(Collection<Finger> knownFingers, PeerIdentifier peerIdentifier, boolean coreNode);

}
