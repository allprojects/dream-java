package protopeer.network.flowbased;

import protopeer.network.*;
import protopeer.util.quantities.Time;

/**
 * Extremely simple implementation of the interface
 * <code>IDelayModel</code>. Returns the same delay
 * specified in the constructor for every path
 * in the network.
 *
 */
public class ConstantDelayModel implements IDelayModel {

	private Time delay;
	
	/**
	 * Constructor without parameters
	 */
	public ConstantDelayModel() {}
	
	/**
	 * Constructor. 
	 * 
	 * @param delay the delay for all paths
	 */
	public ConstantDelayModel(Time delay) {
		this.delay = delay;
	}
	
	public Time getDelay(NetworkAddress source, NetworkAddress destination,
			Message message) {
		return delay;
	}
	
	/**
	 * Sets the delay for all paths
	 * 
	 * @param delay 
	 */
	public void setDelay(Time delay) {
		this.delay = delay;
	}


}
