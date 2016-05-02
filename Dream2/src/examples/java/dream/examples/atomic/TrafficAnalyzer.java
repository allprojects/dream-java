package dream.examples.atomic;

import java.util.ArrayList;
import java.util.Random;

import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class TrafficAnalyzer {

	public static final String GREEN_LIGHT_DURATIONS = "greenLightDurations";
	private static final int TOTAL_SECONDS = 60;
	private boolean serverStarted = false;
	private boolean lockManagerStarted = false;
	public static final String NAME = "TrafficAnalyzer";
	private int totalTrafficSignals = 4;
	private Var<ArrayList<Integer>> greenLightDurations; // each index has green
															// light duration
															// for ith signal
	public static String nodeOneString = "NodeOneGreenLightDuration";
	private Random randomNumber;
	private int prioritize;

	public static void main(String[] args) {
		new TrafficAnalyzer().start();
	}

	public void start() {
		startServerIfNeeded();
		startLockManagerIfNeeded();
		randomNumber = new Random();

		Consts.hostName = NAME;

		greenLightDurations = new Var<ArrayList<Integer>>(GREEN_LIGHT_DURATIONS, new ArrayList<Integer>());
		updateTimeSignals();

	}

	private void updateTimeSignals() {
		prioritize = randomNumber.nextInt(totalTrafficSignals);

		greenLightDurations.modify((old) -> updateGreenLightDurationList(old));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateTimeSignals();
	}

	private void updateGreenLightDurationList(ArrayList<Integer> old) {
		old.clear();
		for (int i = 0; i < totalTrafficSignals; i++) {
			old.add(i, prepareValuesForEachTrafficSignal(i));
		}

	}

	// This will allot each signal duration such that sum of all the signal's
	// duration = 60
	private int prepareValuesForEachTrafficSignal(int index) {
		if (index == prioritize)
			return 30;
		else
			return 10;

	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final void startLockManagerIfNeeded() {
		if (!lockManagerStarted) {
			LockManagerLauncher.start();
			lockManagerStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
