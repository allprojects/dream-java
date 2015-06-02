package protopeer;

import protopeer.network.*;

/**
 * This is a convenience class that provides the default implementation of the
 * <code>Peerlet</code> interface. All the <code>Peerlet</code> methods have
 * an empty body.
 * 
 */

public class BasePeerlet implements Peerlet {

	private Peer peer;

	/**
	 * 
	 * @return the peer which this <code>Peerlet</code> is a memeber of
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer which this <code>Peerlet</code> is a member of
	 * 
	 */
	public void init(Peer peer) {
		this.peer = peer;
	}

	public void start() {
	}

	public void stop() {
	}

	public void handleIncomingMessage(Message message) {

	}

	public void handleOutgoingMessage(Message message) {

	}

	public void networkExceptionHappened(NetworkAddress remoteAddress, Message message, Throwable cause) {

	}

	public void messageSent(Message message) {

	}

}
