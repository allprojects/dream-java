package protopeer.network.delayloss;

import java.util.Collection;

import protopeer.network.IntegerNetworkAddress;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.util.RandomnessSource;

/**
 *
 * A lossless network model in which the message delay is uniformely randomly
 * selected between some minimal and some maximal value.
 *
 *
 */
public class UniformDelayModel implements DelayLossNetworkModel {

	private int allocatedNodeAddress = 1;

	protected final double minDelay;
	protected final double maxDelay;

	/**
	 * Creates a delay model that delays messages by a delay uniformely randomly
	 * chosen between <code>minDelay</code> and <code>maxDelay</code>
	 *
	 * @param minDelay
	 *          minimum message delay in milliseconds
	 * @param maxDelay
	 *          maximum message delay in milliseconds
	 */
	public UniformDelayModel(double minDelay, double maxDelay) {
		super();
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
	}

	@Override
	public NetworkAddress allocateAddress() {
		return new IntegerNetworkAddress(allocatedNodeAddress++);
	}

	@Override
	public double getDelay(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message) {
		return minDelay + RandomnessSource.getNextNetworkDouble() * (maxDelay - minDelay);
	}

	/**
	 * This network model has an practically unbounded number of addresses that
	 * can be allocated.
	 */
	@Override
	public int getNumAvailableAddresses() {
		return Integer.MAX_VALUE - allocatedNodeAddress;
	}

	@Override
	public void deallocateAddress(NetworkAddress address) {

	}

	/**
	 * This network model is lossless.
	 */
	@Override
	public boolean getLoss(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message) {
		return false;
	}

	/**
	 * <b>Not implemented in this network model</b>
	 */
	@Override
	public Collection<NetworkAddress> getAddressesReachableByBroadcast(NetworkAddress srouceAddress) {
		throw new RuntimeException("Not implemented");
	}

}
