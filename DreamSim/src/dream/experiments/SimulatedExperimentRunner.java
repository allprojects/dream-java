package dream.experiments;

import dream.client.ClientFactory;
import dream.client.TrafficGeneratorPeerlet;
import dream.generator.GraphGenerator;
import dream.generator.RandomGenerator;
import dream.locking.LockManagerFactory;
import dream.measurement.DreamDelayModel;
import dream.measurement.MeasurementLogger;
import dream.overlay.ClientAssociationGenerator;
import dream.overlay.TreeOverlayGenerator;
import dream.server.ServerFactory;
import protopeer.ConfigurationException;
import protopeer.Experiment;
import protopeer.PeerFactory;
import protopeer.SimulatedExperiment;
import protopeer.network.NetworkInterfaceFactory;
import protopeer.network.delayloss.DelayLossNetworkInterfaceFactory;
import protopeer.util.quantities.Time;

/**
 * This class is used to run multiple experiments while changing some system
 * parameters and save the results for later processing.
 */
public class SimulatedExperimentRunner extends SimulatedExperiment {
	private final static String CONFIGURATION_FILE = "conf/dream.conf";

	private static final int protocols[] = { //
	    DreamConfiguration.CAUSAL, //
	    DreamConfiguration.SINGLE_SOURCE_GLITCH_FREE, //
	    DreamConfiguration.COMPLETE_GLITCH_FREE, //
	    DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED, //
	    DreamConfiguration.ATOMIC, //
	    DreamConfiguration.SIDUP //
	};

	public final void runExperiments() {
		// runFromFile(0);
		for (int seed = 0; seed < 10; seed++) {
			runDefault(seed);
			runLocality(seed);
			runNumBrokers(seed);
			runNumVars(seed);
			runGraphDepth(seed);
			runNumGraphDependencies(seed);
			runGraphShareProbability(seed);
			runTimeBetweenEvents(seed);
			runTimeBetweenReads(seed);
		}
	}

	@SuppressWarnings("unused")
	private final void runFromFile(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		runExperiment("fromFile", String.valueOf(seed), "0", getProtocolName(DreamConfiguration.get().consistencyType));
	}

	private final void runDefault(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			runExperiment("default", String.valueOf(seed), String.valueOf(0), getProtocolName(i));
		}
	}

	private final void runLocality(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int locality = 0; locality <= 100; locality += 10) {
				final float floatLoc = (float) locality / 100;
				DreamConfiguration.get().graphLocality = floatLoc;
				runExperiment("locality", String.valueOf(seed), String.valueOf(floatLoc), getProtocolName(i));
			}
		}
	}

	/**
	 * Here we keep the number of hops fixed, while increasing the number of
	 * brokers.
	 *
	 * A tree with n nodes has n-1 links. If each link consists of h hops, the
	 * tree has in total (n-1)*h hops. We want to keep this number identical in
	 * all the runs, by changing h (number of hops per link) together with n
	 * (number of nodes).
	 */
	private final void runNumBrokers(int seed) {
		loadFromFile();
		final int totalNumHops = 20;
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int numBrokers = 1; numBrokers <= 20;) {
				DreamConfiguration.get().numberOfBrokers = numBrokers;
				DreamConfiguration.get().numHopsPerLink = totalNumHops / numBrokers;
				runExperiment("numBrokers", String.valueOf(seed), String.valueOf(numBrokers), getProtocolName(i));
				if (numBrokers < 5) {
					numBrokers += 2;
				} else {
					numBrokers += 5;
				}
			}
		}
	}

	private final void runNumVars(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int numVars = 1; numVars <= 100;) {
				DreamConfiguration.get().graphNumSources = numVars;
				runExperiment("numVars", String.valueOf(seed), String.valueOf(numVars), getProtocolName(i));
				if (numVars < 10) {
					numVars += 3;
				} else {
					numVars += 30;
				}
			}
		}
	}

	private final void runGraphDepth(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int depth = 1; depth <= 10; depth++) {
				DreamConfiguration.get().graphDepth = depth;
				runExperiment("graphDepth", String.valueOf(seed), String.valueOf(depth), getProtocolName(i));
			}
		}
	}

	private final void runNumGraphDependencies(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int dep = 1; dep < 10; dep++) {
				DreamConfiguration.get().graphMaxDependenciesPerNode = dep;
				runExperiment("numGraphDependencies", String.valueOf(seed), String.valueOf(dep), getProtocolName(i));
			}
		}
	}

	private final void runGraphShareProbability(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int share = 0; share <= 100; share += 10) {
				final float shareFloat = (float) share / 100;
				DreamConfiguration.get().graphNodeShareProbability = shareFloat;
				runExperiment("graphShare", String.valueOf(seed), String.valueOf(shareFloat), getProtocolName(i));
			}
		}
	}

	private final void runTimeBetweenEvents(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int timeBetweenEvents = 100; timeBetweenEvents <= 10000;) {
				// TODO: change simulation time accordingly?
				DreamConfiguration.get().epochDuration = 2 * timeBetweenEvents;
				DreamConfiguration.get().minTimeBetweenEventsInMs = Math.min(1, timeBetweenEvents - 1);
				DreamConfiguration.get().maxTimeBetweenEventsInMs = timeBetweenEvents + 1;
				runExperiment("timeBetweenEvents", String.valueOf(seed), String.valueOf(timeBetweenEvents), getProtocolName(i));
				if (timeBetweenEvents < 1000) {
					timeBetweenEvents += 300;
				} else {
					timeBetweenEvents += 3000;
				}
			}
		}
	}

	private final void runTimeBetweenReads(int seed) {
		loadFromFile();
		DreamConfiguration.get().seed = seed;
		for (final int i : protocols) {
			DreamConfiguration.get().consistencyType = i;
			for (int timeBetweenReads = 100; timeBetweenReads <= 10000;) {
				DreamConfiguration.get().minTimeBetweenSignalReadsInMs = Math.min(1, timeBetweenReads - 1);
				DreamConfiguration.get().maxTimeBetweenSignalReadsInMs = timeBetweenReads + 1;
				runExperiment("timeBetweenReads", String.valueOf(seed), String.valueOf(timeBetweenReads), getProtocolName(i));
				if (timeBetweenReads < 1000) {
					timeBetweenReads += 300;
				} else {
					timeBetweenReads += 3000;
				}
			}
		}
	}

	private final void loadFromFile() {
		try {
			DreamConfiguration.get().loadFromFile(CONFIGURATION_FILE);
		} catch (final ConfigurationException e) {
			e.printStackTrace();
			return;
		}
	}

	private final void runExperiment(String name, String seed, String value, String protocol) {
		final String experimentName = name + "_" + seed + "_" + value + "_" + protocol;
		final MeasurementLogger mLogger = MeasurementLogger.getLogger();

		// Cleanup
		RandomGenerator.reset();
		mLogger.resetCounters();
		TreeOverlayGenerator.get().clean();
		ClientAssociationGenerator.get().clean();
		GraphGenerator.get().clean();
		TrafficGeneratorPeerlet.resetCount();

		// Init environment and experiment
		Experiment.initEnvironment();
		final SimulatedExperimentRunner experiment = new SimulatedExperimentRunner();
		experiment.init();

		// Init the servers
		int numPeers = 0;
		final PeerFactory brokerPeerFactory = new ServerFactory();
		experiment.initPeers(numPeers + 1, DreamConfiguration.get().numberOfBrokers, brokerPeerFactory);
		numPeers += DreamConfiguration.get().numberOfBrokers;

		// Init the clients
		final PeerFactory componentPeerFactory = new ClientFactory();
		experiment.initPeers(numPeers + 1, DreamConfiguration.get().numberOfClients, componentPeerFactory);
		numPeers += DreamConfiguration.get().numberOfClients;

		// Init the lock manager
		if (DreamConfiguration.get().consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE || //
		    DreamConfiguration.get().consistencyType == DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED || //
		    DreamConfiguration.get().consistencyType == DreamConfiguration.ATOMIC || //
		    DreamConfiguration.get().consistencyType == DreamConfiguration.SIDUP) {
			final PeerFactory lockManagerFactory = new LockManagerFactory();
			experiment.initPeers(numPeers + 1, 1, lockManagerFactory);
			numPeers++;
		}

		// Start peers
		experiment.startPeers(1, numPeers);

		// Run the simulation
		System.out.println("Starting experiment " + experimentName);
		experiment.runSimulation(Time.inSeconds(DreamConfiguration.get().simulationTimeInSeconds));
		System.out.println("Experiment " + experimentName + " complete");
		mLogger.printResults(experimentName + "_");
		System.out.println("Results written on file");
	}

	private final String getProtocolName(int id) {
		switch (id) {
		case DreamConfiguration.CAUSAL:
			return "causal";
		case DreamConfiguration.SINGLE_SOURCE_GLITCH_FREE:
			return "single_glitch_free";
		case DreamConfiguration.COMPLETE_GLITCH_FREE:
			return "complete_glitch_free";
		case DreamConfiguration.COMPLETE_GLITCH_FREE_OPTIMIZED:
			return "complete_glitch_free_optimized";
		case DreamConfiguration.ATOMIC:
			return "atomic";
		case DreamConfiguration.SIDUP:
			return "sid_up";
		default:
			assert false : id;
			return null;
		}
	}

	@Override
	public NetworkInterfaceFactory createNetworkInterfaceFactory() {
		return new DelayLossNetworkInterfaceFactory(getEventScheduler(), //
		    new DreamDelayModel(DreamConfiguration.get().minCommunicationDelayInMs,
		        DreamConfiguration.get().maxCommunicationDelayInMs));
	}

}
