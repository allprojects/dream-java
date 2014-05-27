package protopeer.util;

import java.io.*;

import protopeer.measurement.*;

class DummyListener implements MeasurementLoggerListener {
	@Override
	public void measurementEpochEnded(MeasurementLog log, int epochNumber) {
		// TODO implement your own log processing here
		System.out.println(log);
	}
}

public class MeasurementLogTraverser {

	private LogReplayer replayer = new LogReplayer();

	private String measurementFilename;

	private String directoryToTraverse;

	public static void main(String[] args) {
		if (args.length < 2) {
			printUsage();
		}
		try {
			MeasurementLogTraverser traverser = new MeasurementLogTraverser();
			traverser.measurementFilename = args[0];
			traverser.directoryToTraverse = args[1];
			traverser.traverseDirectory(new File(traverser.directoryToTraverse));
			traverser.replayer.replayTo(new DummyListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.out.println("MeasurementLogTraverser <measurement_filename> <directory_to_traverse>");
	}

	private void loadLogFromFile(File file) throws IOException, ClassNotFoundException {
		replayer.mergeLog(replayer.loadLogFromFile(file.getAbsolutePath()));
	}

	private void traverseDirectory(File directory) throws IOException, ClassNotFoundException {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				traverseDirectory(file);
			} else if (file.getName().equals(measurementFilename)) {
				loadLogFromFile(file);
			}
		}
	}
}
