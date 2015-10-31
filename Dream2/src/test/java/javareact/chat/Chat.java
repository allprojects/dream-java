package javareact.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Var;

public class Chat implements ReactiveChangeListener<String> {

	private RemoteVar<String> remoteMessages;
	private Var<String> messages;
	private String userName;

	public Chat(String username) throws Exception {
		Consts.hostName = username;
		messages = new Var<String>("message", "");
		remoteMessages = new RemoteVar<String>("message@*");
		remoteMessages.addReactiveChangeListener(this);
		this.userName = username;
	}

	protected void writeMessage(String text) {
		messages.set(userName + ":" + text);
	}

	@Override
	public void notifyReactiveChanged(String newValue) {
		String[] msg = newValue.split(":", 2);
		if (!msg[0].equals(userName))
			System.out.println(newValue);
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1)
				System.out.println("username missing");

			// args[0]=topicName; args[1]=username
			Chat chat = new Chat(args[0]);

			// read from command line
			BufferedReader commandLine = new java.io.BufferedReader(
					new InputStreamReader(System.in));

			// loop until the word "exit" is typed
			while (true) {
				String s = commandLine.readLine();
				if (s.equalsIgnoreCase("exit")) {
					System.exit(0);// exit program
				} else
					chat.writeMessage(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}