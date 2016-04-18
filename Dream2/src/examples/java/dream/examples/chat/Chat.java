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

	private Var<String> myMessages;
	private Var<String> incoming;
	private String userName;
	private ChatGUI gui;
	private List<String> listening;
	Signal<ArrayList<String>> onlineList;

	public Chat(String username) throws Exception {
		this.userName = username;

		Consts.hostName = userName;
		Logger.getGlobal().setLevel(Level.ALL);
		listening = new ArrayList<>();
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
				System.out.println("Setup: Server Registration done!");
				setup();
			}
			List<String> names = n.stream().map(x -> x.split("@")[1]).collect(Collectors.toList());
			setOnline(names);
			checkConnections(n);
		});

		myMessages = new Var<String>("chat_message", "");
		incoming = new Var<String>("incoming_messages", "");
		System.out.println("Setup: Waiting for Registration to Server ...");
	}

	private List<String> lastOnline;

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
				filter(x -> !listening.contains(x.getHost()) && x.getVar().startsWith("chat_")).//
				forEach(x -> {
					RemoteVar<String> temp = new RemoteVar<>(x.getHost(), x.getVar());
					listening.add(x.getHost());
					new Signal<String>("incoming" + x.getHost(), () -> {
						if (temp.get() != null)
							return x.getHost() + ": " + temp.get();
						else
							return "";
					}, temp).change().addHandler((oldVal, newVal) -> incoming.set(newVal));
					System.out.println("Adding listener to " + x.getHost());
				});
		/*
		 * if (!n.stream().map(x -> listening.contains(x)).reduce((a, b) -> a &&
		 * b).orElse(false)) { System.out.println("checking again in 5"); try {
		 * Thread.sleep(5000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 * 
		 * checkConnections(onlineList.get()); }
		 */
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

		System.out.println("Setup: Starting GUI");
		gui = new ChatGUI(userName);
		gui.setListener(this);

		display.change().addHandler((oldValue, newValue) -> {
			if (newValue != null) {
				Logger.getGlobal().fine("Received Message: " + incoming.get());
				gui.displayMessage(newValue);
			}
		});
	}

	protected void sendMessage(String text) {
		if (!text.startsWith("/")) {
			gui.displayMessage("You: " + text);
		}
		myMessages.set(text);
		Logger.getGlobal().fine("Send Message: " + text);
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.out.println("username missing");
				return;
			}
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						new Chat(args[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Pair<S, T> {
	private final S first;
	private final T second;

	Pair(S host, T var) {
		this.first = host;
		this.second = var;
	}

	public T getSecond() {
		return second;
	}

	public S getFirst() {
		return first;
	}

	public S getHost() {
		return first;
	}

	public T getVar() {
		return second;
	}
}