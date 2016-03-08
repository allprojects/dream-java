package dream.common.packets.overlay;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;

/**
 * This class represents a message for removing an existing broker.
 *
 * @author Daniel Dubois <daniel@dubois.it>
 */
public class RemoveBrokerMessage extends Message {

	private static final long serialVersionUID = 5205869805158147763L;

	private final NetworkAddress existingBroker;

	/**
	 * Creates a message for removing an existing broker.
	 * 
	 * @param existingBroker
	 *          Existing broker to remove
	 */
	public RemoveBrokerMessage(NetworkAddress existingBroker) {
		this.existingBroker = existingBroker;
	}

	/**
	 * Get the address of the existing broker to remove.
	 * 
	 * @return Existing broker to remove
	 */
	public NetworkAddress getExistingBroker() {
		return existingBroker;
	}

	@Override
	public String toString() {
		return "REMOVEBROKER (-" + existingBroker + ")";
	}
}
