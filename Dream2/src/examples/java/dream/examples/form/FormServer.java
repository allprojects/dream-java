package dream.examples.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class FormServer {

	public static final String NAME = "FormServer";
	private boolean serverStarted;
	private boolean lockManagerStarted;
	protected final Logger logger = Logger.getLogger(NAME);

	protected RemoteVar<Integer> working_hours;
	protected RemoteVar<Double> euro_per_hour;

	public FormServer() {
		startServerIfNeeded();
		startLockManagerIfNeeded();

		logger.setLevel(Level.ALL);
		logger.addHandler(Logger.getGlobal().getHandlers()[0]);

		Consts.hostName = NAME;
		DreamClient.instance.connect();
		detectNewSession();
	}

	/**
	 * Look for new clients every 5 seconds
	 */
	private void detectNewSession() {
		while (euro_per_hour == null || working_hours == null) {
			for (String str : DreamClient.instance.listVariables()) {
				String host = str.split("@")[1];
				String var = str.split("@")[0];
				if (working_hours == null && var.equalsIgnoreCase("working_hours")) {
					working_hours = new RemoteVar<>(host, var);
					logger.fine("Found Secretary");
				} else if (euro_per_hour == null && var.equalsIgnoreCase("euro_per_hour")) {
					euro_per_hour = new RemoteVar<>(host, var);
					logger.fine("Found Boss");
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Failed to sleep for 0.5 seconds", e);
			}
		}
		createDependencies();
	}

	protected void createDependencies() {
		logger.fine("Building Dependencies");

		final Signal<Boolean> minimumHours = new Signal<>("minimumHours", () -> {
			if (working_hours.get() != null)
				return working_hours.get() > 10;
			else
				return false;
		}, working_hours);

		final Signal<Boolean> maximumHours = new Signal<>("maximumHours", () -> {
			if (working_hours.get() != null)
				return working_hours.get() < 60;
			else
				return false;
		}, working_hours);

		final Signal<Boolean> minimumEuroPerHour = new Signal<>("minimumEuroPerHour", () -> {
			if (euro_per_hour.get() != null)
				return euro_per_hour.get() > 10;
			else
				return false;
		}, euro_per_hour);

		new Signal<>("settingsOkay", () -> {
			if (minimumHours.get() != null && maximumHours.get() != null && minimumEuroPerHour.get() != null)
				return minimumHours.get() && maximumHours.get() && minimumEuroPerHour.get();
			else
				return false;
		}, minimumHours, maximumHours, minimumEuroPerHour);

		new Signal<>("salary", () -> {
			if (working_hours.get() != null && euro_per_hour.get() != null)
				return working_hours.get() * euro_per_hour.get();
			else
				return 0.0;
		}, working_hours, euro_per_hour);

		logger.fine("Finished building Dependencies");
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
