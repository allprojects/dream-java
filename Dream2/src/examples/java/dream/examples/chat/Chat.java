package dream.examples.chat;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;

public class Chat {

	private final Var<String> myMessages;
	private final Var<String> incoming;
	private final String userName;
	private ChatGUI gui;
	private final List<String> listening = new ArrayList<>();
	private final Signal<ArrayList<String>> onlineList;

	private List<String> lastOnline;
	private final static Logger logger = Logger.getGlobal();

	public Chat(String username) {
		this.userName = username;
		Consts.hostName = userName;

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
			if (n.contains("chat_message@" + username) && gui == null) {
				logger.fine("Setup: Server Registration done!");
				setup();
			}
			List<String> names = n.stream().map(x -> x.split("@")[1]).collect(Collectors.toList());
			setOnline(names);
			checkConnections(n);
		});

		myMessages = new Var<String>("chat_message", "");
		incoming = new Var<String>("incoming_messages", "");
		logger.fine("Setup: Waiting for Registration to Server ...");
	}

	private void setOnline(List<String> online) {
		if (lastOnline != null) {
			for (String s : lastOnline) {
				if (!online.contains(s)) {
					String msg = s + " has left the Chat.";
					gui.displayMessage(msg);
				}
			}
			for (String s : online) {
				if (!lastOnline.contains(s)) {
					String msg = s + " has joined.";
					gui.displayMessage(msg);
				}
			}
		}
		lastOnline = online;
		gui.setOnline(online);
	}

	private void checkConnections(ArrayList<String> n) {
		n.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> !listening.contains(x.getFirst()) && x.getSecond().startsWith("chat_")).//
				forEach(x -> {
					RemoteVar<String> temp = new RemoteVar<>(x.getFirst(), x.getSecond());
					listening.add(x.getFirst());
					new Signal<String>("incoming" + x.getFirst(), () -> {
						if (temp.get() != null)
							return x.getFirst() + ": " + temp.get();
						else
							return "";
					}, temp).change().addHandler((oldVal, newVal) -> incoming.set(newVal));
					logger.finer("Adding listener to " + x.getFirst());
				});
	}

	private void setup() {
		Signal<String> display = new Signal<String>("display", () -> {
			if (incoming.get().startsWith("/")) {
				String[] temp = incoming.get().split(" ", 2);
				String command = temp[0].substring(1, temp[0].length());
				String rest = temp.length > 1 ? temp[1] : "";
				// QUIT - for now only used to update the registeredClients list
				// (aka who's online)
				if (command.equalsIgnoreCase("W")) {
					String[] temp1 = rest.split(" ", 2);
					String sender = temp1[0];
					String message = temp1.length > 1 ? temp1[1] : "";
					return sender + " whispered: " + message;
				} else
					return null;
			} else
				return incoming.get();
		}, incoming);

		logger.fine("Setup: Starting GUI");
		gui = new ChatGUI(userName);
		gui.setListener(this);

		display.change().addHandler((oldValue, newValue) -> {
			if (newValue != null) {
				logger.fine("Received Message: " + incoming.get());
				gui.displayMessage(newValue);
			}
		});
	}

	protected void sendMessage(String text) {
		if (!text.startsWith("/")) {
			gui.displayMessage("You: " + text);
		}
		myMessages.set(text);
		logger.fine("Send Message: " + text);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			logger.severe("username missing");
			return;
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Chat(args[0]);
			}
		});
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