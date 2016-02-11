package dream.examples.chat;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class ChatServer {
	public static final String NEW_ID = "NewSessionID";
	public static final String NEW_VAR = "NewSessionVAR";
	public static final String NAME = "ChatServer";

	private boolean serverStarted = false;
	private boolean lockManagerStarted = false;

	private Map<String, Var<String>> clientVars;
	private Var<ArrayList<String>> clients;

	private Var<String> newSessionVAR;
	private static SecureRandom r = new SecureRandom();

	private List<String> processedIDs = new ArrayList<String>();

	public static void main(String[] args) {
		new ChatServer().start();
	}

	private void initServer() {
		Consts.hostName = NAME;

		clients = new Var<ArrayList<String>>("RegisteredClients", new ArrayList<String>());
		clientVars = new HashMap<String, Var<String>>();
		newSessionVAR = new Var<String>(NEW_VAR, "");
		changeNewSession();
	}

	/**
	 * Provide new Host ID and Variable name and listen to it for a new client
	 */
	private void changeNewSession() {
		String var = getRandom();
		RemoteVar<String> listener = new RemoteVar<String>("*", var);
		Signal<String> listenerSignal = new Signal<String>("listener", () -> {
			if (listener.get() == null)
				return "";
			else
				return listener.get();
		} , listener);
		listenerSignal.change().addHandler((o, msg) -> {
			System.out.println("new session handler");
			changeNewSession();
			processNewSession(var, msg);
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		newSessionVAR.set(var);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		processedIDs.add(var);
		changeNewSession();
	}

	private void processNewSession(String var, String msg) {
		if (!processedIDs.contains(var)) {
			processedIDs.add(var);
			// variable@name@serverVar
			String[] temp = msg.split("@", 3);
			registerClient(temp[1], temp[0], temp[2]);
		} else
			System.out.println("New message(\"" + msg + "\") on already discarded channel *@" + var);
	}

	/**
	 * Registers a new chat client with its name and the name of the variable it
	 * is sending messages on
	 * 
	 * @param clientName
	 *            the name of the client
	 * @param clientVar
	 *            the name of the variable, must be of type String
	 * @param serverVar
	 * @return the name of the variable on which the server will provide
	 *         messages to the client
	 */
	public void registerClient(String clientName, String clientVar, String serverVar) {
		System.out.println("Register: " + clientName + "(" + clientVar + ") -> " + serverVar);
		RemoteVar<String> remote = new RemoteVar<String>(clientName, clientVar);
		Signal<String> listen = new Signal<String>(serverVar + "listen", () -> {
			if (remote.get() == null)
				return "";
			else
				return remote.get();
		} , remote);

		// handler for "client writes something"
		listen.change().addHandler((oldValue, newValue) -> clientWrote(clientName, newValue));

		// create new var for sending messages to this client
		clientVars.put(clientName, new Var<String>(serverVar, ""));

		// add client as registered
		clients.modify((old) -> old.add(clientName));
	}

	/**
	 * called when the server receives a new message from a client
	 * 
	 * @param name
	 * @param text
	 */
	private void clientWrote(String name, String text) {
		System.out.println("Server: " + name + " -> " + text);
		// TODO propagate to correct clients (chat rooms etc.)
		clientVars.forEach((client, var) -> {
			if (client != name)
				var.set(name + ": " + text);
		});
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

		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
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
