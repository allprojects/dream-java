package protopeer.servers.bootstrap;

import protopeer.*;

public interface BootstrapServerListener {
	
	public void peerJoined(Finger finger);
	
	public void corePeersJoined();
	
}
