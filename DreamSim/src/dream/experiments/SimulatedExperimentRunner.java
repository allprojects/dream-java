package dream.experiments;

import dream.client.ClientFactory;
import dream.client.TrafficGeneratorPeerlet;
import dream.generator.GraphGenerator;
import dream.generator.RandomGenerator;
import dream.locking.LockManagerFactory;
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
import protopeer.network.delayloss.UniformDelayModel;
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
      DreamConfiguration.ATOMIC //
  };

  public final void runExperiments() {
    // runFromFile(0);
    for (int seed = 0; seed < 5; seed++) {
      runFromFile(seed);
      runDefault(seed);
      runDefaultCentralized(seed);
      runLocality(seed);
      runNumBrokers(seed);
      runNumVars(seed);
      runNumSignals(seed);
      runNumGraphDependencies(seed);
      runTimeBetweenEvents(seed);
      runTimeBetweenReads(seed);
    }
  }

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

  private final void runDefaultCentralized(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    DreamConfiguration.get().numberOfBrokers = 1;
    DreamConfiguration.get().linkLength = 4;
    DreamConfiguration.get().minCommunicationDelayInMs *= DreamConfiguration.get().linkLength;
    DreamConfiguration.get().maxCommunicationDelayInMs *= DreamConfiguration.get().linkLength;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      runExperiment("centralized", String.valueOf(seed), String.valueOf(0), getProtocolName(i));
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

  private final void runNumBrokers(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int numBrokers = 2; numBrokers < 25; numBrokers += 2) {
        DreamConfiguration.get().numberOfBrokers = numBrokers;
        runExperiment("numBrokers", String.valueOf(seed), String.valueOf(numBrokers), getProtocolName(i));
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

  private final void runNumSignals(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int numSignals = 10; numSignals <= 1000;) {
        DreamConfiguration.get().graphNumInnerNodes = numSignals;
        runExperiment("numSignals", String.valueOf(seed), String.valueOf(numSignals), getProtocolName(i));
        if (numSignals < 100) {
          numSignals += 30;
        } else {
          numSignals += 300;
        }
      }
    }
  }

  private final void runNumGraphDependencies(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int dep = 1; dep <= 10; dep++) {
        DreamConfiguration.get().graphMinDepPerNode = Math.max(1, dep - 1);
        DreamConfiguration.get().graphMaxDepPerNode = dep + 1;
        runExperiment("numGraphDependencies", String.valueOf(seed), String.valueOf(dep), getProtocolName(i));
      }
    }
  }

  private final void runTimeBetweenEvents(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int timeBetweenEvents = 1; timeBetweenEvents <= 1000;) {
        DreamConfiguration.get().simulationTimeInSeconds = 10 * timeBetweenEvents;
        DreamConfiguration.get().epochDuration = 2 * timeBetweenEvents;
        DreamConfiguration.get().minTimeBetweenEventsInMs = Math.min(1, timeBetweenEvents - 1);
        DreamConfiguration.get().maxTimeBetweenEventsInMs = timeBetweenEvents + 1;
        runExperiment("timeBetweenEvents", String.valueOf(seed), String.valueOf(timeBetweenEvents), getProtocolName(i));
        if (timeBetweenEvents < 10) {
          timeBetweenEvents += 3;
        } else if (timeBetweenEvents < 100) {
          timeBetweenEvents += 30;
        } else {
          timeBetweenEvents += 300;
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
        DreamConfiguration.get().consistencyType == DreamConfiguration.ATOMIC) {
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
    case DreamConfiguration.ATOMIC:
      return "atomic";
    default:
      assert false : id;
      return null;
    }
  }

  @Override
  public NetworkInterfaceFactory createNetworkInterfaceFactory() {
    return new DelayLossNetworkInterfaceFactory(getEventScheduler(), //
        new UniformDelayModel(DreamConfiguration.get().minCommunicationDelayInMs, DreamConfiguration.get().maxCommunicationDelayInMs));
  }

}
