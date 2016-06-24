package dream.examples.scrumBoard.atomic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.scrumBoard.common.MonitorGUI;
import dream.examples.scrumBoard.core.Server;
import dream.examples.util.Client;
import dream.examples.util.Pair;

/**
 * Displays both lists, the developers and the tasks.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Monitor extends Client implements dream.examples.scrumBoard.common.MonitorGUI.Monitor {

	public static final String NAME = "Monitor";

	private MonitorGUI gui;
	private RemoteVar<String> devs;
	private RemoteVar<String> tasks;

	private Var<Boolean> lockRequest;

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList(toVar(Server.NAME, Server.VAR_developers), toVar(Server.NAME, Server.VAR_tasks));
	}

	public Monitor() {
		super(NAME);

		// Establish new session with LockManager
		RemoteVar<ArrayList<Pair<String, String>>> registeredClients = new RemoteVar<>(LockManager.NAME,
				LockManager.VAR_clients);
		Signal<ArrayList<Pair<String, String>>> s = new Signal<>("s", () -> {
			if (registeredClients.get() == null)
				return new ArrayList<Pair<String, String>>();
			else
				return registeredClients.get();
		}, registeredClients);
		s.change().addHandler((o, n) -> {
			if (n.contains(new Pair<>(this.getHostName(), LockManager.VAR_requestLock)) && gui == null)
				setup();
		});

		lockRequest = new Var<>(LockManager.VAR_requestLock, false);
		logger.fine("Setup: Waiting for Registration to LockManager ...");

	}

	private void setup() {
		gui = new MonitorGUI(this);

		devs = new RemoteVar<String>(Server.NAME, Server.VAR_developers);
		tasks = new RemoteVar<String>(Server.NAME, Server.VAR_tasks);
	}

	public static void main(String[] args) {
		new Monitor();
	}

	public void clickButton() {
		// TODO: lock
		if (tasks.get() != null)
			gui.setTasks(tasks.get());
		if (devs.get() != null)
			gui.setDevs(devs.get());
		// TODO: unlock
	}
}