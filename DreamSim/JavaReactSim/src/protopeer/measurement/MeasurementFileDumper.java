package protopeer.measurement;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.network.*;

/**
 * Serializes the MeasurmentLog to a file at the end of each epoch.
 * 
 */
public class MeasurementFileDumper implements MeasurementLoggerListener {

	private static final Logger logger = Logger.getLogger(MeasurementFileDumper.class);

	private ObjectOutputStream measurementsOut;

	public MeasurementFileDumper(String filename) {
		try {
			this.measurementsOut = new ObjectOutputStream(new FileOutputStream(filename));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void measurementEpochEnded(MeasurementLog log, int epochNumber) {
		try {
			if (measurementsOut != null) {
				MeasurementLog subLog = log.getSubLog(epochNumber-1, epochNumber );
				if (logger.isDebugEnabled()) {
					try {
						logger.debug("Dumping the sublog, minEpoch: " + subLog.getMinEpochNumber() + ", maxEpoch: "
								+ subLog.getMaxEpochNumber());
					} catch (NoSuchElementException e) {
						logger.debug("Dumping an empty sublog");
					}
				}
				measurementsOut.writeObject(subLog);
				measurementsOut.flush();
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

}
