package javareact.experiments;

import protopeer.Configuration;
import protopeer.ConfigurationException;

/**
 * This is a class for the JavaReact simulation configuration.
 */
public class JavaReactConfiguration extends Configuration {
  public static final int CAUSAL = 1;
  public static final int GLITCH_FREE = 2;
  public static final int ATOMIC = 3;

  private static JavaReactConfiguration singleton;

  public static JavaReactConfiguration getSingleton() {
    if (singleton == null) {
      singleton = new JavaReactConfiguration();
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
   * Simulation time in seconds
   */
  public int simulationTimeInSeconds;

  /**
   * Random seed used during the simulation
   */
  public int seed;

  /**
   * Number of brokers
   */
  public int numberOfBrokers;

  /**
   * Number of components
   */
  public int numberOfComponents;

  /**
   * Initial broker topology 1 = LINEAR 2 = STAR 3 = SCALEFREE
   */
  public int brokersTopologyType;

  /**
   * Association for the components UNIFORM_LOWEST_ID=1 UNIFORM_HIGHEST_ID=2 UNIFORM_ALTERNATE_ID=3 UNIFORM_RANDOM_ID=4
   */
  public int componentsAssociationType;

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
   * Consistency type CAUSAL=1 GLITCH_FREE=2 ATOMIC=3
   */
  public int consistencyType;

  /**
   * Use registry
   */
  public boolean useRegistry;

  /**
   * Duration of the epoch used to sample delays
   */
  public int epochDuration;

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
   * Min time between two events in a client
   */
  public int minTimeBetweenEventsInMs;

  /**
   * Max time between two events in a client
   */
  public int maxTimeBetweenEventsInMs;

  /**
   * Max value for an observable object
   */
  public int maxObservableValue;

}
