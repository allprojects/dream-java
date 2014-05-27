package protopeer;

import org.apache.log4j.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.network.mina.*;
import protopeer.time.*;

/**
 * This Experiment configures a single peer for the live run. The peer index is
 * taken from <code>MainConfiguration.peerIndex</code>. The peer binds to the IP
 * address <code>MainConfiguration.peerIP</code> and the port
 * <code>MainConfiguration.peerPort</code>. If the port number is zero then the
 * first free port is automatically assigned. The address of the index zero peer
 * is taken from <code>MainConfiguration.peerZeroIP</code> and
 * <code>MainConfiguration.peerZeroPort</code>.
 * 
 */

public class LiveExperiment extends Experiment {

	private MinaAddress peerZeroAddress;

	private MinaAddress localPeerAddress;

	private int localPeerIndex;

	@Override
	public Clock createClock() {
		return new RealClock();
	}

	@Override
	public NetworkInterfaceFactory createNetworkInterfaceFactory() {
		return new MinaNetworkInterfaceFactory(getRootMeasurementLogger());
	}

	@Override
	public NetworkAddress getAddressToBindTo(int peerIndex) {		
		if (peerIndex == localPeerIndex) {
			return localPeerAddress;
		}
		if (peerIndex == 0) {
			return peerZeroAddress;
		}
		throw new RuntimeException("Address unknown for peer index: " + peerIndex);
	}

	@Override
	public void init() {
		localPeerAddress = new MinaAddress(MainConfiguration.getSingleton().peerIP,
				MainConfiguration.getSingleton().peerPort);
		peerZeroAddress = new MinaAddress(MainConfiguration.getSingleton().peerZeroIP,
				MainConfiguration.getSingleton().peerZeroPort);
		localPeerIndex = MainConfiguration.getSingleton().peerIndex;
		super.init();
	}

	/**
	 * 
	 * 
	 * @return the index of the peer that is configured in this experiment.
	 */
	public int getLocalPeerIndex() {
		return localPeerIndex;
	}

	/**
	 * 
	 * @return the address of the index zero peer
	 */
	public MinaAddress getPeerZeroAddress() {
		return peerZeroAddress;
	}

	/**
	 * 
	 * 
	 * @return the address of the peer that is configured in this experiment
	 */
	public MinaAddress getLocalPeerAddress() {
		return localPeerAddress;
	}

}
