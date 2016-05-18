package dream.examples.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dream.client.DreamClient;
import dream.common.Consts;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;
import polimi.reds.broker.overlay.TCPTransport;

/**
 * A helper class for client implementations that ensures that a instance of the
 * Dream Server and LockManager are running, as well as setting up basic
 * infrastructure for clients.
 * 
 * @author Tobias Becker
 */
public abstract class Client {

	private static boolean lockManagerStarted;
	private static boolean serverStarted;
	protected Logger logger;

	public Client(String name) {
		// set up a logger
		logger = Logger.getLogger(name);

		// make sure a DreamServer is running
		startDream();

		// set hostName
		Consts.hostName = name;

		// connect to the dependency graph
		DreamClient.instance.connect();

		init();

		// wait for vars needed by the client
		try {
			if (!waitForVars().isEmpty())
				logger.fine("Waiting for Vars: " + waitForVars());
			while (!allVarsAvailable()) {
				Thread.sleep(500);
			}
			if (!waitForVars().isEmpty())
				logger.fine("Vars are now all available.");
			logger.fine("Client initialization finished.");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean allVarsAvailable() {
		for (String var : waitForVars()) {
			if (!DreamClient.instance.listVariables().contains(var))
				return false;
		}
		return true;
	}

	/**
	 * Override this method if you need certain variables to be available before
	 * your computation can start. The constructor will block until they are on
	 * the dependency graph.
	 * 
	 * @return a list of variables to be available before continuing
	 */
	protected List<String> waitForVars() {
		return new ArrayList<String>();
	}

	/**
	 * Override this method if you want to initialize something before waiting
	 * for the vars in {@link waitForVars()}.
	 * 
	 */
	protected void init() {

	}

	private void startDream() {
		try {
			TCPTransport test = new TCPTransport(Consts.serverPort);
			test.start();
			test.stop();
		} catch (IOException e) {
			serverStarted = true;
			logger.fine("Server already running");
		}
		try {
			TCPTransport test = new TCPTransport(Consts.lockManagerPort);
			test.start();
			test.stop();
		} catch (IOException e) {
			lockManagerStarted = true;
			logger.fine("LockManager already running");
		}
		try {
			startServer();
			startLockManager();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Failed to sleep", e);
		}
	}

	private static final void startServer() throws InterruptedException {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		Thread.sleep(100);
	}

	private static final void startLockManager() throws InterruptedException {
		if (!lockManagerStarted) {
			LockManagerLauncher.start();
			lockManagerStarted = true;
		}
		Thread.sleep(100);
	}

	public String getHostName() {
		return Consts.hostName;
	}

}
