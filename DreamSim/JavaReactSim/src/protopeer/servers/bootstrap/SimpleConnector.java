package protopeer.servers.bootstrap;

import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.network.*;

/**
 * This peerlet implements a simple protocol for maintaining the set of peer
 * neighbors. On <code>start()</code> this peerlet waits for the list of
 * bootstrap peers from the <code>BootstrapClient</code> and sends one
 * <code>ConnectRequest</code> to each of the bootstrap peers. When it
 * receives a <code>ConnectRequest</code> the peerlet adds the sender to the
 * neighbor set and responds back to the sender with a
 * <code>ConnectResponse</code>. When this peerlet receives a
 * <code>ConnectResponse</code> it adds the sender to the neighbor set. When
 * <code>DisconnectNotification</code> is received the sender is removed from
 * the neighbor set.
 * 
 * <strong>Dependencies:</strong> this peerlet requires the <code>BootstrapClient</code> and
 * <code>NeighborManager</code> peerlets.
 * 
 */
public class SimpleConnector extends BasePeerlet {

	private static final Logger logger = Logger.getLogger(SimpleConnector.class);

	private class SimpleConnectorBootListener implements BootstrapClientListener {

		public void bootstrapCompleted(Collection<Finger> bootstrapPeers) {
			// send connect requests to the bootstrap peers and add them to the
			for (Finger newNeighbor : bootstrapPeers) {
				getPeer().sendMessage(newNeighbor.getNetworkAddress(),
						new ConnectionRequest(getPeer().getNetworkAddress(), getPeer().getIdentifier()));
			}
		}

	}

	@Override
	public void init(Peer peer) {
		super.init(peer);
		BootstrapClient bootstrapClient = (BootstrapClient) getPeer().getPeerletOfType(BootstrapClient.class);
		if (bootstrapClient == null) {
			logger.error("SimpleConnector needs the BootstrapClient");
		} else {
			bootstrapClient.addBootstrapClientListener(new SimpleConnectorBootListener());
		}
	}
	
	private NeighborManager getNeighborManager() {
		return (NeighborManager) getPeer().getPeerletOfType(NeighborManager.class);
	}

	protected void deliverConnectionRequest(ConnectionRequest message) {
		if (logger.isDebugEnabled()) {
			logger.debug("received: " +message);
		}
		if (message.getRequestorAddress().equals(getPeer().getNetworkAddress())) {
			logger.warn("connection request from self!");
			return;
		}
		if (getPeer().getIdentifier() == null) {
			logger.warn("peer identifier unknown when receiving ConnectionRequest");
			return;
		}

		Finger newNeighbor = new Finger(message.getRequestorAddress(), message.getRequestOriginIdentifer());
		if (logger.isDebugEnabled()) {
			logger.debug("adding neighbor: " +newNeighbor);
		}
		getNeighborManager().addNeighbor(newNeighbor);

		ConnectionResponse response = new ConnectionResponse(getPeer().getNetworkAddress(), getPeer().getIdentifier(),
				message.getConnectionRequestID());

		// acknowledge the connection request
		getPeer().sendMessage(message.getRequestorAddress(), response);
	}

	protected void deliverConnectionResponse(ConnectionResponse message) {
		if (logger.isDebugEnabled()) {
			logger.debug("received: " +message);
		}
		if (message.getResponderNetworkAddress().equals(getPeer().getNetworkAddress())) {
			logger.warn("connection response from self!");
			return;
		}
		if (!message.getResponderNetworkAddress().equals(message.getSourceAddress())) {
			logger.warn("address mismatch ConnectionResponse, responder: " + message.getResponderNetworkAddress()
					+ " source: " + message.getSourceAddress());
			return;
		}
		Finger newNeighbor = new Finger(message.getResponderNetworkAddress(), message.getResponderIdentifier());
		if (logger.isDebugEnabled()) {
			logger.debug("adding neighbor: " +newNeighbor);
		}
		getNeighborManager().addNeighbor(newNeighbor);
	}
	
	@Override
	public void handleIncomingMessage(Message message) {
		if (message instanceof ConnectionRequest) {
			deliverConnectionRequest((ConnectionRequest) message);
		} else if (message instanceof ConnectionResponse) {
			deliverConnectionResponse((ConnectionResponse) message);
		} 
	}

}
