package protopeer;

import java.net.*;

import org.apache.log4j.*;

/**
 * This class inherits from <code>Configuration</code>. The collection of public
 * fields in this class forms the main ProtoPeer configuration. The parameters
 * are accessed by calling <code>MainConfiguration.getSingleton().field</code>
 * 
 * The fields are typically initialized from the "conf/protopeer.conf" file by
 * calling <code>Experiment.initEnvironment()</code>.
 * 
 */
public class MainConfiguration extends Configuration {

	static final Logger logger = Logger.getLogger(MainConfiguration.class);

	public enum IdentifierInitialization {
		UNIFORM, BINARY, GAUSSIAN_MIXTURE, AOL, EXPONENTIAL
	}

	public boolean liveRun;

	public int numPeersInSim;

	public double measurementEpochDuration;

	public double clockOffset;

	public boolean enableMessageSerializationDuringSimulation;

	public boolean enableLightweightSerialization;

	public double bootstrapTimeout;

	public int initialCoreNodes;

	public int initialNodeDegree;

	public IdentifierInitialization identifierInitialization;

	public InetAddress peerIP;

	public int peerPort;

	public InetAddress peerZeroIP;

	public int peerZeroPort;

	public boolean multiThreadedTimers;

	public int peerIndex;

	public double scenarioExecutorTimeZero;

	public long masterSeed;
	
	public String rootMeasurementLogFilename;

	private static MainConfiguration singleton;

	public static MainConfiguration getSingleton() {
		if (singleton == null) {
			singleton = new MainConfiguration();
		}
		return singleton;
	}

}
