package dream.examples.chat;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Set;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import javafx.util.Pair;

public class Chat {

	private RemoteVar<String> remoteMessages;
	private Var<String> myMessages;
	private String userName;
	private ChatGUI gui;

	public Chat(String username) throws Exception {
		this.userName = username;

		Consts.hostName = userName;
		// Establish new session with server
		RemoteVar<ArrayList<String>> var = new RemoteVar<ArrayList<String>>(ChatServer.NAME,
				ChatServer.SERVER_REGISTERED_CLIENTS);
		Signal<ArrayList<String>> setup = new Signal<ArrayList<String>>("setup", () -> {
			if (var.get() == null)
				return new ArrayList<String>();
			else
				return var.get();
		} , var);
		setup.change().addHandler((o, n) -> {
			if (n.contains(username))
				setup();
			System.out.println("reg clients: " + n);
		});

		myMessages = new Var<String>("chat_message", "");

		System.out.println("Setup: Waiting for Information from Server ...");
	}

	private void setup() {
		if (gui != null)
			return;
		Set<String> vars = DreamClient.instance.listVariables();
		String serverVar = vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> x.getKey().equals(ChatServer.NAME) && x.getValue().contains(userName)).//
				reduce(null, (a, b) -> b).getValue();
		remoteMessages = new RemoteVar<String>(ChatServer.NAME, serverVar);

		Signal<String> display = new Signal<String>("display", () -> {
			if (remoteMessages.get() != null)
				return remoteMessages.get();
			else
				return "";
		} , remoteMessages);

		gui = new ChatGUI(userName);
		gui.setListener(this);

		display.change().addHandler((oldValue, newValue) -> {
			gui.displayMessage(newValue);
		});
	}

	protected void sendMessage() {
		myMessages.set(gui.getTypedText());
		gui.displayMessage("You: " + gui.getTypedText());
		gui.resetTypedText();
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.out.println("username missing");
				return;
			}
			// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
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