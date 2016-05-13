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

public class TaskReview {
	private final Logger log = Logger.getLogger("MagtClient");
	private Var<ArrayList<String>> myServer = null;
	RemoteVar<String> devTasks = null;
	RemoteVar<String> testTasks = null;

	public TaskReview() {
		Consts.hostName = "QueryClient";
		log.setLevel(Level.ALL);
		log.addHandler(log.getGlobal().getHandlers()[0]);
		myServer = new Var<ArrayList<String>>("from_server", new ArrayList<String>());
		detectQueryResults();
	}

	private void detectQueryResults() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		detectQueryResults();
	}

	private void updateDevQuery(String host, String value) {
		RemoteVar<String> rv = new RemoteVar<String>(host, value);

		Signal<String> sig = new Signal<String>("received_" + host, () -> {
			if (rv.get() != null) {
				log.fine("M: not empty " + rv.get());
				return rv.get();
			} else {
				return "";
			}
		}, rv);

		sig.change().addHandler((oldValue, newValue) -> showQuery(newValue));
		myServer.modify((old) -> old.add(value + "@" + host));
	}

	private void updateTestQuery(String host, String value) {
		RemoteVar<String> rv = new RemoteVar<String>(host, value);

		Signal<String> sig = new Signal<String>("received_" + host, () -> {
			if (rv.get() != null) {
				log.fine("M: not empty " + rv.get());
				return rv.get();
			} else {
				return "";
			}
		}, rv);

		sig.change().addHandler((oldValue, newValue) -> showQuery(newValue));
		myServer.modify((old) -> old.add(value + "@" + host));
	}

	private void showQuery(String x) {
		log.info("showQuery:" + x);
	}

	public static void main(String[] args) {
		new TaskReview();
	}

}
