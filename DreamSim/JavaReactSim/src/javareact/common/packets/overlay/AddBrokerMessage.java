package javareact.common.packets.overlay;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;

/**
 * This class represents a message for adding a new broker.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 */
public class AddBrokerMessage extends Message {

	private static final long serialVersionUID = -7791413194580191036L;
    
	private final NetworkAddress newBroker;
	
	/**
	 * Create a new broker.
	 * 
	 * @param newBroker New broker to add.
	 */
	public AddBrokerMessage(NetworkAddress newBroker) {
		this.newBroker = newBroker;
	}
	
	/**
	 * Get the address of the new broker the new broker to add.
	 * 
	 * @return The new broker to add.
	 */
	public NetworkAddress getNewBroker() {
		return newBroker;
	}
	
	@Override
	public String toString() {
		return "ADDBROKER (+" + newBroker + ")";
	}
	
}
