package protopeer.servers.bootstrap;

import protopeer.*;
import protopeer.network.*;

/**
 * 
 * Used in several protocols for requesting connections between the peers.    
 *
 */
public class ConnectionRequest extends Message {

	private NetworkAddress requestorAddress;

	private PeerIdentifier requestorIdentifier;
	
	private int connectionRequestID;	
	
	private static int nextConnectionRequestID=1;

	public ConnectionRequest(NetworkAddress requestorAddress,
			PeerIdentifier requestorIdentifier) {
		this.requestorAddress = requestorAddress;		
		this.requestorIdentifier = requestorIdentifier;
		this.connectionRequestID=nextConnectionRequestID++;
	}

	public NetworkAddress getRequestorAddress() {
		return requestorAddress;
	}

	public PeerIdentifier getRequestOriginIdentifer() {
		return requestorIdentifier;
	}

	public String toString() {
		return "ConnectionRequest(" + requestorAddress + ","
				+ requestorIdentifier + ")";
	}
	
	public ConnectionRequest clone()
	{
		ConnectionRequest twin=(ConnectionRequest)super.clone();
		twin.requestorAddress=this.requestorAddress.clone();
		twin.requestorIdentifier=this.requestorIdentifier.clone();
		return twin;
	}


	public int getConnectionRequestID() {
		return connectionRequestID;
	}
	
}
