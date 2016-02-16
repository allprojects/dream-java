package dream.examples.chat;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;
import javafx.util.Pair;

public class ChatServer {
	public static final String NAME = "ChatServer";

	private boolean serverStarted = false;
	private boolean lockManagerStarted = false;

	private Map<String, Var<String>> clientVars;
	private Var<ArrayList<String>> clients;

	private static SecureRandom r = new SecureRandom();

	public static final String SERVER_PREFIX = "server_";
	public static final String SERVER_REGISTERED_CLIENTS = SERVER_PREFIX + "RegisteredClients";

	public static void main(String[] args) {
		new ChatServer().start();
	}

	private void initServer() {
		Consts.hostName = NAME;

		clients = new Var<ArrayList<String>>(SERVER_REGISTERED_CLIENTS, new ArrayList<String>());
		clientVars = new HashMap<String, Var<String>>();
		detectNewSession();
	}

	/**
	 * Look for new clients every 5 seconds
	 */
	private void detectNewSession() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> !clientVars.keySet().contains(x.getKey()) && x.getValue().startsWith("chat_")).//
				forEach(x -> createNewSessionFor(x.getKey(), x.getValue()));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		detectNewSession();

	}

	/**
	 * Registers a new chat client with its name and the name of the variable it
	 * is sending messages on
	 * 
	 * @param clientName
	 *            the name of the client
	 * @param clientVar
	 *            the name of the variable, must be of type String
	 */
	private void createNewSessionFor(String clientName, String clientVar) {
		// x = "chat_xxx"
		RemoteVar<String> remote = new RemoteVar<String>(clientName, clientVar);
		Signal<String> listen = new Signal<String>(SERVER_PREFIX + "listen", () -> {
			if (remote.get() == null)
				return "";
			else
				return remote.get();
		} , remote);

		// handler for "client writes something"
		listen.change().addHandler((oldValue, newValue) -> clientWrote(clientName, newValue));

		// create new var for sending messages to this client
		String reply = getRandom();
		Var<String> replyChannel = new Var<String>(reply + clientName, clientName);

		// add client as registered
		clients.modify((old) -> old.add(clientName));
		clientVars.put(clientName, replyChannel);
	}

	/**
	 * called when the server receives a new message from a client
	 * 
	 * @param name
	 * @param text
	 */
	private void clientWrote(String name, String text) {
		// TODO propagate to correct clients (chat rooms etc.)
		if (text.startsWith("/")) {
			// special commands
			String[] temp = text.split(" ", 2);
			String command = temp[0].substring(1, temp[0].length());
			String rest = temp.length > 1 ? temp[1] : "";
			if (command.equals("quit")) {
				clients.modify((old) -> old.remove(name));
			}
			System.out.println("Server: " + name + " USED " + command);
		} else {
			System.out.println("Server: " + name + " -> " + text);
			clientVars.forEach((client, var) -> {
				if (client != name)
					var.set(name + ": " + text);
			});
		}
	}

	/**
	 * @return random String hashed with SHA-256
	 */
	public static String getRandom() {
		return new BigInteger(130, r).toString(32);
	}

	public void start() {
		startServerIfNeeded();
		startLockManagerIfNeeded();

		// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
		initServer();

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final void startLockManagerIfNeeded() {
		if (!lockManagerStarted) {
			LockManagerLauncher.start();
			lockManagerStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
