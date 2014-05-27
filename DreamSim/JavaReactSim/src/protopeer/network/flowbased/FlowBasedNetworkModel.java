package protopeer.network.flowbased;

import java.util.*;

import protopeer.network.Message;
import protopeer.time.*;
import protopeer.util.quantities.*;

/**
 * Models the exchange of messages between peers on a flow level. 
 * It is used for simulation, not for emulation.
 * 
 * Every <code>Message</code> is sent
 * using a <code>Connection</code>. The time until the message is transmitted
 * depends on <code>message.getMessageSize()</code> and the available 
 * bandwidth.
 * 
 * The available bandwidth of a connection
 * is calculated by a <code>BandwidthAllocationAlgorithm</code>. Concrete
 * subclasses of that class can implement different strategies. Which algorithm
 * can be used may also depend on the topology and in particular on where the
 * bottlenecks are (only at the uplink, uplink and downlink, in the core 
 * network ...)
 * 
 * An instance of <code>IDelayModel</code> can be used, but this is not a
 * mandatory component of this network model. If one is specified, the 
 * connections are established after the delay given by the delay model.
 *  
 */
public class FlowBasedNetworkModel {

	/**
	 * Contains the information which links are used for a
	 * given source and destination
	 */
	private Topology topology;
	
	/**
	 * Calculates the bandwidth of the connections in the network
	 * when new connections are established or old ones are torn 
	 * down. Note that the algorithm has to fit the given
	 * <code>topology</code>.
	 */
	private BandwidthAllocationAlgorithm bandwidthAllocationAlgorithm;
	
	/**
	 * Responsible for calculating the delay at connection startup.
	 * Optional component, can be null. Then, connections are
	 * established immediately when <code>sendMessage()</code> is 
	 * called. 
	 */
	private IDelayModel delayModel;
	
	/**
	 * Contains the listeners which have to be notified when
	 * connections have been established or torn down.
	 */
	private Collection<IConnectionListener> connectionListeners;
	
	/**
	 * Contains all connection of the network and the corresponding
	 * events that terminate the connections. This is necessary
	 * because the execution time of the event has to be adjusted
	 * when the transmission rate of the connection changes.
	 */
	private Map<Connection, TransmissionCompletedEvent> activeConnections;
	
	/**
	 * Used if the connections start the transmission delayed.
	 * The map contains for every connection the event which
	 * triggers the start of the connection. This is necessary
	 * if the connection is canceled before it starts transmission.
	 */
	private Map<Connection, Event> pendingConnections;
	
	/**
	 * Used to access the current time and to schedule events at
	 * connection start and connection end.
	 */
	private EventScheduler eventScheduler;

	/**
	 * Constructor. The three parameters are mandatory, i.e., they
	 * are assumend to be not null throughout the whole class.
	 * 
	 * @param topology 
	 *            the topology (inter-connection of links)
	 * @param algorithm 
	 *            the algorithm for the bandwidth allocation to be used
	 * @param eventScheduler 
	 *            eventScheduler to permit access to the current time and to
	 *            to add and cancel events 
	 */
	public FlowBasedNetworkModel(Topology topology, 
			BandwidthAllocationAlgorithm algorithm, 
			EventScheduler eventScheduler) {

		assert(topology != null);
		assert(algorithm != null);
		assert(eventScheduler != null);
		
		this.topology = topology;
		this.bandwidthAllocationAlgorithm = algorithm;
		this.eventScheduler = eventScheduler;
		connectionListeners = new LinkedList<IConnectionListener>();
		activeConnections = new HashMap<Connection, TransmissionCompletedEvent>();
		pendingConnections = new HashMap<Connection, Event>();
	}
	
	/**
	 * Expanded Constructor to include the delay model.
	 * 
	 * @param topology 
	 *            the topology (inter-connection of links)
	 * @param algorithm 
	 *            the algorithm for the bandwidth allocation to be used
	 * @param eventScheduler 
	 *            eventScheduler to permit access to the current time and to
	 *            to add and cancel events 
	 * @param delaymodel
	 * 			  the delay model to be used in the network model
	 */
	public FlowBasedNetworkModel(Topology topology, 
			BandwidthAllocationAlgorithm algorithm, 
			EventScheduler eventScheduler, IDelayModel delaymodel) {
		
		this(topology, algorithm, eventScheduler);
		
		assert(delaymodel != null);
		this.delayModel = delaymodel;

	}

	/**
	 * Establishes a <code>Connection</code> between <code>source</code> and
	 * <code>destination</code> of the message and sends the message 
	 * using this connection.
	 * 
	 * The field <code>messageSize</code> is used to determine the required
	 * time to transmit the <code>message</code>
	 * 
	 * If an instance of <code>IDelayModel<code> is present, the connection
	 * is established after a certain delay given be the concrete delay
	 * model. If not, then the connection is established immediately. 
	 * 
	 * @param the connection used to send the message
	 * 
	 */
	public void establishConnection(final Connection connection) {
		
		/*
		 * if a delayModel is given, schedule an event for the start of
		 * of the data transmission. If not, start immediately
		 */
		if (delayModel != null) {
			Message m = connection.getMessage();
			Time delay = delayModel.getDelay(m.getSourceAddress(),
					m.getDestinationAddress(), m);
			
			if (delay != null && delay.isGreaterThan(Time.ZERO)) {
				Event startTransmissionEvent = new Event() {
					@Override
					public void execute() {
						startTransmission(connection);
					}
				}; 
				eventScheduler.enqueueEvent(startTransmissionEvent, delay);
				// remember the event, so that it can be canceled if necessary
				pendingConnections.put(connection, startTransmissionEvent);
			} else {
				// establish connection immediately
				startTransmission(connection);
			}
		} else {
			// establish connection immediately
			startTransmission(connection);
		}
		
	}

	/**
	 * Tests if the destination interface is up. If not, the source interface 
	 * is notified that the connection has failed.
	 * 
	 * If it is up, the method establishes the connection <code>connection</code>, 
	 * reallocates the bandwidth of the other connections in the network and
	 * notifies the listeners.
	 * 
	 * @param connection the connection to be established.
	 */
	private void startTransmission(final Connection connection) {
		
		// delete the connection from pending if present
		pendingConnections.remove(connection);
		
		final FlowBasedNetworkInterface sourceIF = connection.getSourceInterface();
		final FlowBasedNetworkInterface destIF = connection.getDestinationInterface();
		
		if (destIF != null && destIF.isUp()) {
			/*
			 * A Message with size 0 is considered to have no impact on bandwidth 
			 * allocation in the network. Therefore, no reallocation of the
			 * connections' bandwidth is triggered. This should speed up
			 * simulation time considerably. 
			 */
			Data messageSize = connection.getMessage().getSize();
			if (messageSize == null || messageSize.equals(Data.ZERO)) {
				destIF.incomingConnectionEstablished(connection);
				
				eventScheduler.enqueueEvent(new Event() {
					@Override
					public void execute() {
						sourceIF.outgoingConnectionCompleted(connection);
						destIF.incomingConnectionCompleted(connection);
					}
				}, Time.ZERO);
				
			} else {
				/*
				 * add the new connection to the active connections and
				 * notify the used links by calling connection.establish()  
				 */
				activeConnections.put(connection, null);
				
				List<Link> involvedLinks = topology.getLinks(
						connection.getSourceAddress(), 
						connection.getDestinationAddress());
				
				destIF.incomingConnectionEstablished(connection);
				connection.startTransmission(involvedLinks, eventScheduler.now());

				/*
				 * caluclate an set the new transmission rates
				 */
				Map<Connection, Bandwidth> newRates = bandwidthAllocationAlgorithm.
					connectionAdded(connection, getActiveConnections());
				updateConnectionRates(newRates);

				/*
				 * notify listeners
				 */
				fireConnectionEstablished(connection);
			}
		} else {
			sourceIF.connectionFailed(connection);
		}
	}
	
	/**
	 * Normal shutdown of a connection.
	 * 
	 * Removes the <code>connection</code> from the network and initiates
	 * a recalculation of the available bandwidth for the remainig
	 * connections.
	 * 	
	 * @param connection connection which is removed from the network
	 */
	public void connectionCompleted(Connection connection) {
		connection.teardown(eventScheduler.now());
		shutdownConnection(connection);
		
		connection.getSourceInterface().outgoingConnectionCompleted(connection);
		connection.getDestinationInterface().incomingConnectionCompleted(connection);
	}
	
	/**
	 * Abnormal shutdown before all data of the connection is 
	 * transmitted. Removes the connection from the network
	 * and notifies the other end of the connection.
	 * 
	 * @param connection the canceled connection.
	 */
	public void connectionCanceled(Connection connection, 
			FlowBasedNetworkInterface cancelingInterface) {

		connection.cancel(eventScheduler.now());
		
		// test if the connection was not yet established,
		if (pendingConnections.containsKey(connection)) {
			// cancel the event which should start the transmission
			Event startTransmissionEvent = pendingConnections.get(connection);
			pendingConnections.remove(connection);
			eventScheduler.cancelEvent(startTransmissionEvent);
		} else {
			// remove the termination event from the queue
			TransmissionCompletedEvent event = activeConnections.get(connection);
			if (event != null) {
				eventScheduler.cancelEvent(event);
			}

			// remove the connection from the network
			shutdownConnection(connection);
			// notify the other end of the connection
			connection.getOtherInterface(cancelingInterface).
				connectionCanceled(connection);
		}
		
	}
	
	/**
	 * Removes an already established connection from the network
	 * and triggers reallocation of the bandwidth. Doesn't matter
	 * if the connection is completed or canceled.
	 * 
	 * @param connection the connection to be removed
	 */
	private void shutdownConnection(Connection connection) {
		activeConnections.remove(connection);
		Map<Connection, Bandwidth> newRates = bandwidthAllocationAlgorithm.
				connectionTerminated(connection, getActiveConnections());
		updateConnectionRates(newRates);
		
		fireConnectionTerminated(connection);
	}
	
	/**
	 * Sets the transmission rate of the connections to the new values 
	 * specified in the map and adjusts the termination events
	 * 
	 * @param newRates a map containing the new transmission rate for at least
	 *                 those connection whose rates have changed
	 */
	private void updateConnectionRates(Map<Connection, Bandwidth> newRates) {
		
		for (Connection connection : newRates.keySet()) {
			Bandwidth newRate = newRates.get(connection);
			if (!newRate.equals(connection.getRate())) {
				connection.updateRate(newRate, eventScheduler.now());
				
				/*
				 * Check if there was already scheduled an event for the end
				 * of the connection. If so, remove it from the event queue
				 */
				Event oldEvent = activeConnections.get(connection);
				if (oldEvent != null) {
					eventScheduler.cancelEvent(oldEvent);
				}
				
				/*
				 * Create the new event for the point in time when the
				 * transmission is completed in case of no further change
				 * of the transmission rate
				 */

				TransmissionCompletedEvent event = null;

				if (connection.getRate().isGreaterThan(Bandwidth.ZERO)) {
					Time remainingTime = connection.getRemainingTime(eventScheduler.now());
					event = new TransmissionCompletedEvent(this, connection);
					eventScheduler.enqueueEvent(event, remainingTime);
				} 
					
				
				/*
				 * remember the event so that it can be adjusted later
				 * when the rate of the connection changes
				 */
				activeConnections.put(connection, event);
			}
		}
		
	}

	/*
	 * Listener stuff ...
	 */
	
	public void addConnectionListener(IConnectionListener listener) {
		connectionListeners.add(listener);
	}
	
	public void removeConnectionListener(IConnectionListener listener) {
		connectionListeners.remove(listener);
	}
	
	protected void fireConnectionEstablished(Connection connection) {
		for (IConnectionListener listener : connectionListeners) {
			listener.connectionEstablished(connection);
		}
	}
	
	protected void fireConnectionTerminated(Connection connection) {
		for (IConnectionListener listener : connectionListeners) {
			listener.connectionTerminated(connection);
		}
	}

	/*
	 * Getter and setter ...
	 */
	
	public void setBandwidthAllocationAlgorithm(
			BandwidthAllocationAlgorithm bandwidthAllocationAlgorithm) {
		this.bandwidthAllocationAlgorithm = bandwidthAllocationAlgorithm;
	}
	
	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}
	
	public Set<Connection> getActiveConnections() {
		return activeConnections.keySet();
	}

	public IDelayModel getDelayModel() {
		return delayModel;
	}

	public void setDelayModel(IDelayModel delayModel) {
		this.delayModel = delayModel;
	}
	
}

