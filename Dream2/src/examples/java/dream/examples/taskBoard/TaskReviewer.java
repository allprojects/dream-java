package dream.examples.taskBoard;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.examples.util.Pair;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Review the tasks.
 */
public class TaskReviewer {
	private final Logger log = Logger.getLogger("MagtClient");
	private Var<ArrayList<String>> myServer = null;
	RemoteVar<String> devTasks = null;
	RemoteVar<String> testTasks = null;

	public TaskReviewer() {
		Consts.hostName = "QueryClient";
		log.setLevel(Level.ALL);
		log.addHandler(Logger.getGlobal().getHandlers()[0]);
		myServer = new Var<ArrayList<String>>("from_server", new ArrayList<String>());
		detectQueryResults();
	}

	private void detectQueryResults() {
		log.info("In TaskReview");
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myServer.get().contains(x.getSecond() + "@" + x.getFirst())
						&& x.getSecond().equalsIgnoreCase("taskDevs"))
				.forEach(x -> updateDevQuery(x.getFirst(), x.getSecond()));

		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myServer.get().contains(x.getSecond() + "@" + x.getFirst())
						&& x.getSecond().equalsIgnoreCase("taskTests"))
				.forEach(x -> updateTestQuery(x.getFirst(), x.getSecond()));

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		detectQueryResults();
	}

	private void updateDevQuery(String host, String value) {
		RemoteVar<String> rv = new RemoteVar<String>(host, value);
		Signal<String> sig = new Signal<String>("received_" + host, () -> {
			if (rv.get() != null) {
				return rv.get();
			} else {
				return "";
			}
		}, rv);
		sig.change().addHandler((oldValue, newValue) -> showDevQuery(newValue));
		myServer.modify((old) -> old.add(value + "@" + host));
	}

	private void updateTestQuery(String host, String value) {
		RemoteVar<String> rv = new RemoteVar<String>(host, value);
		Signal<String> sig = new Signal<String>("received_" + host, () -> {
			if (rv.get() != null) {
				return rv.get();
			} else {
				return "";
			}
		}, rv);
		sig.change().addHandler((oldValue, newValue) -> showTestQuery(newValue));
		myServer.modify((old) -> old.add(value + "@" + host));
	}

	private void showTestQuery(String x) {
		log.info("showTestQuery:" + x);
	}

	private void showDevQuery(String x) {
		log.info("showDevQuery:" + x);
	}

	public static void main(String[] args) {
		new TaskReviewer();
	}

}
