package dream.examples.form.complete_glitchfree;

import dream.client.Signal;

public class CompleteGlitchFreeFormServer extends FormServer {

	@Override
	protected void createDependencies() {
		logger.fine("Building Dependencies");
		// TODO implement glitch freedom
		final Signal<Boolean> minimumHours = new Signal<>(MinimumHours, () -> {
			if (working_hours.get() != null && required_hours.get() != null)
				return working_hours.get() > required_hours.get().getFirst();
			else
				return false;
		}, working_hours, required_hours);

		final Signal<Boolean> maximumHours = new Signal<>(MaximumHours, () -> {
			if (working_hours.get() != null && required_hours.get() != null)
				return working_hours.get() < required_hours.get().getSecond();
			else
				return false;
		}, working_hours, required_hours);

		final Signal<Boolean> minimumEuroPerHour = new Signal<>(MinimumEuroPerHour, () -> {
			if (euro_per_hour.get() != null)
				return euro_per_hour.get() > 10;
			else
				return false;
		}, euro_per_hour);

		new Signal<>(SettingsOkay, () -> {
			if (minimumHours.get() != null && maximumHours.get() != null && minimumEuroPerHour.get() != null)
				return minimumHours.get() && maximumHours.get() && minimumEuroPerHour.get();
			else
				return false;
		}, minimumHours, maximumHours, minimumEuroPerHour);

		new Signal<>(Salary, () -> {
			if (working_hours.get() != null && euro_per_hour.get() != null)
				return working_hours.get() * euro_per_hour.get();
			else
				return 0.0;
		}, working_hours, euro_per_hour);

		logger.fine("Finished building Dependencies");
	}

	public static void main(String[] args) {
		new CompleteGlitchFreeFormServer();
	}
}
