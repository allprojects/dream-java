package dream.examples.chat;

import java.util.ArrayList;

import dream.examples.chat.core.Chat;
import dream.examples.chat.core.ChatServer;
import dream.examples.util.NewJvmHelper;

/**
 * Convenience class to start ChatServer and x Chats (x = CHAT_COUNT), each in
 * its own VM.
 * 
 * To exit all processes just close one chat window.
 */
public class Starter {

	public static final int CHAT_COUNT = 4;

	private static ArrayList<Process> processes = new ArrayList<Process>();

	public static void main(String[] args) {
		new Starter().start();
	}

	private Thread serverThread;

	private static final String[] names = { "Alice", "Bob", "Chris", "David", "Eve", "Fred", "Georg", "Hans", "Igor" };
	int xStep = 450;
	int yStep = 175;

	private void start() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> onExit()));
		serverThread = new Thread(() -> ChatServer.main(null));
		serverThread.start();
		sleep(1500);
		int x = 0;
		int y = 0;
		for (int i = 0; i < CHAT_COUNT; i++) {
			processes.add(NewJvmHelper.startNewJVM(Chat.class, getName(i), Integer.toString(x), Integer.toString(y)));
			x += xStep;
			if (x >= 3 * xStep) {
				x = 0;
				y += yStep;
			}
		}
		// sleep infinite time
		sleep(-1);
	}

	private String getName(int i) {
		if (i < names.length)
			return names[i];
		else
			return names[i % names.length] + "" + i;
	}

	@Override
	protected void finalize() throws Throwable {
		onExit();
		super.finalize();
	}

	private void sleep(int time) {
		do {
			try {
				Thread.sleep(time == -1 ? 1000 : time);
				checkExit();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (time == -1);

	}

	private void checkExit() {
		for (Process p : processes) {
			if (!p.isAlive()) {
				System.out.println("One window closed ... exiting!");
				System.exit(0);
			}
		}
	}

	private void onExit() {
		System.out.println("exit");
		for (Process p : processes) {
			p.destroyForcibly();
			System.out.println("Destroying " + p.toString());
		}
	}
}
