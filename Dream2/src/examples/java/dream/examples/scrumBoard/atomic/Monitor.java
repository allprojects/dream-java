package dream.examples.scrumBoard.atomic;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.examples.scrumBoard.common.MonitorGUI;
import dream.examples.scrumBoard.core.Server;

/**
 * Displays both lists, the developers and the tasks.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Monitor extends LockClient implements dream.examples.scrumBoard.common.MonitorGUI.Monitor {

	public static final String NAME = "Monitor";

	private MonitorGUI gui;
	private RemoteVar<String> devs;
	private RemoteVar<String> tasks;

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList(toVar(Server.NAME, Server.VAR_developers), toVar(Server.NAME, Server.VAR_tasks));
	}

	public Monitor() {
		super(NAME);
	}

	protected void setup() {
		gui = new MonitorGUI(this);

		devs = new RemoteVar<String>(Server.NAME, Server.VAR_developers);
		tasks = new RemoteVar<String>(Server.NAME, Server.VAR_tasks);
	}

	public static void main(String[] args) {
		new Monitor();
	}

	public void clickButton() {
		lock();
		if (tasks.get() != null)
			gui.setTasks(tasks.get());
		if (devs.get() != null)
			gui.setDevs(devs.get());
		unlock();
	}
}