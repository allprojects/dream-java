package dream.examples.scrumBoard.core;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.scrumBoard.common.MonitorGUI;
import dream.examples.util.Client;

/**
 * Displays both lists, the developers and the tasks.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Monitor extends Client implements dream.examples.scrumBoard.common.MonitorGUI.Monitor {

	public static final String NAME = "Monitor";

	private final MonitorGUI gui;
	private final RemoteVar<String> devs;
	private final RemoteVar<String> tasks;
	private final Signal<String> sigDevs;
	private final Signal<String> sigTasks;

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList(toVar(Server.NAME, Server.VAR_developers), toVar(Server.NAME, Server.VAR_tasks));
	}

	public Monitor() {
		super(NAME);
		gui = new MonitorGUI(this);

		devs = new RemoteVar<String>(Server.NAME, Server.VAR_developers);
		sigDevs = new Signal<String>("sigDevs", () -> {
			return devs.get();
		}, devs);

		tasks = new RemoteVar<String>(Server.NAME, Server.VAR_tasks);
		sigTasks = new Signal<String>("sigTests", () -> {
			return tasks.get();
		}, tasks);

		sigDevs.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal devs:" + newVal);
			// gui.setDevs(newVal);
		});

		sigTasks.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal tasks:" + newVal);
			// gui.setTasks(newVal);
		});
	}

	public static void main(String[] args) {
		new Monitor();
	}

	public void clickButton() {
		readLock(toVar(Server.NAME, Server.VAR_tasks), toVar(Server.NAME, Server.VAR_developers));
		if (tasks.get() != null)
			gui.setTasks(tasks.get());
		if (devs.get() != null)
			gui.setDevs(devs.get());
		unlock();
	}
}