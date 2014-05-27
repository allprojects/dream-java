package protopeer.network.flowbased;

import java.util.*;

import org.apache.log4j.*;

import protopeer.measurement.*;
import protopeer.network.*;

/**
 * Corresponding network interface to the flow based network model.
 * 
 */
public class FlowBasedNetworkInterface extends NetworkInterface {

	private static final Logger logger = Logger.getLogger(FlowBasedNetworkInterface.class);
	private FlowBasedNetworkModel networkModel;
	private FlowBasedNetworkInterfaceFactory factory;
	
	/**
	 * Set of all incoming connections. Used to notify
	 * the senders when this peer goes offline.
	 */
	private Set<Connection> incomingConnections;

	/**
	 * Set of all outgoing connections. Used to notify
	 * the receivers when this peer goes offline.
	 */
	private Set<Connection> outgoingConnections;
	
	public FlowBasedNetworkInterface(
			MeasurementLogger measurementLogger,
			NetworkAddress networkAddress,
			FlowBasedNetworkModel networkModel,
			FlowBasedNetworkInterfaceFactory factory) {
		
		super(networkAddress, measurementLogger);
		this.networkModel = networkModel;
		this.factory = factory;
		incomingConnections = new LinkedHashSet<Connection>();
		outgoingConnections = new LinkedHashSet<Connection>();
	}
	
	@Override
	public void broadcastMessage(Message message) {
		throw new RuntimeException("Not yet implemented");
	}


	/**
	 * Sends the message <code>message</code> to the peer at 
	 * <code>destination</code> using the flow based network model.
	 */
	@Override
	public void sendMessage(NetworkAddress destination, Message message) {
		
		if (!isUp()) {
			logger.warn("Interface down, ignoring message: " + message + " dest: " + destination);
			return;
		}

		message.setSourceAddress(getNetworkAddress());
		message.setDestinationAddress(destination);

//		if (factory.getNetworkInterface(destination) != null){
			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("Peer ");
				sb.append(getNetworkAddress());
				sb.append(" sends a ");
				sb.append(message.getClass().getSimpleName());
				sb.append(" to ");
				sb.append(destination);
				logger.debug(sb.toString());
			}
	
			Connection connection = new Connection(message, this, 
					factory.getNetworkInterface(destination));
	
			outgoingConnections.add(connection);
			networkModel.establishConnection(connection);
//		} else {
//			fireExceptionHappened(message.getDestinationAddress(), message,
//					new ConnectionFailedException());	
//		}
	}
	
	/**
	 * Called when an incoming connection starts transmission 
	 * of the data. 
	 * 
	 * @param connection
	 */
	public void incomingConnectionEstablished(Connection connection) {
		assert isUp();
		incomingConnections.add(connection);
	}


	/**
	 * Called by the network model when the complete message
	 * is transmitted to this interface.
	 * 
	 * @param message the transmitted message
	 */
	public void incomingConnectionCompleted(Connection connection) {

		assert incomingConnections.contains(connection);
		incomingConnections.remove(connection);
		
		Message message = connection.getMessage();
		
		if (logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Peer ");
			sb.append(getNetworkAddress());
			sb.append(" received a ");
			sb.append(message.getClass().getSimpleName());
			sb.append(" from ");
			sb.append(message.getSourceAddress());
			logger.debug(sb.toString());
		}
		
		fireMessageReceived(message.getSourceAddress(), message);
	}
	
	/**
	 * Called by the network model when the complete message
	 * is transmitted to the destination interface.
	 * 
	 * @param message the transmitted message
	 */
	public void outgoingConnectionCompleted(Connection connection) {
		
		assert outgoingConnections.contains(connection);
		outgoingConnections.remove(connection);
		
		Message message = connection.getMessage();
		fireMessageSent(message.getDestinationAddress(), message);
	}

	/**
	 * Cancels all established connections of this
	 * network interface. Notifies the other ends
	 * of the connections.
	 */
	@Override
	public void bringDown() {
	
		Iterator<Connection> it = incomingConnections.iterator();
		while(it.hasNext()) {
			Connection connection = it.next();
			networkModel.connectionCanceled(connection, this);
			it.remove();
		}

		it = outgoingConnections.iterator();
		while(it.hasNext()) {
			Connection connection = it.next();
			networkModel.connectionCanceled(connection, this);
			it.remove();
		}
		
		super.bringDown();
		
		factory.destroyNetworkInterface(this);
	}
	
	
	
	/**
	 * Called by the network when the connection fails,
	 * e.g., when the other peer is not online. Can only
	 * occur with outgoing connections.
	 * 
	 * @param connection
	 */
	public void connectionFailed(Connection connection) {
		outgoingConnections.remove(connection);
		Message m = connection.getMessage();
		fireExceptionHappened(m.getDestinationAddress(), m, 
					new ConnectionFailedException());	
	}
	
	/**
	 * Called by the network if the other end of the connection
	 * cancels the connection
	 * 
	 * @param connection the canceled connection
	 */
	public void connectionCanceled(Connection connection) {
		if (incomingConnections.contains(connection)) {
			incomingConnections.remove(connection);
		} else if (outgoingConnections.contains(connection)) {
			outgoingConnections.remove(connection);
			fireExceptionHappened(
					connection.getDestinationAddress(), 
					connection.getMessage(), 
					new ConnectionCanceledException());
		}
	}	
}
