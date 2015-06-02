package protopeer.measurement;

import java.io.*;
import java.util.*;

/**
 * A utility class that merges several logs and replays them to a
 * <code> MeasurmentLoggerListener </code>.
 * 
 */
public class LogReplayer {

	private MeasurementLog completeLog = new MeasurementLog();

	public void mergeLog(MeasurementLog log) {
		completeLog.mergeWith(log);
	}

	public void replayTo(MeasurementLoggerListener listener) {
		try {
			for (int epochNumber = completeLog.getMinEpochNumber(); epochNumber <= completeLog.getMaxEpochNumber(); epochNumber++) {
				listener.measurementEpochEnded(completeLog, epochNumber);
			}
		} catch (NoSuchElementException e) {
			// the completeLog is empty
		}
	}

	public MeasurementLog loadLogFromFile(String filename) throws IOException, ClassNotFoundException {
		MeasurementLog combinedLog=new MeasurementLog();
		ObjectInputStream logIn = null;
		BufferedInputStream logFileIn = null;		
		try {
			logFileIn = new BufferedInputStream(new FileInputStream(filename), 1024 * 1024);
			logIn = new ObjectInputStream(logFileIn);			
			while (logFileIn.available() > 0) {				
				Object object = logIn.readObject();
				MeasurementLog log = (MeasurementLog) object;
				combinedLog.mergeWith(log);
			}
		} catch (IOException ex) {			 
			return combinedLog;
		} finally {
			if (logFileIn != null) {
				logFileIn.close();
			}
			if (logIn != null) {
				logIn.close();
			}
		}				
		return combinedLog;
	}

}
