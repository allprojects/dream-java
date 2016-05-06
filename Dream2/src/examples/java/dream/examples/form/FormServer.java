package dream.examples.form;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class FormServer {

	public static final String NAME = "FormServer";
	private boolean serverStarted;
	private boolean lockManagerStarted;
	private final Logger logger = Logger.getGlobal();// Logger("FormServer");

	private RemoteVar<Integer> working_hours;
	private RemoteVar<Double> euro_per_hour;
	private Signal<Boolean> minimumHours;
	private Signal<Boolean> maximumHours;
	private Signal<Boolean> minimumEuroPerHour;
	private Signal<Double> salary;
	private Signal<Boolean> settingsOkay;

	public FormServer() {
		startServerIfNeeded();
		startLockManagerIfNeeded();

		logger.setLevel(Level.ALL);
		// logger.addHandler(Logger.getGlobal().getHandlers()[0]);
		Consts.hostName = NAME;
		new Var<>("helper", "");
		detectNewSession();
	}

	/**
	 * Look for new clients every 5 seconds
	 */
	private void detectNewSession() {
		Set<String> vars = DreamClient.instance.listVariables();
		for (String str : vars) {
			String host = str.split("@")[1];
			String var = str.split("@")[0];
			if (working_hours == null && var.equalsIgnoreCase("working_hours")) {
				working_hours = new RemoteVar<>(host, var);
				System.out.println("Found Secreatary");
				updateDependencies();
			}
			if (euro_per_hour == null && var.equalsIgnoreCase("euro_per_hour")) {
				euro_per_hour = new RemoteVar<>(host, var);
				System.out.println("Found Boss");
				updateDependencies();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep for 0.5 seconds", e);
		}

		detectNewSession();
	}

	private void updateDependencies() {
		if (euro_per_hour == null || working_hours == null)
			return;

		System.out.println("update Dep");

		minimumHours = new Signal<>("minimumHours", () -> {
			if (working_hours.get() != null)
				return working_hours.get() > 10;
			else
				return false;
		}, working_hours);
		maximumHours = new Signal<>("maximumHours", () -> {
			if (working_hours.get() != null)
				return working_hours.get() < 60;
			else
				return false;
		}, working_hours);
		minimumEuroPerHour = new Signal<>("minimumEuroPerHour", () -> {
			if (euro_per_hour.get() != null)
				return euro_per_hour.get() > 10;
			else
				return false;
		}, working_hours);
		settingsOkay = new Signal<>("settingsOkay", () -> {
			if (minimumHours.get() != null && maximumHours.get() != null && minimumEuroPerHour.get() != null)
				return minimumHours.get() && maximumHours.get() && minimumEuroPerHour.get();
			else
				return false;
		}, minimumHours, maximumHours, minimumEuroPerHour);
		salary = new Signal<>("salary", () -> {
			if (working_hours.get() != null && euro_per_hour.get() != null)
				return working_hours.get() * euro_per_hour.get();
			else
				return 0.0;
		}, working_hours, euro_per_hour);

		System.out.println("update Dep finished");
	}

	public static void main(String[] args) {
		new FormServer();
	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to wait for Server starting", e);
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
			logger.log(Level.SEVERE, "Failed to wait for LockManager starting", e);
		}
	}
}
