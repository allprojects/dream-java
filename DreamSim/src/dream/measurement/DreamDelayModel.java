package dream.measurement;

import dream.experiments.DreamConfiguration;
import protopeer.Experiment;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;
import protopeer.network.delayloss.UniformDelayModel;
import protopeer.util.RandomnessSource;

public class DreamDelayModel extends UniformDelayModel {

	public DreamDelayModel(double minDelay, double maxDelay) {
		super(minDelay, maxDelay);
	}

	@Override
	public double getDelay(NetworkAddress sourceAddress, NetworkAddress destinationAddress, Message message) {
		final double linkDelay = minDelay + RandomnessSource.getNextNetworkDouble() * (maxDelay - minDelay);
		final int numLinks = communicationInvolvesLockManager(sourceAddress, destinationAddress) //
		    ? DreamConfiguration.get().numHopsToLockManager //
		    : DreamConfiguration.get().numHopsPerLink;
		return linkDelay * numLinks;
	}

	private final boolean communicationInvolvesLockManager(NetworkAddress sourceAddress,
	    NetworkAddress destinationAddress) {
		final int lockManagerId = DreamConfiguration.get().numberOfBrokers + DreamConfiguration.get().numberOfClients + 1;
		final NetworkAddress lockManagerAddress = Experiment.getSingleton().getAddressToBindTo(lockManagerId);
		return sourceAddress.equals(lockManagerAddress) || destinationAddress.equals(lockManagerAddress);
	}

}
