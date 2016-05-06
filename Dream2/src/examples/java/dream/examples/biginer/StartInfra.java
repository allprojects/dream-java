package dream.examples.biginer;

import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class StartInfra {

	public static void main(String args[]) {
		try {
			// Start the Server
			ServerLauncher.start();

			// Start the LockManager
			LockManagerLauncher.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}
}
