package dream.examples.form;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

public abstract class FormClient {

	private RemoteVar<Double> salary;
	private RemoteVar<Boolean> settings;
	private Signal<Double> remoteSalary;
	private Signal<Boolean> remoteSettings;

	private FormGUI gui;
	protected final Logger logger;
	private String labelText;

	public FormClient(String name, String labelText) {
		Consts.hostName = name;
		this.labelText = labelText;

		logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL);
		logger.addHandler(Logger.getGlobal().getHandlers()[0]);

		DreamClient.instance.connect();
	}

	protected void init() {
		while (!DreamClient.instance.listVariables().contains("salary@FormServer") || //
				!DreamClient.instance.listVariables().contains("settingsOkay@FormServer")) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		gui = new FormGUI(Consts.hostName, labelText);
		gui.setListener(this);

		salary = new RemoteVar<>("FormServer", "salary");
		settings = new RemoteVar<>("FormServer", "settingsOkay");

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

	public abstract void typedText(String typedText);
}
