package dream.examples.scrumBoard.atomic;

import dream.examples.util.MultipleStarter;

/**
 * To start this example either:<br>
 * - run {@link Server}, {@link Creator} and {@link Monitor} in any order<br>
 * - or run this class.<br>
 * This class will start all three classes each in a seperate instance of the
 * JVM. It will also stop all classes if one of them is stopped normally.<br>
 *
 * @author Min Yang
 * @author Tobias Becker
 */
public class InitApp {

	public static void main(String... args) {
		MultipleStarter.addStartQueue(LockManager.class);
		MultipleStarter.addStartQueue(Server.class);
		MultipleStarter.addStartQueue(Creator.class);
		MultipleStarter.addStartQueue(Creator.class);
		MultipleStarter.addStartQueue(Monitor.class);
		MultipleStarter.start();
	}
}