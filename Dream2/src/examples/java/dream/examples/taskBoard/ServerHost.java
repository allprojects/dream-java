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

public class ServerHost {
	public static final String NAME = "ServerHost";
	private boolean serverStarted = false;
	private Var<ArrayList<String>> myClients = null;
	private final Logger log = Logger.getLogger("Server");
	private Var<String> devTask = null;
	private Var<String> testTask = null;

	public static void main(String[] args) {
		new ServerHost();
	}

	public ServerHost() {
		startServerIfNeeded();
		log.setLevel(Level.ALL);
		log.addHandler(log.getGlobal().getHandlers()[0]);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		log.info("Sig" + sig.toString());
		sig.change().addHandler((oldValue, newValue) -> createTaskLists(newValue));
		myClients.modify((old) -> old.add(clientVar + "@" + clientHost));
	}

	private void createTaskLists(String message) {
		if (message != null) {
			String newDev = message.split(":")[0];
			String newTest = message.split(":")[1];
			if (devTask == null) {
				devTask = new Var<String>("taskDevs", "");
			}
			if (testTask == null) {
				testTask = new Var<String>("taskTests", "");
			}
			// Set vars for remote querying
			devTask.set(newDev);
			testTask.set(newTest);
		}
	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			log.info("M: server started");
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