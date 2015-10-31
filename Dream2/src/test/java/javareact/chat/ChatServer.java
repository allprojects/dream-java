package javareact.chat;

import java.util.HashSet;
import java.util.Set;

import javareact.common.Consts;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class ChatServer {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	public static void main(String[] args) {
		new ChatServer().start();
	}

	public void start() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

		Consts.hostName = "ChatServer";

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
