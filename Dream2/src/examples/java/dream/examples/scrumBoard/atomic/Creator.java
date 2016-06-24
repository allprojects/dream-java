package dream.examples.scrumBoard.atomic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.examples.scrumBoard.common.Assignment;
import dream.examples.scrumBoard.common.CreatorGUI;
import dream.examples.util.Client;
import dream.examples.util.Pair;

/**
 * Interface to create new Tasks. May be started multiple times!
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Creator extends Client implements dream.examples.scrumBoard.common.CreatorGUI.Creator {

	public static final String VAR_newAssignment = "newAssign";

	private Var<LinkedList<Assignment>> assignmentCreator;

	private Var<Boolean> lockRequest;

	public Creator() {
		super("Creator" + new Random().nextInt(1000));

		// Establish new session with LockManager
		RemoteVar<ArrayList<Pair<String, String>>> registeredClients = new RemoteVar<>(LockManager.NAME,
				LockManager.VAR_clients);
		Signal<ArrayList<Pair<String, String>>> s = new Signal<>("s", () -> {
			if (registeredClients.get() == null)
				return new ArrayList<Pair<String, String>>();
			else
				return registeredClients.get();
		}, registeredClients);
		s.change().addHandler((o, n) -> {
			if (n.contains(new Pair<>(this.getHostName(), LockManager.VAR_requestLock)) && assignmentCreator == null)
				setup();
		});

		lockRequest = new Var<>(LockManager.VAR_requestLock, false);
		logger.fine("Setup: Waiting for Registration to LockManager ...");
	}

	private void setup() {
		assignmentCreator = new Var<>(VAR_newAssignment, new LinkedList<>());
		new CreatorGUI(this);
	}

	public static void main(String[] args) {
		new Creator();
	}

	public Logger getLogger() {
		return logger;
	}

	public void addAssignment(Assignment t) {
		// TODO: lock
		assignmentCreator.modify((old) -> old.addLast(t));
		// TODO: unlock
	}
}
