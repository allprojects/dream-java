package dream.examples.chat.core;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;

public class Chat {

	private final String userName;
	private ChatGUI gui;

	private final Signal<ArrayList<String>> onlineList;
	private List<String> lastOnline;

	private Var<String> toServer;

	private Map<Integer, Var<String>> rooms = new HashMap<>();
	private Map<String, Integer> roomNames = new HashMap<>();

	private final Logger logger;
	private int posX;
	private int posY;

	public Chat(String username, int window_x, int window_y) {
		this.userName = username;
		Consts.hostName = userName;
		this.posX = window_x;
		this.posY = window_y;
		logger = Logger.getLogger("Chat_" + userName);
		logger.addHandler(Logger.getGlobal().getHandlers()[0]);
		logger.setLevel(Level.ALL);

		// Establish new session with server
		RemoteVar<ArrayList<String>> registeredClients = new RemoteVar<ArrayList<String>>(ChatServer.NAME,
				ChatServer.SERVER_REGISTERED_CLIENTS);
		onlineList = new Signal<ArrayList<String>>("setup", () -> {
			if (registeredClients.get() == null)
				return new ArrayList<String>();
			else
				return registeredClients.get();
		}, registeredClients);
		onlineList.change().addHandler((o, n) -> {
			if (n.contains("toServerVar@" + username) && gui == null)
				setup();

			List<String> names = n.stream().map(x -> x.split("@")[1]).collect(Collectors.toList());
			setOnline(names);
		});

		toServer = new Var<String>("toServerVar", "");
		logger.fine("Setup: Waiting for Registration to Server ...");
	}

	private void setOnline(List<String> online) {
		if (lastOnline != null) {
			for (String s : lastOnline) {
				if (!online.contains(s)) {
					String msg = s + " has left the Chat.";
					// gui.displayMessage(0, msg);
				}
			}
			for (String s : online) {
				if (!lastOnline.contains(s)) {
					String msg = s + " has joined.";
					// gui.displayMessage(0, msg);
				}
			}
		}
		lastOnline = online;
		gui.setOnline(online);
	}

	private void setup() {
		logger.fine("Setup: Var successfully registered to Server");
		// Var for messages from server
		String serverVar = ChatServer.getRandom();
		sendServerMessage(serverVar);
		// while (!DreamClient.instance.listVariables().contains(serverVar + "@"
		// + ChatServer.NAME)) {
		logger.fine(DreamClient.instance.listVariables().toString());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// }
		RemoteVar<String> remote = new RemoteVar<String>(ChatServer.NAME, serverVar);
		Signal<String> fromServer = new Signal<>("fromServer", () -> {
			if (remote.get() != null)
				return remote.get();
			else
				return "";
		}, remote);
		fromServer.change().addHandler((oldValue, newValue) -> receivedServerMessage(newValue));

		logger.fine("Setup: Starting GUI");
		gui = new ChatGUI(userName, posX, posY);
		gui.setListener(this);

		// main room:
		// newRoom("Main", "*");
	}

	protected void receivedChatMessage(int roomNumber, String sender, String message) {
		gui.displayMessage(roomNumber, sender + ": " + message);
	}

	protected void receivedServerMessage(String message) {
		logger.fine("Received message from server: " + message);
		String[] temp = message.split(" ", 2);
		String command = temp[0];
		String rest = temp.length > 1 ? temp[1] : "";
		if (command.equalsIgnoreCase("room")) {
			// room <roomName> <recipient1> <recipient2> ...
			String[] t = rest.split(" ", 2);
			String roomName = t[0];
			String otherClients = t[1];
			logger.finer("Server requested a Var for room " + roomName + " with " + otherClients);

			String roomVar = newRoom(roomName);
			sendServerMessage("roomVar " + roomName + " " + roomVar);
		} else if (command.equalsIgnoreCase("roomVar")) {
			// roomVar <roomName> <member0>=<member0Var> <member1>=<member1Var>
			String[] t = rest.split(" ", 2);
			String roomName = t[0];
			String[] pairs = t[1].split(" ");
			for (String p : pairs) {
				String[] t2 = p.split("=", 2);
				String clientName = t2[0];
				String clientVar = t2[1];
				int roomNumber = roomNames.get(roomName);
				createConnection(roomNumber, roomName, clientName, clientVar);
			}
		}
	}

	protected void sendChatMessage(int roomNumber, String message) {
		rooms.get(roomNumber).set(message);
	}

	protected void sendServerMessage(String message) {
		toServer.set(message);
	}

	protected String newRoom(String roomName) {
		int roomNumber = gui.newChat(roomName);
		String roomVar = "room" + roomNumber;
		Var<String> room = new Var<String>(roomVar, "");
		rooms.put(roomNumber, room);
		roomNames.put(roomName, roomNumber);
		logger.fine("Room: Creating new Room(" + roomNumber + ")");
		return roomVar;
	}

	private void createConnection(int roomNumber, String roomName, String clientName, String clientVar) {
		if (clientName.equals(userName))
			return;
		RemoteVar<String> r = new RemoteVar<>(clientName, clientVar);
		Signal<String> s = new Signal<>(roomName + "_" + clientName, () -> {
			if (r.get() != null)
				return r.get();
			else
				return "";
		}, r);
		s.change().addHandler((oldValue, newValue) -> receivedChatMessage(roomNumber, clientName, newValue));
	}

	protected void typedMessage(String text) {
		if (!text.startsWith("/")) {
			// normal message
			int room = gui.getSelectedChat();
			gui.displayMessage(room, "You: " + text);
			sendChatMessage(room, text);
			logger.fine("Send Message to Room" + room + ": " + text);
		} else {
			// message to server
			processCommand(text.substring(1, text.length()));
		}
	}

	private void processCommand(String text) {
		String[] temp = text.split(" ", 2);
		String command = temp[0];
		String rest = temp.length > 1 ? temp[1] : "";
		if (command.equalsIgnoreCase("room")) {
			String[] temp1 = rest.split(" ", 2);
			String name = temp1[0];
			initiateNewRoom(name, temp1[1]);
		}
		logger.fine("Processed Command: " + text);
	}

	private void initiateNewRoom(String name, String recipients) {
		String roomVar = newRoom(name);
		// room command: room <Name> <Variable> <recipient1> <recipient2> ...
		logger.finer("Room: Sending to Server: " + "room " + name + " " + roomVar + " " + recipients);
		sendServerMessage("room " + name + " " + roomVar + " " + recipients);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			Logger.getGlobal().severe("username missing");
			return;
		}
		int x, y;
		for (String s : args) {
			System.out.print(s + ",");
		}
		System.out.println();
		if (args.length < 3)
			y = -1;
		else
			y = Integer.parseInt(args[2]);

		if (args.length < 2)
			x = -1;
		else
			x = Integer.parseInt(args[1]);
		EventQueue.invokeLater(() -> new Chat(args[0], x, y));
	}
}

class Pair<S, T> {
	private final S first;
	private final T second;

	Pair(S a, T b) {
		this.first = a;
		this.second = b;
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}
}