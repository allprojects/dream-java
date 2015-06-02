package protopeer.servers.bootstrap;

import protopeer.*;
import protopeer.network.*;

/**
 * The ack for the <code>ConnectionRequest</code>.
 * 
 *
 */
public class ConnectionResponse extends Message {

	private NetworkAddress responderNetworkAddress;

	private PeerIdentifier responderIdentifier;
	
	private int connectionRequestID;


	public int getConnectionRequestID() {
		return connectionRequestID;
	}

	public ConnectionResponse(NetworkAddress responderNetworkAddress,
			PeerIdentifier responderIdentifier, int connectionRequestID) {
		this.responderNetworkAddress = responderNetworkAddress;
		this.responderIdentifier = responderIdentifier;
		this.connectionRequestID=connectionRequestID;
	}

	public PeerIdentifier getResponderIdentifier() {
		return responderIdentifier;
	}

	public NetworkAddress getResponderNetworkAddress() {
		return responderNetworkAddress;
	}

	public String toString() {
		return "ConnectionResponse(" + responderNetworkAddress + ", "
				+ responderIdentifier + ")";
	}

	public ConnectionResponse clone() {
		ConnectionResponse twin = (ConnectionResponse) super.clone();
		twin.responderIdentifier = this.responderIdentifier.clone();
		twin.responderNetworkAddress = this.responderNetworkAddress.clone();
		return twin;
	}
}
