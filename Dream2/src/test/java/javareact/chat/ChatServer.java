package javareact.chat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class ChatServer {
	public static final String NEW_ID = "NewSessionID";
	public static final String NEW_VAR = "NewSessionVAR";
	public static final String NAME = "ChatServer";

	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	private Map<String, Var<String>> clientVars;
	private Var<List<String>> clients;

	private Var<String> newSessionID;
	private Var<String> newSessionVAR;
	private static Random r = new Random();

	private List<String> processedIDs = new ArrayList<String>();

	public static void main(String[] args) {
		new ChatServer().start();
	}

	private void initServer() {
		Consts.hostName = NAME;

		clients = new Var<List<String>>("RegisteredClients", new LinkedList<String>());
		clientVars = new HashMap<String, Var<String>>();
		newSessionID = new Var<String>(NEW_ID, "");
		newSessionVAR = new Var<String>(NEW_VAR, "");
		changeNewSession();
	}

	/**
	 * Provide new Host ID and Variable name and listen to it for a new client
	 */
	private void changeNewSession() {
		String id = getRandom();
		String var = getRandom();
		RemoteVar<String> listener = new RemoteVar<String>("*", var);
		Signal<String> listenerSignal = new Signal<String>("listener", () -> {
			if (listener.get() == null)
				return "";
			else
				return listener.get();
		}, listener);
		listenerSignal.change().addOneTimeHandler((o, msg) -> {
			System.out.println("new session handler");
			changeNewSession();
			processNewSession(id, var, msg);
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		newSessionID.set(id);
		newSessionVAR.set(var);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		processedIDs.add(id);
		changeNewSession();
	}

	private void processNewSession(String id, String var, String msg) {
		if (!processedIDs.contains(id)) {
			processedIDs.add(id);
			// variable@name@serverVar
			String[] temp = msg.split("@", 3);
			registerClient(temp[1], temp[0], temp[2]);
		} else
			System.out.println("New message(\"" + msg + "\") on already discarded channel " + id + "@" + var);
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
		}, remote);

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
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(String.valueOf(r.nextLong()).getBytes());
			String encryptedString = new String(messageDigest.digest());
			return encryptedString.replace("@", "");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void start() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

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

	private final void startTokenServiceIfNeeded() {
		if (!tokenServiceStarted) {
			String serverAddress = "reds-tcp:localhost:9000";
			Set<String> addresses = new HashSet<String>();
			addresses.add(serverAddress);
			TokenServiceLauncher.start(addresses);
			tokenServiceStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
