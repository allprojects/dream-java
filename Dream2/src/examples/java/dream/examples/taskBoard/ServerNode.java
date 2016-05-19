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
import dream.server.ServerLauncher;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description run background tasks: read task, create task lists: development
 *              and test etc.
 */
public class ServerNode {
	public static final String NAME = "ServerHost";
	private boolean serverStarted = false;
	private Var<ArrayList<String>> myClients = null;
	private final Logger log = Logger.getLogger("Server");
	private Var<String> devTask = null;
	private Var<String> testTask = null;

	public static void main(String... args) {
		new ServerNode();
	}

	public ServerNode() {
		startServerIfNeeded();
		log.setLevel(Level.ALL);
		log.addHandler(Logger.getGlobal().getHandlers()[0]);
		Consts.hostName = NAME;
		myClients = new Var<ArrayList<String>>("Server_registered_clients", new ArrayList<String>());
		detectClients();
	}

	private void detectClients() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myClients.get().contains(x.getSecond() + "@" + x.getFirst())
						&& x.getSecond().equalsIgnoreCase("toServerVar"))
				.forEach(x -> createClient(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("inside ServerHost:" + vars.toString());
		detectClients();
	}

	private void createClient(String clientHost, String clientVar) {
		RemoteVar<String> rv = new RemoteVar<String>(clientHost, clientVar);
		Signal<String> sig = new Signal<String>("received_" + clientHost, () -> {
			if (rv.get() != null) {
				return rv.get();
			} else {
				return "";
			}
		}, rv);
		// sig.change().addHandler((oldValue, newValue) ->
		// createTaskLists(newValue));
		sig.change().addHandler((oldValue, newValue) -> {
			if (newValue != null) {
				String newDev = newValue.split(":")[0];
				String newTest = newValue.split(":")[1];
				if (devTask == null) {
					devTask = new Var<String>("taskDevs", "");
				}
				if (testTask == null) {
					testTask = new Var<String>("taskTests", "");
				}
				// Set vars for remote querying
				devTask.set(devTask.get().toString() + newDev);
				testTask.set(testTask.get().toString() + newTest);
			}
		});
		myClients.modify((old) -> old.add(clientVar + "@" + clientHost));
	}

	/*
	 * private void createTaskLists(String newValue) { if (newValue != null) {
	 * String newDev = newValue.split(":")[0]; String newTest =
	 * newValue.split(":")[1]; if (devTask == null) { devTask = new
	 * Var<String>("taskDevs", ""); } if (testTask == null) { testTask = new
	 * Var<String>("taskTests", ""); } // Set vars for remote querying
	 * devTask.set(devTask.get().toString() + newDev);
	 * testTask.set(testTask.get().toString() + newTest); } }
	 */
	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "Failed to wait for Server starting", e);
		}
	}
}