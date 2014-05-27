package protopeer.network;

import java.util.concurrent.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.measurement.*;

/**
 * Represents the network interface through which messages can be sent and
 * received to/from other network interfaces. Maintains a set of
 * <code>NetworkListener</code>s that receive a callback on network interface
 * events. There is one <code>NetworkAddress</code> associated with each
 * <code>NetworkInterface</code>.
 * 
 */
public abstract class NetworkInterface {

	private static final Logger logger = Logger.getLogger(NetworkInterface.class);

	private CopyOnWriteArrayList<NetworkListener> listeners = new CopyOnWriteArrayList<NetworkListener>();

	private MeasurementLogger measurementLogger;

	protected boolean interfaceUp;

	protected NetworkAddress networkAddress;

	private ExecutionContext executionContext;

	public NetworkInterface(NetworkAddress networkAddress, MeasurementLogger measurementLogger) {
		this.measurementLogger = measurementLogger;
		this.networkAddress = networkAddress;
	}

	/**
	 * Sends the message to a destination.
	 * 
	 * @param destination
	 *            the destination to which the <code>message</code> is sent
	 * @param message
	 *            the message to be sent
	 */
	public abstract void sendMessage(NetworkAddress destination, Message message);

	/**
	 * Sends the message to all destinations that are reachable from this
	 * interface. The <code>NetworkInteface</code> is not required to implement
	 * this method.
	 * 
	 * @param message
	 *            the message to be broadcast
	 */
	public abstract void broadcastMessage(Message message);

	public void addNetworkListener(NetworkListener listener) {
		listeners.add(listener);
	}

	public void removeNetworkListener(NetworkListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Lets the current thread know that it left the network interface's execution context.
	 * 
	 */
	private void enterExecutionContext() {
		if (Experiment.getSingleton() != null) {
			Experiment.getSingleton().enterExecutionContext(getExecutionContext());
		}
	}

	/**
	 * Lets the current thread know that it left the network interface's execution context.
	 * 
	 */
	private void leaveExecutionContext() {
		if (Experiment.getSingleton() != null) {
			Experiment.getSingleton().leaveExecutionContext();
		}
	}

	protected void fireMessageReceived(NetworkAddress sourceAddress, Message message) {
		try {
			enterExecutionContext();
			for (NetworkListener listener : listeners) {
				listener.messageReceived(this, sourceAddress, message);
			}
			leaveExecutionContext();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected void fireExceptionHappened(NetworkAddress remoteAddress, Message message, Throwable cause) {
		try {
			enterExecutionContext();
			for (NetworkListener listener : listeners) {
				listener.exceptionHappened(this, remoteAddress, message, cause);
			}
			leaveExecutionContext();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected void fireMessageSent(NetworkAddress destinationAddress, Message message) {
		try {
			enterExecutionContext();
			for (NetworkListener listener : listeners) {
				listener.messageSent(this, destinationAddress, message);
			}
			leaveExecutionContext();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected void fireInterfaceDown() {
		try {
			enterExecutionContext();
			for (NetworkListener listener : listeners) {
				listener.interfaceDown(this);
			}
			leaveExecutionContext();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected void fireInterfaceUp() {
		try {
			enterExecutionContext();
			for (NetworkListener listener : listeners) {
				listener.interfaceUp(this);
			}
			leaveExecutionContext();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Gets the measurement logger associated with this network interface.
	 * 
	 * @return
	 */
	public MeasurementLogger getMeasurementLogger() {
		return measurementLogger;
	}

	/**
	 * Brings the network interface down, freeing all the underlying resources.
	 * The interface cannot be used to send messages any more. To do that, a new
	 * interface needs to be created.
	 */
	public void bringUp() {
		interfaceUp = true;
		fireInterfaceUp();
	}

	/**
	 * Brings the network interface up. The interface is ready to send messages
	 * after this call.
	 * 
	 */
	public void bringDown() {
		interfaceUp = false;
		fireInterfaceDown();
	}

	public boolean isUp() {
		return interfaceUp;
	}

	/**
	 * Returns the network address associated with this interface.
	 * 
	 * @return
	 */
	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	/**
	 * Returns the execution context that is hosting this network interface.
	 * 
	 * @return
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	/**
	 * Lets the network interface know which execution context it is part of.
	 * 
	 * @param hostPeer
	 */
	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

}
