package dream.examples.form.core;

import dream.client.RemoteVar;
import dream.examples.util.Client;

public class FormServer extends Client {

	public static final String NAME = "FormServer";
	public static final String MinimumHours = "minimumHours";
	public static final String MaximumHours = "maximumHours";
	public static final String MinimumEuroPerHour = "minimumEuroPerHour";
	public static final String SettingsOkay = "settingsOkay";
	public static final String Salary = "salary";

	protected RemoteVar<Integer> working_hours;
	protected RemoteVar<Double> euro_per_hour;

	public FormServer() {
		super(NAME);
	}

	public static void main(String[] args) {
		new FormServer();
	}
}
