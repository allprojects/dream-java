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
public class Server extends Client {
	public static final String NAME = "ServerNode";
	public static final String VAR_developers = "developers";
	public static final String VAR_tasks = "tasks";

	private final ArrayList<String> myClients;
	private final Var<String> developers;
	private final Var<String> tasks;

	public static void main(String... args) {
		new Server();
	}

	public Server() {
		super(NAME);
		developers = new Var<String>(VAR_developers, "");
		tasks = new Var<String>(VAR_tasks, "");
		myClients = new ArrayList<String>();
		detectClients();
	}

	private void detectClients() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0]))
				.filter(x -> !myClients.contains(toVar(x)) && (x.getSecond().equalsIgnoreCase(Creator.VAR_newTask)
						|| x.getSecond().equalsIgnoreCase(Creator.VAR_newDev)))
				.forEach(x -> createClient(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		detectClients();
	}

	private void createClient(String clientHost, String clientVar) {
		logger.info("detected client " + clientHost + " " + clientVar);
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
				if (clientVar.equalsIgnoreCase(Creator.VAR_newDev)) {
					developers.set(developers.get().length() == 0 ? newValue : developers.get() + ":" + newValue);
				}
				if (clientVar.equalsIgnoreCase(Creator.VAR_newTask)) {
					tasks.set(tasks.get().length() == 0 ? newValue : tasks.get() + ":" + newValue);
				}
				System.out.println("new value from " + clientHost + "@" + clientVar);
			}
		});
		myClients.add(clientVar + "@" + clientHost);
	}
}