package dream.examples.form.complete_glitchfree;

import java.util.logging.Level;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Pair;

public class FormServer extends LockClient {

	protected RemoteVar<Integer> working_hours;
	protected RemoteVar<Double> euro_per_hour;
	protected RemoteVar<Pair<Integer, Integer>> required_hours;

	public static final String NAME = "FormServer";
	public static final String MinimumHours = "minimumHours";
	public static final String MaximumHours = "maximumHours";
	public static final String MinimumEuroPerHour = "minimumEuroPerHour";
	public static final String SettingsOkay = "settingsOkay";
	public static final String Salary = "salary";

	public FormServer() {
		super(NAME);
		detectNewSession();
	}

	/**
	 * Look for new clients every 5 seconds
	 */
	private void detectNewSession() {
		while (euro_per_hour == null || working_hours == null || required_hours == null) {
			for (String str : DreamClient.instance.listVariables()) {
				String host = str.split("@")[1];
				String var = str.split("@")[0];
				if (working_hours == null && var.equalsIgnoreCase(Secretary.WorkingHours)) {
					working_hours = new RemoteVar<>(host, var);
					logger.fine("Found Secretary");
				} else if (euro_per_hour == null && var.equalsIgnoreCase(Boss.EuroPerHour)) {
					euro_per_hour = new RemoteVar<>(host, var);
					logger.fine("Found Boss");
				} else if (required_hours == null && var.equalsIgnoreCase(Boss.RequiredHours)) {
					required_hours = new RemoteVar<>(host, var);
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

		final Signal<Boolean> minimumHours = new Signal<>(MinimumHours, () -> {
			if (working_hours.get() != null && required_hours.get() != null)
				return working_hours.get() > required_hours.get().getFirst();
			else
				return false;
		}, false, working_hours, required_hours);

		final Signal<Boolean> maximumHours = new Signal<>(MaximumHours, () -> {
			if (working_hours.get() != null && required_hours.get() != null)
				return working_hours.get() < required_hours.get().getSecond();
			else
				return false;
		}, false, working_hours, required_hours);

		final Signal<Boolean> minimumEuroPerHour = new Signal<>(MinimumEuroPerHour, () -> {
			if (euro_per_hour.get() != null)
				return euro_per_hour.get() > 10;
			else
				return false;
		}, false, euro_per_hour);

		new Signal<>(SettingsOkay, () -> {
			if (minimumHours.get() != null && maximumHours.get() != null && minimumEuroPerHour.get() != null)
				return minimumHours.get() && maximumHours.get() && minimumEuroPerHour.get();
			else
				return false;
		}, false, minimumHours, maximumHours, minimumEuroPerHour);

		new Signal<>(Salary, () -> {
			if (working_hours.get() != null && euro_per_hour.get() != null)
				return working_hours.get() * euro_per_hour.get();
			else
				return 0.0;
		}, 0.0, working_hours, euro_per_hour);

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new FormServer();
	}

	@Override
	protected void setup() {
	}

}
