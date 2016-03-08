package dream.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Consts {
	private static final String LOGGING_PROPERTIES_FILE_NAME = "logging.properties";

	public static final String hostPrefix = "host";
	public static final String objPrefix = "obj";

	public static final double startGraphCreationAtSecond = 1;
	public static final double registerToGraphsGeneratorAtSecond = 2;
	public static final double startNotifyGraphAtSecond = 3;
	public static final double delayBeforeSendingSubscriptions = 1;
	public static final double startSendingEventsAtSecond = 10;
	public static final double startReadingSignalsAtSecond = 10;

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
	}
}
