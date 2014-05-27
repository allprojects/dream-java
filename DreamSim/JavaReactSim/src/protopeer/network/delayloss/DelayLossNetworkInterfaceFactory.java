package protopeer.network.delayloss;

import java.util.*;

import org.apache.log4j.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.time.*;

/**
 * 
 * An implementation of the <code>NetworkInterfaceFactory</code> for the
 * simple network that loses messages and delays them, independent of their
 * size. Bandwidth limitations are not modelled. Uses a
 * <code>DelayLossNetworkModel</code> as the model the network delay and loss.
 * FIFO order of messages is not guaranteed, even for messages sent between the
 * same source-destination pairs.
 * 
 */
public class DelayLossNetworkInterfaceFactory implements NetworkInterfaceFactory {

	private static Logger logger = Logger.getLogger(DelayLossNetworkInterfaceFactory.class);

	private HashMap<NetworkAddress, DelayLossNetworkInterface> address2interfaceMap;

	private EventScheduler eventScheduler;

	private DelayLossNetworkModel delayLossNetworkModel;

	/**
	 * Returns the network model used by this factory.
	 * 
	 * @return
	 */
	public DelayLossNetworkModel getDelayLossNetworkModel() {
		return delayLossNetworkModel;
	}

	/**
	 * Creates a network interface factory
	 * 
	 * @param eventScheduler
	 *            the event scheduler that the factory and all the interfaces
	 *            that it creates will use
	 * @param delayLossNetworkModel
	 *            the network model that this factory and all of the interfaces
	 *            that it creates will use for simulating the message delay and
	 *            loss
	 */
	public DelayLossNetworkInterfaceFactory(EventScheduler eventScheduler, DelayLossNetworkModel delayLossNetworkModel) {
		this.eventScheduler = eventScheduler;
		this.delayLossNetworkModel = delayLossNetworkModel;
		address2interfaceMap = new HashMap<NetworkAddress, DelayLossNetworkInterface>();
	}

	public NetworkInterface createNewNetworkInterface(MeasurementLogger measurementLogger,
			NetworkAddress addressToBindTo) {
		if (addressToBindTo == null) {
			addressToBindTo = delayLossNetworkModel.allocateAddress();
		}
		DelayLossNetworkInterface newInterface = new DelayLossNetworkInterface(addressToBindTo, this, measurementLogger);
		address2interfaceMap.put(addressToBindTo, newInterface);
		if (logger.isDebugEnabled()) {
			logger.debug("Created network interface: " + addressToBindTo);
		}

		return newInterface;
	}

	void destroyNetworkInterface(NetworkInterface networkInterface) {
		NetworkAddress address = networkInterface.getNetworkAddress();
		if (logger.isDebugEnabled()) {
			logger.debug("Dealloc network interface: " + address);
		}
		if (address == null) {
			logger.error("interface address is null");
			return;
		}

		address2interfaceMap.remove(address);
		delayLossNetworkModel.deallocateAddress(networkInterface.getNetworkAddress());
	}

	void sendMessage(DelayLossNetworkInterface sourceNetworkInterface, NetworkAddress destinationAddress,
			Message message, byte[] serializedMessage) throws NetworkException {
		DelayLossNetworkInterface destinationNetworkInterface = address2interfaceMap.get(destinationAddress);

		if (destinationNetworkInterface == null) {
			throw new NetworkException("Destination address does not exist");
		}

		NetworkAddress sourceAddress = sourceNetworkInterface.getNetworkAddress();
		MessageReceivedEvent newEvent = new MessageReceivedEvent(sourceNetworkInterface, destinationNetworkInterface,
				message, serializedMessage);

		// check for loss and send the message
		if (!delayLossNetworkModel.getLoss(sourceAddress, destinationAddress, message)) {
			double delay = delayLossNetworkModel.getDelay(sourceAddress, destinationAddress, message);
			eventScheduler.enqueueEventArbitrary(newEvent, delay);
		} else {
			// don't throw eceptions on loss, it makes peers disconnect on loss
			if (logger.isDebugEnabled()) {
				logger
						.debug("dropping message: " + message + " src: " + sourceAddress + " dest: "
								+ destinationAddress);
			}
		}
	}

	public Collection<NetworkAddress> getAddressesReachableByBroadcast(NetworkInterface sourceNetworkInterface) {
		return delayLossNetworkModel.getAddressesReachableByBroadcast(sourceNetworkInterface.getNetworkAddress());
	}

}
