package protopeer;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.time.*;
import protopeer.util.*;

/**
 * This is the main class for launching the experiments with ProtoPeer. Before
 * calling any methods to <code>Experiment</code>, you need to initialize
 * ProtoPeer by calling <code>Experiment.initEnvironment()</code>. Then you
 * create an Experiment instance and call <code>init()</code> on it. Then you
 * create and initialize the peers by calling <code>initPeers()</code>.
 * 
 */
public abstract class Experiment {

	protected static Logger logger;

	protected static Experiment singleton;

	protected Vector<Peer> peers = new Vector<Peer>();

	protected Clock clock;

	protected NetworkInterfaceFactory networkInterfaceFactory;

	protected MeasurementLog rootMeasurementLog;

	protected MeasurementLogger rootMeasurementLogger;

	// FIXME: this should probably be moved out of here
	protected ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<ExecutionContext>();

	/**
	 * Constructs the <code>Experiment</code> and makes this instance the
	 * singleton that <code>getSingleton()</code> will return.
	 * 
	 */
	public Experiment() {
		setSingleton(this);
	}

	/**
	 * 
	 * Periodically merges all the peer measurement logs into the root
	 * measurement log. Waits one full measurement epoch after the epoch ends
	 * before the merge to account for the asynchrony of epoch ending at all the
	 * peers.
	 * 
	 */
	private class MeasurementLogMerger implements MeasurementLoggerListener {

		public void measurementEpochEnded(MeasurementLog log, int epochNumber) {
			if (epochNumber >= 1) {
				for (Peer peer : getPeers()) {
					if (peer != null) {
						log.mergeWith(peer.getMeasurementLogger().getMeasurementLog(), epochNumber - 1);
					}
				}
			}
		}
	}

	/**
	 * Returns the singleton instance of the Experiment. Returns
	 * <code>null</code> until <code>init()</code> is called.
	 * 
	 * @return singleton instance
	 */
	public static Experiment getSingleton() {
		return singleton;
	}

	protected static void setSingleton(Experiment singleton) {
		Experiment.singleton = singleton;
	}

	/**
	 * Called whenever a clock needs to be created. Each peer has a different
	 * clock instance. The <code>Experiment</code> also creates its own clock
	 * (mainly for logging purposes).
	 * 
	 */
	public abstract Clock createClock();

	/**
	 * Called whenever a network factory needs to be created. Typically called
	 * only once during an experiment. The network factory is used to create all
	 * the network interfaces for all the peers used in this experiment.
	 * 
	 */
	public abstract NetworkInterfaceFactory createNetworkInterfaceFactory();

	/**
	 * Returns the desired address that a peer with <code>peerIndex</code>
	 * should bind to. This address is passed as an argument when calling
	 * <code>Peer.init()</code>.
	 * 
	 * @param peerIndex
	 *            the index of the peer
	 * @return the network address of the peer with the given index, may be null
	 *         which indicates the address should be auto-allocated by the
	 *         network interface factory
	 */
	public abstract NetworkAddress getAddressToBindTo(int peerIndex);

	/**
	 * Initializes the experiment, creates the experiment's clock, the network
	 * interface factory and sets up the measurement logging.
	 * 
	 * @param peerFactory
	 *            the peerFactory to use to create new peers
	 */
	public void init() {
		clock = createClock();
		rootMeasurementLog = new MeasurementLog();
		rootMeasurementLogger = new MeasurementLogger(clock, rootMeasurementLog);
		rootMeasurementLogger.addMeasurementLoggerListener(new MeasurementLogMerger());
		String rootMeasurementLogFilename = MainConfiguration.getSingleton().rootMeasurementLogFilename;
		if (rootMeasurementLogFilename != null && rootMeasurementLogFilename.length() > 0) {
			rootMeasurementLogger.addMeasurementLoggerListener(new MeasurementFileDumper(rootMeasurementLogFilename));
		}
		networkInterfaceFactory = createNetworkInterfaceFactory();
	}

	/**
	 * Creates and initializes the peers.
	 * 
	 * @param startIndex
	 *            the starting peer index
	 * @param numPeers
	 *            number of peers to create and initialize
	 */
	public void initPeers(int startIndex, int numPeers, PeerFactory peerFactory) {
		for (int peerIndex = startIndex; peerIndex < startIndex + numPeers; peerIndex++) {
			Peer peer = peerFactory.createPeer(peerIndex, this);
			peer.init(networkInterfaceFactory, createClock(), getAddressToBindTo(peerIndex));
			peers.setSize(startIndex + numPeers);
			peers.setElementAt(peer, peerIndex);
		}
	}

	/**
	 * Removes the peer with the given index number from the experiment. Stops
	 * the peer if it is running.
	 * 
	 * @param peerIndex
	 */

	public void removePeer(int peerIndex) {
		Peer peer = peers.elementAt(peerIndex);
		// stop the running peer
		if (peer.getState() == Peer.PeerState.RUNNING) {
			peer.stop();
		}
		// deactivate the clock, the timers are still scheduled but they won't
		// call their listeners when they expire
		// FIXME: this might prevent the peer state garbage collection when the
		// peer has timers scheduled with a large delay, need to cancel all the
		// scheduled timers
		peer.getClock().deactivate();
		peers.setElementAt(null, peerIndex);
	}

	/**
	 * Removes the peer with the given range of index numbers. Stops the peers
	 * if they are running.
	 * 
	 * @param startIndex
	 *            the first index in range
	 * @param numPeers
	 *            number of peers to stop
	 */
	public void removePeers(int startIndex, int numPeers) {
		for (int peerIndex = startIndex; peerIndex < startIndex + numPeers; peerIndex++) {
			removePeer(peerIndex);
		}
	}

	/**
	 * Adds a peer to the experiment.
	 * 
	 * @param peer
	 */
	public void addPeer(Peer peer) {
		peers.add(peer.getIndexNumber(), peer);
	}

	/**
	 * Calls <code>start()</code> on peers with specified indices.
	 * 
	 * @param startIndex
	 *            the starting peer index
	 * @param numPeers
	 *            number of peers
	 */
	public void startPeers(int startIndex, int numPeers) {
		for (int peerIndex = startIndex; peerIndex < startIndex + numPeers; peerIndex++) {
			peers.elementAt(peerIndex).start();
		}
	}

	/**
	 * Returns the vector of peers created for this experiment. The peer with
	 * index <code>i</code> has index <code>i</code> in the vector.
	 * 
	 * @return
	 */
	public Vector<Peer> getPeers() {
		// FIXME: make the vector unmodifiable for safety
		return peers;
	}

	/**
	 * Returns the clock used by this <code>Experiment</code> instance. Note
	 * that this clock instance is different from the clocks that the peers use.
	 * All clocks are created using the <code>createClock()</code> calls.
	 * 
	 * @return the experiment's clock or <code>null</code> if it hasn't been
	 *         created yet
	 */
	public Clock getClock() {
		return clock;
	}

	/**
	 * @return the <code>NetworkInterfaceFactory</code> used for the experiment
	 *         or <code>null</code> if it hasn't been created yet.
	 */
	public NetworkInterfaceFactory getNetworkInterfaceFactory() {
		return networkInterfaceFactory;
	}

	/**
	 * Tries to open the file with a given name, if not present attempts to open
	 * the resource with the given name
	 * 
	 * @param name
	 * @return
	 */
	private static InputStream getInputStreamFromFileOrResource(String name) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(name);
		} catch (Exception e) {
			inputStream = Experiment.class.getClassLoader().getResourceAsStream(name);
		}
		return inputStream;
	}

	/**
	 * Attempts to load the Properties form a file, if it doesn't exist it tries
	 * to open a resource with the same name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private static Properties loadPropertiesFromFileOrResource(String name) throws IOException {
		Properties properties = new Properties();
		InputStream input = getInputStreamFromFileOrResource(name);
		if (input == null) {
			logger.fatal("Could not load properties from file or resource " + name);
			return null;
		}
		input.available();
		properties.load(input);
		return properties;
	}

	private static void loadConfiguration() throws ConfigurationException {
		try {
			// load the log4j conf
			PropertyConfigurator.configure(loadPropertiesFromFileOrResource("conf/log4j.properties"));

			// initialize the logger only AFTER the PropertyConfigurator has
			// loaded the log4j config
			logger = Logger.getLogger(Experiment.class);

			// load the main configuration
			MainConfiguration.getSingleton()
					.loadFromProperties(loadPropertiesFromFileOrResource("conf/protopeer.conf"));
			if (MainConfiguration.getSingleton().enableLightweightSerialization) {
				LightweightSerialization.getSingleton().loadMapFromFile("conf/LightweightSerialization.map");
			}

			// load the measurement configuration
			MeasurementConfiguration.getSingleton().loadFromProperties(
					loadPropertiesFromFileOrResource("conf/measurement.conf"));

		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Initializes the JVM-wide <code>RandomnessSource</code> from the
	 * <code>MainConfiguration.masterSeed</code>.
	 */
	private static void initRandomnessSource() {
		// FIXME: there should be one instance of RandomnessSource per
		// experiment, we shouldn't be multiplying those statically accessible
		// objects (currently statically accessible: RandomnessSource,
		// Experiment and all the
		// Configurations).
		long masterSeed = MainConfiguration.getSingleton().masterSeed;
		if (masterSeed == 0) {
			RandomnessSource.init(System.nanoTime());
		} else {
			RandomnessSource.init(masterSeed);
		}
	}

	/**
	 * Initializes the ProtoPeer environment. Must be called before any other
	 * <code>Experiment</code> methods. Loads the
	 * <code>conf/log4j.properties</code> and <code>conf/protopeer.conf</code>
	 * and initializes the <code>RandomnessSource</code>.
	 * 
	 */
	public static void initEnvironment() {
		try {
			loadConfiguration();
			initRandomnessSource();
		} catch (ConfigurationException e) {
			logger.fatal("Configuration problems, aborting...", e);
			System.err.println("Configuration problems, aborting...");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param address
	 *            the address of the peer
	 * @return the peer that has the <code>address</code>, null if not found
	 */
	public Peer getPeerWithNetworkAddress(NetworkAddress address) {
		for (Peer peer : getPeers()) {
			if (peer.getNetworkAddress().equals(address)) {
				return peer;
			}
		}
		return null;
	}

	/**
	 * Get the execution context of the current thread.
	 * 
	 * @return the execution context of the current thread or <code>null</code>
	 *         if no execution context is currently set.
	 */
	public ExecutionContext getExecutionContext() {
		return this.executionContext.get();
	}

	/**
	 * Make the current thread enter an execution context. This will nullify the
	 * effect of any previous calls to this method.
	 * 
	 * @param executionContext
	 *            the execution context to enter
	 */
	public void enterExecutionContext(ExecutionContext executionContext) {
		this.executionContext.set(executionContext);
	}

	/**
	 * Make the current thread leave the execution context it's currently in.
	 * 
	 * @return the execution context the thread was in
	 */
	public ExecutionContext leaveExecutionContext() {
		ExecutionContext out = getExecutionContext();
		this.executionContext.set(null);
		return out;
	}

	/**
	 * Returns the experiment-wide measurement log where the logs of all the
	 * peers are aggregated together at the end of every measurement epoch.
	 * 
	 * @return
	 */
	public MeasurementLog getRootMeasurementLog() {
		return rootMeasurementLog;
	}

	/**
	 * Returns the experiment-wide measurement logger. Can be accessed from
	 * anywhere by calling
	 * <code>Experiment.getSingleton().getMeasurementLogger()</code>. Peers
	 * should be using their own measurement loggers, not this one.
	 * 
	 * @return
	 */
	public MeasurementLogger getRootMeasurementLogger() {
		return rootMeasurementLogger;
	}
}
