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
 * Holds a list of tasks and a list of developers each indicated by a simple
 * integer. Searches for new clients (TaskCreater) and registers to their
 * "task creation channels"
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class ServerNode extends Client {
	public static final String NAME = "ServerNode";
	private Var<ArrayList<String>> myClients = null;
	private Var<String> developers = null;
	private Var<String> tasks = null;

	public static void main(String... args) {
		new ServerNode();
	}

	public ServerNode() {
		super(NAME);
		developers = new Var<String>("developers", "");
		tasks = new Var<String>("tasks", "");
		myClients = new Var<ArrayList<String>>("Server_registered_clients", new ArrayList<String>());
		detectClients();
	}

	private void detectClients() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myClients.get().contains(x.getSecond() + "@" + x.getFirst())
						&& (x.getSecond().equalsIgnoreCase("newTask") || x.getSecond().equalsIgnoreCase("newDev")))
				.forEach(x -> createClient(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		detectClients();
	}

	private void createClient(String clientHost, String clientVar) {
		System.out.println("detected client " + clientHost + " " + clientVar);
		RemoteVar<String> rv = new RemoteVar<>(clientHost, clientVar);
		Signal<String> sig = new Signal<>(clientHost, () -> {
			if (rv.get() != null) {
				return rv.get();
			} else {
				return null;
			}
		}, rv);

		sig.change().addHandler((oldValue, newValue) -> {
			if (newValue != null) {
				// Set vars for remote querying
				if (clientVar.equalsIgnoreCase("newDev")) {
					developers.set(developers.get().length() == 0 ? newValue : developers.get() + ":" + newValue);
				}
				if (clientVar.equalsIgnoreCase("newTask")) {
					tasks.set(tasks.get().length() == 0 ? newValue : tasks.get() + ":" + newValue);
				}
				System.out.println("new value from " + clientHost + "@" + clientVar);
			}
		});
		myClients.modify((old) -> old.add(clientVar + "@" + clientHost));
	}
}