package protopeer.servers.bootstrap;

import protopeer.*;
import protopeer.network.*;

public class BootstrapHello extends Message {
	
	private NetworkAddress networkAddress;

	private PeerIdentifier peerIdentifier;

	public BootstrapHello(NetworkAddress networkAddress, PeerIdentifier peerIdentifier) {
		super();
		this.networkAddress = networkAddress;
		this.peerIdentifier = peerIdentifier;
	}

	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	public PeerIdentifier getPeerIdentifier() {
		return peerIdentifier;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("BootstrapHello(src=");
		buffer.append(networkAddress);
		buffer.append(", srcID=");
		buffer.append(peerIdentifier);
		buffer.append(")");
		return buffer.toString();
	}
}
