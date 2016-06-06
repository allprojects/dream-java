package dream.examples.taskBoard;

import java.util.ArrayList;
import java.util.List;

import dream.examples.util.NewJvmHelper;

/**
 * To start this example either:<br>
 * - run {@link Server}, {@link Creator} and {@link Monitor} in any
 * order<br>
 * - or run this class.<br>
 * This class will start all three classes each in a seperate instance of the
 * JVM. It will also stop all classes if one of them is stopped normally.<br>
 * <b>WARNING:</b> If they are destroyed forcefully via Eclipse (or any other
 * way) there may continue to run and will have to be exited manually.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class InitApp {
	private static List<Process> processes;

	public static void main(String... args) {
		processes = new ArrayList<>();
		processes.add(NewJvmHelper.startNewJVM(Server.class));
		processes.add(NewJvmHelper.startNewJVM(Creator.class));
		processes.add(NewJvmHelper.startNewJVM(Creator.class));
		processes.add(NewJvmHelper.startNewJVM(Monitor.class));

		sleep(-1);
	}

	private static void sleep(int time) {
		do {
			try {
				Thread.sleep(time == -1 ? 1000 : time);
				checkExit();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (time == -1);

	}

	private static void checkExit() {
		for (Process p : processes) {
			if (!p.isAlive()) {
				System.out.println(p.getClass().getSimpleName() + " closed ... exiting!");
				destr();
				System.exit(0);
			}
		}
	}

	private static void destr() {
		for (Process p : processes) {
			p.destroyForcibly();
		}
	}
}