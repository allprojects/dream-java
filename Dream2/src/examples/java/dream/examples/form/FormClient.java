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

	public FormClient(String name) {
		Consts.hostName = name;
		Logger.getGlobal().setLevel(Level.ALL);
	}

	protected void init(String name) {
		while (!DreamClient.instance.listVariables().contains("salary@FormServer") || //
				!DreamClient.instance.listVariables().contains("settingsOkay@FormServer")) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		gui = new FormGUI(name);
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

		gui.setText("");
		gui.setColor(Color.red);
		remoteSalary.change().addHandler((o, n) -> gui.setText(n.toString()));
		remoteSettings.change().addHandler((o, n) -> gui.setColor((n ? Color.green : Color.red)));
	}

	public abstract void typedText(String typedText);
}
