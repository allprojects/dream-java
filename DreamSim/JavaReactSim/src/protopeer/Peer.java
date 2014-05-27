package protopeer;

import java.util.concurrent.*;

import org.apache.log4j.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.queues.*;
import protopeer.time.*;

/**
 * Represents the peer. Each peer has a <code>PeerIdentifier</code> and
 * maintains a <code>NetworkInterface</code> for sending and receiving messages,
 * a <code>Clock</code> for creating <code>Timer</code>s and a
 * <code>MeasurementLogger</code> for logging the measurements.
 * 
 * The <code>Peer</code> follows the <code>init()</code>, <code>start()</code>,
 * <code>stop()</code>, <code>start()</code>, <code>stop()</code>,... lifecycle.
 * The <code>Peer</code> is composed of <code>Peerlet</code>s. The peerlets are
 * notified about all the networking and peer lifetime events.
 * 
 */
public class Peer implements ExecutionContext {

	private class PeerNetworkListener implements NetworkListener {
		public void messageSent(NetworkInterface networkInterface, NetworkAddress remoteAddress, Message message) {
			if (getState() == PeerState.RUNNING) {
				if (logger.isDebugEnabled()) {
					logger.debug("messageSent() call, message=" + message + ", remoteAddress=" + remoteAddress);
				}

				// tell the peerlets
				fireMessageSent(message);
			} else {
				logger.warn("messageSent() call, message=" + message + " for remote address " + remoteAddress
						+ " while in state " + getState());
			}
		}

		public void messageReceived(NetworkInterface networkInterface, NetworkAddress sourceAddress, Message message) {
			if (getState() == PeerState.RUNNING) {
				if (logger.isDebugEnabled()) {
					logger.debug("Message received: " + message + " source: " + sourceAddress + " on interface: "
							+ networkInterface);
				}

				// tell the peerlets
				fireHandleIncomingMesage(message);
			} else {
				logger.warn("Message " + message + " received from " + sourceAddress + " while in state " + getState());
			}
		}

		public void interfaceDown(NetworkInterface networkInterface) {
			if (logger.isDebugEnabled()) {
				logger.debug("Interface brought down");
			}
			setState(PeerState.STOPPED);
		}

		public void exceptionHappened(NetworkInterface networkInterface, NetworkAddress remoteAddress, Message message,
				Throwable cause) {
			logger.warn("PeerNetworkListener.exceptionHappened, remoteAddress: " + remoteAddress, cause);
			fireNetworkExceptionHappened(remoteAddress, message, cause);
		}

		public void interfaceUp(NetworkInterface networkInterface) {
			setState(PeerState.RUNNING);
			startPeerlets();
		}
	}

	/**
	 * States of the peer in the order in which the transitions occur, left to
	 * right.
	 * 
	 */
	public enum PeerState {
		UNINITIALIZED, INITIALIZED, STARTING, RUNNING, STOPPING, STOPPED,
	}

	private static final Logger logger = Logger.getLogger(Peer.class);

	private PeerIdentifier identifier;

	private PeerState state = PeerState.UNINITIALIZED;

	private CopyOnWriteArrayList<Peerlet> peerlets = new CopyOnWriteArrayList<Peerlet>();

	private NetworkInterfaceFactory networkInterfaceFactory;

	private NetworkInterface networkInterface;

	private NetworkAddress addressToBindTo;

	private Clock clock;

	private MeasurementLogger measurementLogger;

	private MessageQueue outgoingMessageQueue;

	private MessageQueueListener messageQueueListener;

	private final int indexNumber;

	/**
	 * Each peer in the system has a unique index number, both during the
	 * simulation and live deployment and it must be initialied at construction
	 * time.
	 * 
	 * @param indexNumber
	 */
	public Peer(int indexNumber) {
		this.indexNumber = indexNumber;
	}

	private void fireMessageSent(Message message) {
		for (Peerlet peerlet : peerlets) {
			peerlet.messageSent(message);
		}
	}

	private void fireNetworkExceptionHappened(NetworkAddress remoteAddress, Message message, Throwable cause) {
		for (Peerlet peerlet : peerlets) {
			peerlet.networkExceptionHappened(remoteAddress, message, cause);
		}
	}

	/**
	 * Initializes the peer, calls <code>init()</code> on all peerlets managed
	 * by this peer. Sets the peer state to <code>INITIALIZED</code>. Does
	 * <strong>not</strong> create any network interfaces or bind to any
	 * addresses.
	 * 
	 * @param networkInterfaceFactory
	 *            the factory this peer will use to create its network interface
	 *            when <code>start()</code> is called
	 * @param clock
	 *            the clock that this peer will be using
	 * @param addressToBindTo
	 *            the network address this peer will bind to when
	 *            <code>start()</code> is called
	 */
	public void init(NetworkInterfaceFactory networkInterfaceFactory, Clock clock, NetworkAddress addressToBindTo) {
		this.measurementLogger = new MeasurementLogger(clock, new MeasurementLog());
		this.networkInterfaceFactory = networkInterfaceFactory;
		this.clock = clock;
		this.clock.setExecutionContext(this);
		this.addressToBindTo = addressToBindTo;

		initPeerlets();
		setState(PeerState.INITIALIZED);
	}

	/**
	 * Creates the peer's network interface using the current peer network
	 * interface factory and binds it to the current peer network address. While
	 * this is happening the peer state is <code>STARTING</code>. When the
	 * interface is up, <code>start()</code> is called on all the peerlets
	 * managed by this peer and the peer's state becomes <code>RUNNING</code>.
	 * Note, the network interface is brought up asynchronously so the peer
	 * might not be <code>RUNNING</code> immediately after this method returns.
	 * 
	 * Throws a RuntimeException if init() was not called before or if the peer
	 * is already running.
	 * 
	 */
	public void start() {
		if (getState() == PeerState.UNINITIALIZED) {
			throw new RuntimeException("Attempting to start an unitialized peer. Call init() before start().");
		}
		if (getState() == PeerState.RUNNING) {
			throw new RuntimeException("Attempting to start an already running peer.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Starting the peer...");
		}
		setState(PeerState.STARTING);

		this.networkInterface = networkInterfaceFactory.createNewNetworkInterface(getMeasurementLogger(),
				this.addressToBindTo);
		this.networkInterface.setExecutionContext(this);
		this.networkInterface.addNetworkListener(new PeerNetworkListener());
		networkInterface.bringUp();
		// wait for the async interfaceUp() callback and then transition to the
		// RUNNING state
	}

	/**
	 * Calls <code>stop</code> on all the peerlets managed by this peer, brings
	 * down the network interface. While this is happening the peer's state is
	 * <code>STOPPING</code>. When the network interface is finally down, the
	 * state is set to <code>STOPPED</code>.
	 * 
	 */

	public void stop() {
		if (getState() != PeerState.RUNNING) {
			throw new RuntimeException("Attempting to stop a non-running peer");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Stopping the peer...");
		}
		setState(PeerState.STOPPING);

		stopPeerlets();
		networkInterface.bringDown();
	}

	/**
	 * Adds a peerlet
	 * 
	 * @param peerlet
	 */
	public void addPeerlet(Peerlet peerlet) {
		peerlets.add(peerlet);
	}

	/**
	 * Returns a peerlet that is an instance of <code>clazz</code>. If there is
	 * more than one peerlet satisfying the <code><b>intanceof</b> clazz</code>
	 * condition an arbitrary one of them is returned.
	 * 
	 * @param clazz
	 * @return
	 */
	public Peerlet getPeerletOfType(Class clazz) {
		for (Peerlet peerlet : peerlets) {
			if (clazz.isInstance(peerlet)) {
				return peerlet;
			}
		}
		return null;
	}

	/**
	 * Sends the <code>message</code> to the specified <code>destination</code>
	 * address using the current peer's network interface. Can only be called in
	 * the <code>RUNNING</code> state. The destination and the message must be
	 * non-null. If any of these conditions are false logs an error and returns
	 * without sending any messag, otherwise calls
	 * <code>handleOutgoingMessage</code> on all managed peerlets before sending
	 * the message.
	 * 
	 * @param destination
	 * @param message
	 */
	public void sendMessage(NetworkAddress destination, Message message) {
		message = message.clone();
		if (getState() != PeerState.RUNNING) {
			logger.error("Attempting to send message " + message + " in state: " + getState());
			fireNetworkExceptionHappened(destination, message, new RecoverableNetworkException("Peer is not running."));
			return;
		}
		if (message == null) {
			logger.error("Message in sendMessage is null");
			fireNetworkExceptionHappened(destination, message, new RecoverableNetworkException(
					"Message in the sendMessage() call is null."));
			return;
		}
		if (destination == null) {
			logger.error("Destination in sendMessage is null");
			fireNetworkExceptionHappened(destination, message, new RecoverableNetworkException(
					"Destination in the sendMessage() call is null."));
			return;
		}
		message.setDestinationAddress(destination);
		// tell peerlets about the message that is about to be sent
		fireHandleOutgoingMessage(message);

		// enqueue message if outgoing queue exists otherwise send right away
		if (getOutgoingMessageQueue() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Enqueuing message, destination: " + destination + " message: " + message);
			}
			getOutgoingMessageQueue().enqueue(message);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Sending message, destination: " + destination + " message: " + message);
			}
			networkInterface.sendMessage(destination, message);
		}
	}

	public MessageQueue getOutgoingMessageQueue() {
		return outgoingMessageQueue;
	}

	/**
	 * Sets the outgoing message queue, should be called before calling init().
	 * 
	 * @param messageQueue
	 */
	public synchronized void setOutgoingMessageQueue(MessageQueue messageQueue) {
		if (getState() != PeerState.UNINITIALIZED) {
			throw new RuntimeException("Cannot set queue after the peer was initialized.");
		}
		this.outgoingMessageQueue = messageQueue;
		messageQueueListener = new MessageQueueListener() {

			public void messageAvailable() {
				Message message = getOutgoingMessageQueue().dequeue();
				if (logger.isDebugEnabled()) {
					logger.debug("Sending message, destination: " + message.getDestinationAddress() + " message: "
							+ message);
				}
				networkInterface.sendMessage(message.getDestinationAddress(), message);
			}

			public void messageDropped(Message message) {
				if (logger.isDebugEnabled()) {
					logger.debug("Message " + message + " dropped in the outgoing queue");
				}
			}
		};

		this.outgoingMessageQueue.addMessageQueueListener(messageQueueListener);
	}

	/**
	 * Broadcasts a message through the current peer's network interface. The
	 * network interface must support the broadcasting. Does
	 * <strong>not</strong> call the <code>handleOutgoingMessage</code> on the
	 * peerlets.
	 * 
	 * @param message
	 */
	public void broadcastMessage(Message message) {
		if (getState() != PeerState.RUNNING) {
			logger.error("Attempting to broadcast message " + message + " in state: " + getState());
			fireNetworkExceptionHappened(null, message, new RecoverableNetworkException("Peer is not running."));
			return;
		}
		if (message == null) {
			logger.error("Message in the broadcastMessage() call is null.");
			fireNetworkExceptionHappened(null, message, new RecoverableNetworkException(
					"Message in the broadcastMessage() call is null."));
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Broadcasting message: " + message);
		}
		networkInterface.broadcastMessage(message);
	}

	public void setIdentifier(PeerIdentifier newIdentifier) {
		this.identifier = newIdentifier;
	}

	public PeerIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the network address to which the current peer's network interface
	 * is bound to. Returns <code>null</code> if the interface has not been
	 * initialized.
	 * 
	 * @return
	 */
	public NetworkAddress getNetworkAddress() {
		if (networkInterface == null) {
			return null;
		}
		return networkInterface.getNetworkAddress();
	}

	private void initPeerlets() {
		for (Peerlet peerlet : peerlets) {
			peerlet.init(this);
		}
	}

	private void startPeerlets() {
		for (Peerlet peerlet : peerlets) {
			peerlet.start();
		}
	}

	private void stopPeerlets() {
		for (Peerlet peerlet : peerlets) {
			peerlet.stop();
		}
	}

	private void fireHandleIncomingMesage(Message message) {
		for (Peerlet peerlet : peerlets) {
			peerlet.handleIncomingMessage(message);
		}
	}

	private void fireHandleOutgoingMessage(Message message) {
		for (Peerlet peerlet : peerlets) {
			peerlet.handleOutgoingMessage(message);
		}
	}

	public Clock getClock() {
		return clock;
	}

	public PeerState getState() {
		return state;
	}

	private void setState(PeerState peerState) {
		this.state = peerState;
	}

	public MeasurementLogger getMeasurementLogger() {
		return this.measurementLogger;
	}

	public Finger getFinger() {
		return new Finger(getNetworkAddress(), getIdentifier());
	}

	public int getIndexNumber() {
		return indexNumber;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();			
		buffer.append("(pidx=");
		buffer.append(getIndexNumber());
		buffer.append(", paddr=");
		buffer.append(getNetworkAddress());
		buffer.append(", pid=");
		buffer.append(getIdentifier());
		buffer.append(", pstate=");
		buffer.append(getState());
		buffer.append(")");
		return buffer.toString();
	}
	
}
