package protopeer.servers.bootstrap;

import protopeer.*;
import protopeer.network.*;


public interface PeerIdentifierGenerator {
	
	public abstract PeerIdentifier generatePeerIdentifier(NetworkAddress address);
	
}
