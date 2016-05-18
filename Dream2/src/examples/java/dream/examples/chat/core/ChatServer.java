package dream.examples.chat.core;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.util.Client;
import dream.examples.util.DependencyVisualization;
import dream.examples.util.Pair;

public class ChatServer extends Client {
	public static final String NAME = "ChatServer";

	private final static SecureRandom r = new SecureRandom();

	private final Var<ArrayList<String>> clients;
	private final HashMap<String, Var<String>> clientVars;
	private final HashMap<String, Map<String, String>> rooms;
	private final Logger logger = Logger.getLogger("ChatServer");

	public static final String SERVER_PREFIX = "server_";
	public static final String SERVER_REGISTERED_CLIENTS = SERVER_PREFIX + "RegisteredClients";

	public static void main(String[] args) {
		new ChatServer();
	}

	public ChatServer() {
		super(NAME);
		logger.setLevel(Level.ALL);
		logger.addHandler(Logger.getGlobal().getHandlers()[0]);
		clientVars = new HashMap<>();
		rooms = new HashMap<>();
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
						&& x.getSecond().equalsIgnoreCase("toServerVar"))
				.//
				forEach(x -> createNewSessionFor(x.getFirst(), x.getSecond()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep for 0.5 seconds", e);
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
		logger.fine("Creating new Session for " + clientName + " with Var " + clientVar);

		// add listener to messages from this client
		RemoteVar<String> var = new RemoteVar<String>(clientName, clientVar);
		Signal<String> sig = new Signal<>(SERVER_PREFIX + "receive_" + clientName, () -> {
			if (var.get() != null)
				return var.get();
			else
				return "";
		}, var);
		sig.change().addHandler((oldValue, newValue) -> receivedMessage(clientName, newValue));

		// add client as registered
		clients.modify((old) -> old.add(clientVar + "@" + clientName));

		// now wait for first message from client
		logger.fine("Setup: Waiting for first message from " + clientName);
	}

	protected void sendMessage(String clientName, String message) {
		clientVars.get(clientName).set(message);
	}

	protected void receivedMessage(String clientName, String message) {
		logger.fine("Received client message from " + clientName + ": " + message);
		if (!clientVars.containsKey(clientName)) {
			// first message from client
			// setup Var for messages to this client
			clientVars.put(clientName, new Var<String>(message, ""));
			logger.fine("Setup: new Var for messages to " + clientName);
		} else {
			String[] temp = message.split(" ", 2);
			String command = temp[0];
			String rest = temp.length > 1 ? temp[1] : "";

			if (command.equalsIgnoreCase("room")) {
				// room <roomName> <Variable> <recipient1> <recipient2> ...
				logger.fine("Room: Received command to create new room (" + message + ")");
				String[] t = rest.split(" ", 3);
				String roomName = t[0];
				String otherClients = t[2];
				// Map: clientName -> clientRoomVar
				HashMap<String, String> roomVars = new HashMap<>();
				roomVars.put(clientName, t[1]);
				// send message to every recipient to send a Var for that room
				if (otherClients.equals("*")) {
					for (String client : clients.get()) {
						// set Var for c as "not sent"
						roomVars.put(client, null);
						// and ask c for his Var
						logger.finer("Room: Sending Var-Request for " + roomName + " to " + client);
						sendMessage(client, "room " + roomName + " " + otherClients.replace(client, clientName));
					}
				} else {
					for (String client : otherClients.split(" ")) {
						// set Var for c as "not sent"
						roomVars.put(client, null);
						// and ask c for his Var
						logger.finer("Room: Sending Var-Request for " + roomName + " to " + client);
						sendMessage(client, "room " + roomName + " " + otherClients.replace(client, clientName));
					}
				}
				rooms.put(roomName, roomVars);
			} else if (command.equalsIgnoreCase("roomVar")) {
				// roomVar <roomName> <Variable>
				String[] t = rest.split(" ", 2);
				String roomName = t[0];
				String varName = t[1];
				logger.fine("Room: Received a Var (" + varName + ") from " + clientName + " for room " + roomName);
				Map<String, String> roomVars = rooms.get(roomName);
				roomVars.put(clientName, varName);

				// check if every room member has sent a Var
				if (!roomVars.values().contains(null)) {
					String varString = "";
					for (Entry<String, String> e : roomVars.entrySet()) {
						varString += e.getKey() + "=" + e.getValue() + " ";
					}
					for (String client : roomVars.keySet()) {
						sendMessage(client, "roomVar " + roomName + " " + varString);
					}
					logger.fine("Room: Finished setting up room " + roomName);
				}
			} else if (command.equalsIgnoreCase("graph")) {
				DependencyVisualization.show();
			}
		}
	}

	/**
	 * @return random String hashed with SHA-256
	 */
	public static String getRandom() {
		return new BigInteger(130, r).toString(32);
	}
}
