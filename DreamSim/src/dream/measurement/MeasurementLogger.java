package dream.measurement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dream.experiments.DreamConfiguration;
import protopeer.Experiment;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * This class is used to store and print all the information about the
 * experiment.
 *
 * @author Alessandro Margara
 */
public class MeasurementLogger {
	private final Map<String, Integer> storedMsgs = new HashMap<String, Integer>();
	private final Map<String, Double> storedMsgsSize = new HashMap<String, Double>();
	private final Map<Integer, List<Double>> storedDelays = new HashMap<Integer, List<Double>>();
	private final int epochDuration;
	private final int simulationTime;
	private static MeasurementLogger logger = null;

	public static MeasurementLogger getLogger() {
		if (logger == null) {
			logger = new MeasurementLogger();
		}
		return logger;
	}

	private MeasurementLogger() {
		// Private constructor
		simulationTime = DreamConfiguration.get().simulationTimeInSeconds;
		epochDuration = DreamConfiguration.get().epochDuration;
	}

	public final void resetCounters() {
		storedMsgs.clear();
		storedMsgsSize.clear();
		storedDelays.clear();
	}

	public void saveMessage(Message msg) {
		final String name = msg.getClass().getName();
		addMessage(name, msg);
	}

	public final void saveDelay(double delay) {
		final double time = Experiment.getSingleton().getClock().getCurrentTime();
		final Integer epoch = (int) (time / epochDuration);
		if (!storedDelays.containsKey(epoch)) {
			final List<Double> content = new ArrayList<Double>();
			storedDelays.put(epoch, content);
		}
		storedDelays.get(epoch).add(delay);
	}

	public final void printResults(String filename) {
		printTraffic(filename);
		printDelay(filename);
		printDelayAvg(filename);
	}

	private final void addMessage(String name, Message msg) {
		if (!storedMsgs.containsKey(name)) {
			storedMsgs.put(name, 1);
			storedMsgsSize.put(name, Data.inByte(msg.getSize()));
		} else {
			final int previousCount = storedMsgs.remove(name);
			storedMsgs.put(name, previousCount + 1);
			final double previousSize = storedMsgsSize.remove(name);
			storedMsgsSize.put(name, previousSize + Data.inByte(msg.getSize()));
		}
	}

	private final void printTraffic(String filename) {
		try {
			final String filePrefix = DreamConfiguration.get().resultsDir + filename;
			final FileOutputStream traffic = new FileOutputStream(filePrefix + "Traffic");
			final FileOutputStream trafficByte = new FileOutputStream(filePrefix + "TrafficByte");
			for (final String s : storedMsgs.keySet()) {
				final int val = storedMsgs.get(s).intValue();
				traffic.write((s + "\t" + val + "\t" + (double) val / simulationTime + "\n").getBytes());
			}
			for (final String s : storedMsgsSize.keySet()) {
				final int val = storedMsgsSize.get(s).intValue();
				trafficByte.write((s + "\t" + val + "\t" + (double) val / simulationTime + "\n").getBytes());
			}
			traffic.close();
			trafficByte.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private final void printDelay(String filename) {
		try {
			final String filePrefix = DreamConfiguration.get().resultsDir + filename;
			final FileOutputStream delay = new FileOutputStream(filePrefix + "Delay");
			final List<Integer> keyList = new ArrayList<Integer>(storedDelays.keySet());
			Collections.sort(keyList);
			for (final Integer label : keyList) {
				double sum = 0;
				final double size = storedDelays.get(label).size();
				for (final Double val : storedDelays.get(label)) {
					sum += val;
				}
				final double avgVal = sum / size;
				delay.write((label * epochDuration / 1000 + "\t" + avgVal + "\n").getBytes());
			}
			delay.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private final void printDelayAvg(String filename) {
		try {
			final String filePrefix = DreamConfiguration.get().resultsDir + filename;
			final FileOutputStream delay = new FileOutputStream(filePrefix + "DelayAvg");
			double sum = 0;
			double count = 0;
			for (final Integer key : storedDelays.keySet()) {
				final List<Double> values = storedDelays.get(key);
				for (final Double val : values) {
					sum += val;
					count++;
				}
			}
			final double avgVal = sum / count;
			delay.write((String.valueOf(avgVal) + "\n").getBytes());
			delay.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
