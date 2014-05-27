package javareact.experiments;

import javareact.client.ComponentFactory;
import javareact.client.TrafficGeneratorPeerlet;
import javareact.generator.GraphsGenerator;
import javareact.generator.RandomGenerator;
import javareact.measurement.MeasurementLogger;
import javareact.registry.RegistryFactory;
import javareact.server.ServerFactory;
import javareact.token_service.TokenServiceFactory;
import protopeer.ConfigurationException;
import protopeer.Experiment;
import protopeer.PeerFactory;
import protopeer.SimulatedExperiment;
import protopeer.network.NetworkInterfaceFactory;
import protopeer.network.delayloss.DelayLossNetworkInterfaceFactory;
import protopeer.network.delayloss.UniformDelayModel;
import protopeer.util.quantities.Time;

/**
 * This class is used to run multiple experiments while changing some system parameters and save the results for later
 * processing.
 */
public class SimulatedExperimentRunner extends SimulatedExperiment {
  private final static String CONFIGURATION_FILE = "conf/jr.conf";
  private final JavaReactConfiguration config;

  private static final int protocols[] = { JavaReactConfiguration.CAUSAL, JavaReactConfiguration.GLITCH_FREE, JavaReactConfiguration.ATOMIC };

  public SimulatedExperimentRunner() {
    config = JavaReactConfiguration.getSingleton();
  }

  public final void runExperiments() {
    for (int seed = 0; seed < 1; seed++) {
      runFromFile(seed);
      runDefault(seed);
      runDefaultCentralized(seed);
      runLocality(seed);
      runNumBrokers(seed);
      runNumGraphNodes(seed);
      runNumGraphDependencies(seed);
      runTimeBetweenEvents(seed);
    }
  }

  private final void runFromFile(int seed) {
    loadFromFile();
    config.seed = seed;
    runExperiment("fromFile", "0", getProtocolName(config.consistencyType));
  }

  private final void runDefault(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      runExperiment("default", String.valueOf(0), getProtocolName(i));
    }
  }

  private final void runDefaultCentralized(int seed) {
    loadFromFile();
    config.seed = seed;
    config.numberOfBrokers = 1;
    config.linkLength = 2;
    config.minCommunicationDelayInMs *= config.linkLength;
    config.maxCommunicationDelayInMs *= config.linkLength;
    for (int i : protocols) {
      config.consistencyType = i;
      runExperiment("centralized", String.valueOf(0), getProtocolName(i));
    }
  }

  private final void runLocality(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      for (float locality = 0; locality <= 1; locality += 0.2) {
        config.locality = locality;
        runExperiment("locality", String.valueOf(locality), getProtocolName(i));
      }
    }
  }

  private final void runNumBrokers(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      for (int numBrokers = 1; numBrokers < 32; numBrokers += 5) {
        config.numberOfBrokers = numBrokers;
        runExperiment("numBrokers", String.valueOf(numBrokers), getProtocolName(i));
      }
    }
  }

  private final void runNumGraphNodes(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      for (int numGraphNodes = 2; numGraphNodes <= 16; numGraphNodes += 2) {
        config.numGraphNodes = numGraphNodes;
        runExperiment("numGraphNodes", String.valueOf(numGraphNodes), getProtocolName(i));
      }
    }
  }

  private final void runNumGraphDependencies(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      config.numGraphNodes = 10;
      for (int numGraphDependencies = 1; numGraphDependencies <= 8; numGraphDependencies++) {
        config.numGraphDependencies = numGraphDependencies;
        runExperiment("numGraphDependencies", String.valueOf(numGraphDependencies), getProtocolName(i));
      }
    }
  }

  private final void runTimeBetweenEvents(int seed) {
    loadFromFile();
    config.seed = seed;
    for (int i : protocols) {
      config.consistencyType = i;
      for (int timeBetweenEvents = 1; timeBetweenEvents <= 1000;) {
        config.simulationTimeInSeconds = 10 * timeBetweenEvents;
        config.epochDuration = 2 * timeBetweenEvents;
        config.minTimeBetweenEventsInMs = Math.min(1, timeBetweenEvents - 1);
        config.maxTimeBetweenEventsInMs = timeBetweenEvents + 1;
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

  private void loadFromFile() {
    try {
      config.loadFromFile(CONFIGURATION_FILE);
    } catch (ConfigurationException e) {
      e.printStackTrace();
      return;
    }
  }

  private final void runExperiment(String name, String value, String protocol) {
    String experimentName = name + "_" + value + "_" + protocol;
    MeasurementLogger mLogger = MeasurementLogger.getLogger();

    // Cleanup
    RandomGenerator.reset();
    mLogger.resetCounters();
    GraphsGenerator.get().clean();
    TrafficGeneratorPeerlet.resetCount();

    // Init environment and experiment
    Experiment.initEnvironment();
    SimulatedExperimentRunner experiment = new SimulatedExperimentRunner();
    experiment.init();

    // Init brokers
    int numPeers = 0;
    PeerFactory brokerPeerFactory = new ServerFactory();
    experiment.initPeers(numPeers + 1, config.numberOfBrokers, brokerPeerFactory);
    numPeers += config.numberOfBrokers;

    // Init clients
    PeerFactory componentPeerFactory = new ComponentFactory();
    experiment.initPeers(numPeers + 1, config.numberOfComponents, componentPeerFactory);
    numPeers += config.numberOfComponents;

    // Init token service
    if (config.consistencyType == JavaReactConfiguration.ATOMIC) {
      PeerFactory tokenServiceFactory = new TokenServiceFactory();
      experiment.initPeers(numPeers + 1, 1, tokenServiceFactory);
      numPeers++;
    }

    // Init registry
    if (config.useRegistry) {
      PeerFactory registryFactory = new RegistryFactory();
      experiment.initPeers(numPeers + 1, 1, registryFactory);
      numPeers++;
    }

    // Start peers
    experiment.startPeers(1, numPeers);

    // Run the simulation
    System.out.println("Starting experiment " + experimentName);
    experiment.runSimulation(Time.inSeconds(config.simulationTimeInSeconds));
    System.out.println("Experiment " + experimentName + " complete");
    mLogger.printResults(experimentName + "_");
    System.out.println("Results written on file");
  }

  private final String getProtocolName(int id) {
    switch (id) {
    case JavaReactConfiguration.CAUSAL:
      return "causal";
    case JavaReactConfiguration.GLITCH_FREE:
      return "glitchFree";
    case JavaReactConfiguration.ATOMIC:
      return "atomic";
    default:
      assert false : id;
      return null;
    }
  }

  @Override
  public NetworkInterfaceFactory createNetworkInterfaceFactory() {
    return new DelayLossNetworkInterfaceFactory(getEventScheduler(), new UniformDelayModel(JavaReactConfiguration.getSingleton().minCommunicationDelayInMs, JavaReactConfiguration.getSingleton().maxCommunicationDelayInMs));
  }

}
