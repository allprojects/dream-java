package dream.experiments;

import dream.client.ClientFactory;
import dream.client.TrafficGeneratorPeerlet;
import dream.generator.GraphsGenerator;
import dream.generator.RandomGenerator;
import dream.locking.LockManagerFactory;
import dream.measurement.MeasurementLogger;
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
    // FIXME
    runFromFile(0);

    // for (int seed = 0; seed < 1; seed++) {
    // runFromFile(seed);
    // runDefault(seed);
    // runDefaultCentralized(seed);
    // runLocality(seed);
    // runNumBrokers(seed);
    // runNumGraphNodes(seed);
    // runNumGraphDependencies(seed);
    // runTimeBetweenEvents(seed);
    // }
  }

  private final void runFromFile(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    runExperiment("fromFile", "0", getProtocolName(DreamConfiguration.get().consistencyType));
  }

  private final void runDefault(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      runExperiment("default", String.valueOf(0), getProtocolName(i));
    }
  }

  private final void runDefaultCentralized(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    DreamConfiguration.get().numberOfBrokers = 1;
    DreamConfiguration.get().linkLength = 2;
    DreamConfiguration.get().minCommunicationDelayInMs *= DreamConfiguration.get().linkLength;
    DreamConfiguration.get().maxCommunicationDelayInMs *= DreamConfiguration.get().linkLength;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      runExperiment("centralized", String.valueOf(0), getProtocolName(i));
    }
  }

  private final void runLocality(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (float locality = 0; locality <= 1; locality += 0.2) {
        DreamConfiguration.get().locality = locality;
        runExperiment("locality", String.valueOf(locality), getProtocolName(i));
      }
    }
  }

  private final void runNumBrokers(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int numBrokers = 1; numBrokers < 32; numBrokers += 5) {
        DreamConfiguration.get().numberOfBrokers = numBrokers;
        runExperiment("numBrokers", String.valueOf(numBrokers), getProtocolName(i));
      }
    }
  }

  private final void runNumGraphNodes(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      for (int numGraphNodes = 2; numGraphNodes <= 16; numGraphNodes += 2) {
        DreamConfiguration.get().numGraphNodes = numGraphNodes;
        runExperiment("numGraphNodes", String.valueOf(numGraphNodes), getProtocolName(i));
      }
    }
  }

  private final void runNumGraphDependencies(int seed) {
    loadFromFile();
    DreamConfiguration.get().seed = seed;
    for (final int i : protocols) {
      DreamConfiguration.get().consistencyType = i;
      DreamConfiguration.get().numGraphNodes = 10;
      for (int numGraphDependencies = 1; numGraphDependencies <= 8; numGraphDependencies++) {
        DreamConfiguration.get().numGraphDependencies = numGraphDependencies;
        runExperiment("numGraphDependencies", String.valueOf(numGraphDependencies), getProtocolName(i));
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
        runExperiment("timeBetweenEvents", String.valueOf(timeBetweenEvents), getProtocolName(i));
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

  private final void loadFromFile() {
    try {
      DreamConfiguration.get().loadFromFile(CONFIGURATION_FILE);
    } catch (final ConfigurationException e) {
      e.printStackTrace();
      return;
    }
  }

  private final void runExperiment(String name, String value, String protocol) {
    final String experimentName = name + "_" + value + "_" + protocol;
    final MeasurementLogger mLogger = MeasurementLogger.getLogger();

    // Cleanup
    RandomGenerator.reset();
    mLogger.resetCounters();
    GraphsGenerator.get().clean();
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
    if (DreamConfiguration.get().consistencyType == DreamConfiguration.ATOMIC) {
      final PeerFactory tokenServiceFactory = new LockManagerFactory();
      experiment.initPeers(numPeers + 1, 1, tokenServiceFactory);
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
