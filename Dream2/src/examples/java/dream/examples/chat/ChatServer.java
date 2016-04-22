package dream.examples.chat;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class ChatServer {
	public static final String NAME = "ChatServer";

	private boolean serverStarted = false;
	private boolean lockManagerStarted = false;

	private final Var<ArrayList<String>> clients;

	private final Logger logger = Logger.getGlobal();

	public static final String SERVER_PREFIX = "server_";
	public static final String SERVER_REGISTERED_CLIENTS = SERVER_PREFIX + "RegisteredClients";

	public static void main(String[] args) {
		new ChatServer();
	}

	public ChatServer() {
		startServerIfNeeded();
		startLockManagerIfNeeded();

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
				filter(x -> !clients.get().contains(x.getSecond() + "@" + x.getFirst())
						&& x.getSecond().startsWith("chat_"))
				.//
				forEach(x -> createNewSessionFor(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep for 5 seconds", e);
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

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to wait for Server starting", e);
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
			logger.log(Level.SEVERE, "Failed to wait for LockManager starting", e);
		}
	}
}
