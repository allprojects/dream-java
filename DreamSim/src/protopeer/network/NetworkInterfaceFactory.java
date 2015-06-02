package protopeer.network;

import protopeer.measurement.*;

/**
 * Creates and manages the {@link NetworkInterface}s.  
 * 
 * @author galuba
 *
 */
public interface NetworkInterfaceFactory {
	/**
	 * Creates a network interface, through which <code>Message</code>s can be sent.
	 * @param measurementLogger 
	 * @param addressToBindTo, if null the factory should choose the address itself
	 * @return the newly created network interface 
	 */
	public abstract NetworkInterface createNewNetworkInterface(MeasurementLogger measurementLogger, NetworkAddress addressToBindTo);
}
