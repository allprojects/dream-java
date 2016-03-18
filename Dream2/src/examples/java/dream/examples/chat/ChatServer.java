package dream.examples.chat;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Set;

import dream.client.DreamClient;
import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;
import javafx.util.Pair;

public class ChatServer {
	public static final String NAME = "ChatServer";

	private boolean serverStarted = false;
	private boolean lockManagerStarted = false;

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
		detectNewSession();
	}

	/**
	 * Look for new clients every 5 seconds
	 */
	private void detectNewSession() {
		Set<String> vars = DreamClient.instance.listVariables();
		vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> !clients.get().contains(x.getValue() + "@" + x.getKey())
						&& x.getValue().startsWith("chat_"))
				.//
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
		// add client as registered
		clients.modify((old) -> old.add(clientVar + "@" + clientName));
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
