package dream.examples.scrumBoard.atomic;

import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import dream.client.Var;
import dream.examples.scrumBoard.common.Assignment;
import dream.examples.scrumBoard.common.CreatorGUI;

/**
 * Interface to create new Tasks. May be started multiple times!
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Creator extends LockClient implements dream.examples.scrumBoard.common.CreatorGUI.Creator {

	public static final String VAR_newAssignment = "newAssign";

	private Var<LinkedList<Assignment>> assignmentCreator;

	public Creator() {
		super("Creator" + new Random().nextInt(1000));
	}

	protected void setup() {
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
		lock();
		assignmentCreator.modify((old) -> old.addLast(t));
		unlock();
	}
}
