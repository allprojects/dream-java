package dream.examples.taskBoard;

import dream.examples.util.NewJvmHelper;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description User can first run
 *              ServerNode.java(..examples.taskBoard.ServerNode.java), then run
 *              NewTaskGUI.java (..examples.taskBoard.NewTaskGUI.java), then run
 *              TaskReviewer.java (..examples.taskBoard.TaskReviewer.java)
 * 
 */
// TODO run the whole package together
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