package dream.examples.taskBoard;

import java.util.Arrays;
import java.util.List;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Review the tasks.
 */
public class TaskReviewer extends Client {
	RemoteVar<String> devs = null;
	RemoteVar<String> tests = null;
	Signal<String> sigDevs = null;
	Signal<String> sigTests = null;

	public static void main(String[] args) {
		new TaskReviewer();
	}

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList("taskDevs@ServerNode", "taskTests@ServerNode");
	}

	public TaskReviewer() {
		super("QueryClient");
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
