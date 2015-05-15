package protopeer.network.delayloss;

import java.util.*;

import protopeer.network.*;

/**
 * 
 * Models the loss and delay in the neworks. Before the
 * <code>NetworkAddress</code> can be used in the <code>getDelay</code> or
 * <code>getLoss</code> methods it must be allocated via
 * <code>allocateAddress</code>.
 * 
 */
public interface DelayLossNetworkModel {

	/**
	 * Allocates an address in the model.
	 * 
	 * @return the allocated address
	 */
	public abstract NetworkAddress allocateAddress();

	/**
	 * Deallocates the address.
	 * 
	 * @param address
	 *            the deallocated address
	 */
	public abstract void deallocateAddress(NetworkAddress address);

	/**
	 * Returns the number of available unallocated addresses.
	 * 
	 * @return
	 */
	public abstract int getNumAvailableAddresses();

	/**
	 * Returns the delay in milliseconds that the <code>message</code> should
	 * experience when being sent from the <code>sourceAddress</code> to the
	 * <code>destinationAddress</code>.
	 * 
	 * @param sourceAddress
	 * @param destinationAddress
	 * @param message
	 * @return
	 */
	public abstract double getDelay(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message);

	/**
	 * Returns true if the <code>message</code> should
	 * be lost when being sent from the <code>sourceAddress</code> to the
	 * <code>destinationAddress</code>.
	 * 
	 * @param sourceAddress
	 * @param destinationAddress
	 * @param message
	 * @return
	 */
	public abstract boolean getLoss(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message);

	/**
	 * Returns the collection of network addresses reachable when sending a broadcast from the <code>soruceAddress</code>.
	 * @param srouceAddress
	 * @return
	 */
	public abstract Collection<NetworkAddress> getAddressesReachableByBroadcast(NetworkAddress srouceAddress);

}