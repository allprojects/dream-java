package javareact.common;

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

	public static final int serverPort;
	public static final String serverAddr;

	public static ConsistencyType consistencyType;
	public static String hostName;

	static {
		/**
		 * Load properties
		 */
		try {
			FileInputStream input = new FileInputStream("jr.properties");
			properties.load(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String serverPortProperty = properties.getProperty("serverPort", "9000");
		serverPort = Integer.parseInt(serverPortProperty);

		String serverAddrProperty = properties.getProperty("serverAddr", "localhost");
		serverAddr = "reds-tcp:" + serverAddrProperty + ":" + serverPort;

		String hostNameProperty = properties.getProperty("hostName", "local");
		hostName = hostNameProperty;

		String consistencyTypeProperty = properties.getProperty("consistencyType", "glitch_free").toLowerCase();
		if (consistencyTypeProperty.equals("causal")) {
			consistencyType = ConsistencyType.CAUSAL;
		} else if (consistencyTypeProperty.equals("glitch_free")) {
			consistencyType = ConsistencyType.GLITCH_FREE;
		} else {
			consistencyType = ConsistencyType.ATOMIC;
		}

		/**
		 * Read logging properties
		 */
		LogManager manager = LogManager.getLogManager();
		try {
			manager.readConfiguration(new FileInputStream(new File(LOGGING_PROPERTIES_FILE_NAME)));
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		logger.addHandler(consoleHandler);
	}
}
