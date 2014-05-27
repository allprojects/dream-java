package protopeer.network.delayloss;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.util.*;

/**
 * The implementation of the <code>NetworkInterface</code> for the delay-loss
 * model.
 * 
 * 
 */
public class DelayLossNetworkInterface extends NetworkInterface {

	private static Logger logger = Logger.getLogger(DelayLossNetworkInterface.class);

	private DelayLossNetworkInterfaceFactory delayLossNetworkInterfaceFactory;

	private ByteArrayOutputStream byteStream;

	public DelayLossNetworkInterface(NetworkAddress networkAddress,
			DelayLossNetworkInterfaceFactory delayLossNetworkInterfaceFactory, MeasurementLogger measurementLogger) {
		super(networkAddress,measurementLogger);
		this.networkAddress = networkAddress;
		this.delayLossNetworkInterfaceFactory = delayLossNetworkInterfaceFactory;
		this.interfaceUp = false;
		if (MainConfiguration.getSingleton().enableMessageSerializationDuringSimulation) {
			byteStream = new ByteArrayOutputStream(4096);
		}
	}

	private void sendMessage(Collection<NetworkAddress> destinationAddresses, Message message) {
		message.setSourceAddress(networkAddress);
		byte[] serializedMessage = null;
		Message messageToSend = null;
		if (MainConfiguration.getSingleton().enableMessageSerializationDuringSimulation) {
			if (MainConfiguration.getSingleton().enableLightweightSerialization) {
				try {
					serializedMessage = LightweightSerialization.getSingleton().serializeObject(message);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
			} else {
				byteStream.reset();

				try {
					ObjectOutputStream serialStream = new ObjectOutputStream(byteStream);
					serialStream.writeObject(message);
					serialStream.flush();
				} catch (IOException e) {
					logger.error("IOException while serializing the message");
					e.printStackTrace();
					return;
				}

				serializedMessage = byteStream.toByteArray();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Size after serialization: " + serializedMessage.length);
			}
			message.setMessageSize(serializedMessage.length);
		} else {
			// if serialization is disabled, clone the messages
			// used to be like that, now cloning happens right in the
			// Peer.sendMessage call
			// messageToSend = message.clone();
			messageToSend = message;
		}

		// send the message to destinations
		for (NetworkAddress destination : destinationAddresses) {
			try {
				delayLossNetworkInterfaceFactory.sendMessage(this, destination, messageToSend, serializedMessage);
			} catch (NetworkException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("", e);
				}
				fireExceptionHappened(destination, message, e);
			}
		}

	}

	@Override
	public void broadcastMessage(Message message) {
		sendMessage(delayLossNetworkInterfaceFactory.getAddressesReachableByBroadcast(this), message);
	}

	@Override
	public void sendMessage(NetworkAddress destination, Message message) {
		if (!isUp()) {
			logger.warn("Interface down, ignoring message: " + message + " dest: " + destination);
		} else {
			Collection<NetworkAddress> destinations = new LinkedList<NetworkAddress>();
			destinations.add(destination);
			sendMessage(destinations, message);
		}
	}

	void messageReceived(DelayLossNetworkInterface sourceInterface, Message message, byte[] serializedMessage) {
		NetworkAddress sourceAddress = sourceInterface.getNetworkAddress();
		if (!isUp()) {
			fireExceptionHappened(getNetworkAddress(), message, new NetworkException("Destination interface down."));
			return;
		}

		if (MainConfiguration.getSingleton().enableMessageSerializationDuringSimulation) {
			Message deserializedMessage = null;
			if (MainConfiguration.getSingleton().enableLightweightSerialization) {
				try {
					deserializedMessage = (Message) LightweightSerialization.getSingleton().deserializeObject(
							serializedMessage);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
			} else {
				try {
					ObjectInputStream serialStream = new ObjectInputStream(new ByteArrayInputStream(serializedMessage));
					deserializedMessage = (Message) serialStream.readObject();
				} catch (IOException e) {
					logger.error("IOException while serializing the message");
					return;
				} catch (ClassNotFoundException e) {
					logger.error("ClassNotFoundException while serializing the message");
					return;
				}
			}
			if (deserializedMessage != null) {
				deserializedMessage.setMessageSize(serializedMessage.length);
				fireMessageReceived(sourceAddress, deserializedMessage);
				sourceInterface.fireMessageSent(getNetworkAddress(), deserializedMessage);
			}
		} else {
			fireMessageReceived(sourceAddress, message);
			sourceInterface.fireMessageSent(getNetworkAddress(), message);
		}
	}

	@Override
	public void bringDown() {
		delayLossNetworkInterfaceFactory.destroyNetworkInterface(this);
		super.bringDown();
	}

}
