package dream.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Consts {
	private static final Properties properties = new Properties();
	private static final String LOGGING_PROPERTIES_FILE_NAME = "logging.properties";

	public static String serverAddr;
	public static String lockManagerAddr;

	public static int serverPort;
	public static int lockManagerPort;

	public static ConsistencyType consistencyType;
	private static String hostName;

	public static boolean enableEvaluation;
	public static String trafficMeasurementFile = "./traffic.txt";
	public static String delayMeasurementFile = "./delay.txt";

	static {
		/**
		 * Read logging properties
		 */
		final LogManager manager = LogManager.getLogManager();
		try {
			manager.readConfiguration(new FileInputStream(new File(LOGGING_PROPERTIES_FILE_NAME)));
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		final ConsoleHandler consoleHandler = new ConsoleHandler();
		logger.addHandler(consoleHandler);

		/**
		 * Load properties
		 */
		try {
			final FileInputStream input = new FileInputStream("dream.properties");
			properties.load(input);
			input.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final String serverAddrProperty = properties.getProperty("serverAddr", "localhost");
		final String serverPortProperty = properties.getProperty("serverPort", "9000");
		serverPort = Integer.parseInt(serverPortProperty);
		serverAddr = "reds-tcp:" + serverAddrProperty + ":" + serverPort;

		final String lockManagerAddrProperty = properties.getProperty("lockManagerAddr", "localhost");
		final String lockManagerPortProperty = properties.getProperty("serverManagerPort", "9999");
		lockManagerPort = Integer.parseInt(lockManagerPortProperty);
		lockManagerAddr = "reds-tcp:" + lockManagerAddrProperty + ":" + lockManagerPort;

		final String hostNameProperty = properties.getProperty("hostName", "local");
		hostName = hostNameProperty;
		trafficMeasurementFile = "./" + hostName + "_traffic.txt";
		delayMeasurementFile = "./" + hostName + "_delay.txt";

		final String consistencyTypeProperty = properties.getProperty("consistencyType", "single_glitch_free")
				.toLowerCase();
		if (consistencyTypeProperty.equals("causal")) {
			consistencyType = ConsistencyType.CAUSAL;
		} else if (consistencyTypeProperty.equals("single_glitch_free")) {
			consistencyType = ConsistencyType.SINGLE_SOURCE_GLITCH_FREE;
		} else if (consistencyTypeProperty.equals("complete_glitch_free")) {
			consistencyType = ConsistencyType.COMPLETE_GLITCH_FREE;
		} else if (consistencyTypeProperty.equals("atomic")) {
			consistencyType = ConsistencyType.ATOMIC;
		} else {
			logger.warning("Unknown consistency type. Using single source glitch free as default.");
		}

		final String enableEvaluationProperty = properties.getProperty("enableEvaluation", "false");
		enableEvaluation = Boolean.valueOf(enableEvaluationProperty);
	}

	public static final void setHostName(String name) {
		hostName = name;
		trafficMeasurementFile = "./" + hostName + "_traffic.txt";
		delayMeasurementFile = "./" + hostName + "_delay.txt";
	}

	public static final String getHostName() {
		return hostName;
	}
}
