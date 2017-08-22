package dream.examples.form.simple;

import java.util.LinkedList;

import dream.client.Signal;
import dream.client.Var;

public class GlitchFreeFormServer extends FormServer {

	final LinkedList<Boolean> minimumQueue = new LinkedList<>();
	final LinkedList<Boolean> maximumQueue = new LinkedList<>();
	final Var<Boolean> settingsOkay = new Var<>(SettingsOkay, false);
	Signal<Boolean> minimumEuroPerHour;

	private void updateSettingsOkay() {
		if (minimumQueue.size() > 0 && maximumQueue.size() > 0 && minimumEuroPerHour.get() != null)
			settingsOkay.set(minimumQueue.pop() && maximumQueue.pop() && minimumEuroPerHour.get());
	}

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");

		minimumEuroPerHour = new Signal<>(MinimumEuroPerHour, () -> {
			return euro_per_hour.get() > 10;
		}, false, euro_per_hour);

		final Signal<Boolean> minimumHours = new Signal<>(MinimumHours, () -> {
			return working_hours.get() > 10;
		}, false, working_hours);

		final Signal<Boolean> maximumHours = new Signal<>(MaximumHours, () -> {
			return working_hours.get() < 60;
		}, false, working_hours);

		minimumEuroPerHour.change().addHandler((o, n) -> updateSettingsOkay());

		minimumHours.change().addHandler((o, n) -> {
			minimumQueue.add(n);
			updateSettingsOkay();
		});

		maximumHours.change().addHandler((o, n) -> {
			maximumQueue.add(n);
			updateSettingsOkay();
		});

		new Signal<>(Salary, () -> {
			if (working_hours.get() != null && euro_per_hour.get() != null)
				return working_hours.get() * euro_per_hour.get();
			else
				return 0.0;
		}, 0.0, working_hours, euro_per_hour);

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new GlitchFreeFormServer();
	}
}