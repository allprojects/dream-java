package dream.examples.taskBoard;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Review the tasks.
 */
public class TaskReviewer {
	private final Logger log = Logger.getLogger("MagtClient");
	private Var<ArrayList<String>> myServer = null;
	RemoteVar<String> devs = null;
	RemoteVar<String> tests = null;
	Signal<String> sigDevs = null;
	Signal<String> sigTests = null;

	public static void main(String[] args) {
		new TaskReviewer();
	}

	public TaskReviewer() {
		Consts.hostName = "QueryClient";
		log.setLevel(Level.ALL);
		log.addHandler(Logger.getGlobal().getHandlers()[0]);
		if (devs == null) {
			devs = new RemoteVar<String>("ServerNode", "taskDevs");
			sigDevs = new Signal<String>("sigDevs", () -> {
				return devs.get();
			}, devs);
		}
		if (tests == null) {

			tests = new RemoteVar<String>("ServerNode", "taskTests");
			sigTests = new Signal<String>("sigTests", () -> {
				return tests.get();
			}, tests);
		}
		// TODO show in monitor
		sigDevs.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal devs:" + newVal);
		});

		sigTests.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal tests:" + newVal);
		});
	}
}
