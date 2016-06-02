package dream.examples.taskBoard;

import dream.examples.util.NewJvmHelper;

/**
 * To start this example either:<br>
 * - run {@link ServerNode}, {@link TaskCreater} and {@link TaskMonitor} in any
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
	private static Process serverNode;
	private static Process viewer;
	private static Process gui;

	public static void main(String... args) {
		serverNode = NewJvmHelper.startNewJVM(ServerNode.class);
		gui = NewJvmHelper.startNewJVM(TaskCreater.class);
		viewer = NewJvmHelper.startNewJVM(TaskMonitor.class);

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
		if (!serverNode.isAlive()) {
			System.out.println("server closed ... exiting!");
			destr();
			System.exit(0);
		}
		if (!viewer.isAlive()) {
			System.out.println("viewer window closed ... exiting!");
			destr();
			System.exit(0);
		}
		if (!gui.isAlive()) {
			System.out.println("gui window closed ... exiting!");
			destr();
			System.exit(0);
		}

	}

	private static void destr() {
		if (serverNode != null) {
			serverNode.destroyForcibly();
		}
		if (viewer != null) {
			viewer.destroyForcibly();
		}
		if (gui != null) {
			gui.destroyForcibly();
		}
	}
}