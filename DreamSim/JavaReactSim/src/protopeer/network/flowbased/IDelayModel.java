package protopeer.network.flowbased;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.util.quantities.Time;

/**
 * Implementing classes have to calculate the delay at connection startup 
 *
 */
public interface IDelayModel {
	
	/**
	 * Calculates the delay at connection startup for 
	 * the path specified by <code>source</code> and <code>destination</code>
	 * 
	 * @param source the source address of the connection
	 * @param destination the destination address of the connection
	 * @param message the message which will be transmitted
	 * @return the delay as a <code>Time</code> object.
	 */
	public Time getDelay(NetworkAddress source, NetworkAddress destination, Message message);
}
