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
		RemoteVar<ArrayList<String>> registeredClients = new RemoteVar<ArrayList<String>>(ChatServer.NAME,
				ChatServer.SERVER_REGISTERED_CLIENTS);
		Signal<ArrayList<String>> onlineList = new Signal<ArrayList<String>>("setup", () -> {
			if (registeredClients.get() == null)
				return new ArrayList<String>();
			else
				return registeredClients.get();
		} , registeredClients);
		onlineList.change().addHandler((o, n) -> {
			if (n.contains(username) && gui == null) {
				System.out.println("Setup: Server Registration done!");
				setup(n);
			} else
				gui.setOnline(n);
		});

		myMessages = new Var<String>("chat_message", "");

		System.out.println("Setup: Waiting for Registration to Server ...");
	}

	private void setup(ArrayList<String> online) {
		if (gui != null)
			return;
		Set<String> vars = DreamClient.instance.listVariables();
		String serverVar = vars.stream().map(x -> new Pair<String, String>(x.split("@")[1], x.split("@")[0])).// Pair(Host,Var)
				filter(x -> x.getKey().equals(ChatServer.NAME) && x.getValue().contains(userName)).//
				reduce(null, (a, b) -> b).getValue();
		System.out.println("Setup: Listening to Main Chat provided by Server");
		remoteMessages = new RemoteVar<String>(ChatServer.NAME, serverVar);

		Signal<String> display = new Signal<String>("display", () -> {
			if (remoteMessages.get() != null)
				return remoteMessages.get();
			else
				return "";
		} , remoteMessages);

		System.out.println("Setup: Starting GUI");
		gui = new ChatGUI(userName);
		gui.setListener(this);

		System.out.println("Setup: Initializing Online-List");
		gui.setOnline(online);
		display.change().addHandler((oldValue, newValue) -> {
			gui.displayMessage(newValue);
		});
	}

	protected void sendMessage(String text) {
		myMessages.set(text);
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