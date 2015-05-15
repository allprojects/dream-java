package protopeer;

import protopeer.network.*;
import protopeer.network.delayloss.*;
import protopeer.time.*;
import protopeer.util.quantities.*;

public abstract class SimulatedExperiment extends Experiment {

	protected EventScheduler eventScheduler;
	
	private int numPeersInSimulation;

	/** 
	 * Override for convenience.
	 * 
	 * */
	public static SimulatedExperiment getSingleton() {
		return (SimulatedExperiment) Experiment.getSingleton();
	}
	
	/**
	 * Creates the <code>SimulatedClock</code>.s
	 */
	@Override
	public Clock createClock() {
		return new SimulatedClock(eventScheduler);
	}

	/**
	 * Returns the default simple network interface factory. Messages passed
	 * between network interfaces experience zero delay and are never lost.
	 */
	@Override
	public NetworkInterfaceFactory createNetworkInterfaceFactory() {
		DelayLossNetworkModel delayLossNetworkModel = new UniformDelayModel(0, 0);
		return new DelayLossNetworkInterfaceFactory(eventScheduler, delayLossNetworkModel);
	}

	/**
	 * By default each peer binds to an <code>IntegerAddress</code> equal to its
	 * index.
	 */
	@Override
	public NetworkAddress getAddressToBindTo(int peerIndex) {
		return new IntegerNetworkAddress(peerIndex);
	}

	@Override
	public void init() {
		numPeersInSimulation=MainConfiguration.getSingleton().numPeersInSim;
		this.eventScheduler = new EventScheduler();		
		super.init();
	}

	/**
	 * Runs the simulation for the specified amount of simulated time.
	 * 
	 * @param duration
	 *            the amount of simulated time for which
	 *            <code>runSimulation</code> will run
	 */
	public void runSimulation(Time duration) {
		eventScheduler.run(duration);
	}

	/**
	 * 
	 * @return the event scheduler used for the simulation
	 */
	public EventScheduler getEventScheduler() {
		return eventScheduler;
	}

	public int getNumPeersInSimulation() {
		return numPeersInSimulation;
	}

}
