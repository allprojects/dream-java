package dream.examples.chat;

import dream.examples.chat.core.Chat;
import dream.examples.chat.core.ChatServer;
import dream.examples.util.MultipleStarter;

/**
 * Convenience class to start ChatServer and x Chats (x = CHAT_COUNT), each in
 * its own VM.
 * 
 * To exit all processes just close one chat window.
 */
public class Starter {

	public static final int CHAT_COUNT = 4;

	public static void main(String[] args) {
		new Starter().start();
	}

	private static final String[] names = { "Alice", "Bob", "Chris", "David", "Eve", "Fred", "Georg", "Hans", "Igor" };
	int xStep = 450;
	int yStep = 175;

	private void start() {
		MultipleStarter.addStartQueue(ChatServer.class);
		int x = 0;
		int y = 0;
		for (int i = 0; i < CHAT_COUNT; i++) {
			MultipleStarter.addStartQueue(Chat.class, getName(i), Integer.toString(x), Integer.toString(y));
			x += xStep;
			if (x >= 3 * xStep) {
				x = 0;
				y += yStep;
			}
		}
		MultipleStarter.start();
	}

	private String getName(int i) {
		if (i < names.length)
			return names[i];
		else
			return names[i % names.length] + "" + i;
	}
}
