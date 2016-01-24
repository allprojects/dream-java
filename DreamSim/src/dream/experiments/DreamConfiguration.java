package dream.experiments;

import protopeer.Configuration;
import protopeer.ConfigurationException;

/**
 * This is a class for the JavaReact simulation configuration.
 */
public class DreamConfiguration extends Configuration {
  public static final int CAUSAL = 1;
  public static final int SINGLE_SOURCE_GLITCH_FREE = 2;
  public static final int COMPLETE_GLITCH_FREE = 3;
  public static final int ATOMIC = 4;

  private static DreamConfiguration singleton;

  public static DreamConfiguration get() {
    if (singleton == null) {
      singleton = new DreamConfiguration();
    }
    return singleton;
  }

  @Override
  public void loadFromFile(String filename) throws ConfigurationException {
    super.loadFromFile(filename);
    // Put here validity checks on configuration values
    // Launch a ConfigurationException if some of them is invalid
  }

  /**
   * Directory to save results
   */
  public String resultsDir;

  /**
   * Simulation time in seconds
   */
  public int simulationTimeInSeconds;

  /**
   * Duration of the epoch used to sample delays
   */
  public int epochDuration;

  /**
   * Random seed used during the simulation
   */
  public int seed;

  /**
   * Number of brokers
   */
  public int numberOfBrokers;

  /**
   * Number of clients
   */
  public int numberOfClients;

  /**
   * Initial broker topology 1 = LINEAR 2 = STAR 3 = SCALEFREE
   */
  public int brokersTopologyType;

  /**
   * Association for the components UNIFORM_LOWEST_ID=1 UNIFORM_HIGHEST_ID=2
   * UNIFORM_ALTERNATE_ID=3 UNIFORM_RANDOM_ID=4
   */
  public int clientsAssociationType;

  /**
   * Percentage of brokers that are pure forwarders.
   */
  public double percentageOfPureForwarders;

  /**
   * Max communication delay for communication interface
   */
  public double maxCommunicationDelayInMs;

  /**
   * Min communication delay for communication interface
   */
  public double minCommunicationDelayInMs;

  /**
   * Real (physical) length of each virtual link
   */
  public int linkLength;

  /**
   * Consistency type CAUSAL = 1 SINGLE_GLITCH_FREE = 2 COMPLETE_GLITCH_FREE = 3
   * ATOMIC = 4
   */
  public int consistencyType;

  /**
   * Number of reactive graphs
   */
  public int numGraphs;

  /**
   * Number of nodes per graph
   */
  public int numGraphNodes;

  /**
   * Number of dependencies per node
   */
  public int numGraphDependencies;

  /**
   * Locality (range 0--1).
   *
   * 0: an expression can involve reactive on every client.
   *
   * 1: an expression is entirely internal to a node.
   */
  public double locality;

  /**
   * Min time between two updates to a var
   */
  public int minTimeBetweenEventsInMs;

  /**
   * Max time between two updates to a var
   */
  public int maxTimeBetweenEventsInMs;

  /**
   * Min time between two reads of a signal
   */
  public int minTimeBetweenSignalReadsInMs;

  /**
   * Max time between two reads of a signal
   */
  public int maxTimeBetweenSignalReadsInMs;

  /**
   * Duration of a read lock (how long the lock is retained)
   */
  public int readLockDurationInMs;

}
