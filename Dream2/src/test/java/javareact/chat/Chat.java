package javareact.chat;

import java.awt.EventQueue;
import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;

public class Chat{

	private RemoteVar<String> remoteMessages;
	private Var<String> messages;
	private String userName;
	private ChatGUI gui;

	public Chat(String username) throws Exception {
		Consts.hostName = username;
		messages = new Var<String>("message", "");
		remoteMessages = new RemoteVar<String>("message@*");

		// split Sender + Message into two Signals (sHost + sMessage)
		Signal<String> sHost = new Signal<String>("sHost", () -> {
			if (remoteMessages.get() == null)
				return "";
			else 
				return remoteMessages.get().split(":", 2)[0];
		}, remoteMessages);
		
		Signal<String> sMessage = new Signal<String>("sMessage", () -> {
			if (remoteMessages.get() == null)
				return "";
			else 
				return remoteMessages.get().split(":", 2)[1];
		}, remoteMessages);
		
		Signal<String> display = new Signal<String>("display", () -> {
			if (!sHost.get().equals(userName))
				return sMessage.get();
			else
				return "";				
		}, sHost, sMessage);
		
		display.change().addHandler((oldValue, newValue) -> {
			if (!sHost.get().equals(userName))
				gui.displayMessage(sHost.get() + ": " + newValue);
		});
				
		
		this.userName = username;

		gui = new ChatGUI(userName);
		gui.setListener(this);
	}


	protected void sendMessage() {
		messages.set(userName + ":" + gui.getTypedText());
		gui.displayMessage("You: " + gui.getTypedText());
		gui.resetTypedText();
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1)
				System.out.println("username missing");

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						new Chat(args[0]);
					} catch (Exception e) {
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}