package dream.examples.taskBoard;

import java.util.ArrayList;
import java.util.Set;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.util.Client;
import dream.examples.util.Pair;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description run background tasks: read task, create task lists: development
 *              and test etc.
 */
public class ServerNode extends Client {
	public static final String NAME = "ServerNode";
	private Var<ArrayList<String>> myClients = null;
	private Var<String> devTask = null;
	private Var<String> testTask = null;

	public static void main(String... args) {
		new ServerNode();
	}

	public ServerNode() {
		super(NAME);
		myClients = new Var<ArrayList<String>>("Server_registered_clients", new ArrayList<String>());
		detectClients();
	}

	private void detectClients() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myClients.get().contains(x.getSecond() + "@" + x.getFirst())
						&& x.getSecond().equalsIgnoreCase("FromTaskNode"))
				.forEach(x -> createClient(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		detectClients();
	}

	private void createClient(String clientHost, String clientVar) {
		RemoteVar<String> rv = new RemoteVar<String>(clientHost, clientVar);
		Signal<String> sig = new Signal<String>(clientHost, () -> {
			if (rv.get() != null) {
				return rv.get();
			} else {

				return "";
			}
		}, rv);

		sig.change().addHandler((oldValue, newValue) -> {
			if (newValue != null) {
				String newDev = newValue.split(":")[0];
				String newTest = newValue.split(":")[1];
				if (devTask == null)
					devTask = new Var<String>("taskDevs", "");

				if (testTask == null)
					testTask = new Var<String>("taskTests", "");

				// Set vars for remote querying
				devTask.set(devTask.get().toString() + newDev + ":");
				testTask.set(testTask.get().toString() + newTest + ":");
			}
		});
		myClients.modify((old) -> old.add(clientVar + "@" + clientHost));
	}
}