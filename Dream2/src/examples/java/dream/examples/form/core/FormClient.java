package dream.examples.form.core;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

public abstract class FormClient extends Client {

	private RemoteVar<Double> salary;
	private RemoteVar<Boolean> settings;
	private Signal<Double> remoteSalary;
	private Signal<Boolean> remoteSettings;

	private FormGUI gui;
	private String[] labelText;
	private String[] values;

	public FormClient(String name, String... labelText) {
		super(name);
		this.labelText = labelText;
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList(toVar(FormServer.NAME, FormServer.Salary),
				toVar(FormServer.NAME, FormServer.SettingsOkay));
	}

	protected void start() {
		gui = new FormGUI(getHostName(), labelText);
		gui.setListener(this);
		if (values != null)
			gui.setInitValues(values);

		salary = new RemoteVar<>(FormServer.NAME, FormServer.Salary);
		settings = new RemoteVar<>(FormServer.NAME, FormServer.SettingsOkay);

		remoteSalary = new Signal<>("remoteSalary", () -> {
			if (salary.get() != null)
				return salary.get();
			else
				return 0.0;
		}, salary);

		remoteSettings = new Signal<>("remoteSettings", () -> {
			if (settings.get() != null)
				return settings.get();
			else
				return false;
		}, settings);

		gui.setText("Salary: ");
		gui.setColor(Color.red);
		remoteSalary.change().addHandler((o, n) -> gui.setText("Salary: " + n.toString()));
		remoteSettings.change().addHandler((o, n) -> gui.setColor((n ? Color.green : Color.red)));
	}

	public abstract void typedText(int i, String typedText);

	public void setInitValues(String... values) {
		this.values = values;
	}
}
