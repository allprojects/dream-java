package dream.measurement;

import dream.experiments.DreamConfiguration;
import protopeer.BasePeerlet;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.network.Message;
import protopeer.network.NetworkAddress;

/**
 * This peerlet, installed in every client, collects and prints information
 * about the network traffic.
 */
public class ClientMeasurementPeerlet extends BasePeerlet {
	private MeasurementLogger mLogger;

	@Override
	public void init(Peer peer) {
		super.init(peer);
		mLogger = MeasurementLogger.getLogger();
	}

	@Override
	public void handleOutgoingMessage(Message msg) {
		final int numHops = destinationIsLockManager(msg) //
		    ? DreamConfiguration.get().numHopsToLockManager //
		    : DreamConfiguration.get().numHopsPerLink;
		for (int i = 0; i < numHops; i++) {
			mLogger.saveMessage(msg);
		}
	}

	private final boolean destinationIsLockManager(Message msg) {
		final int lockManagerId = DreamConfiguration.get().numberOfBrokers + DreamConfiguration.get().numberOfClients + 1;
		final NetworkAddress lockManagerAddress = Experiment.getSingleton().getAddressToBindTo(lockManagerId);
		return msg.getDestinationAddress().equals(lockManagerAddress);
	}

}
