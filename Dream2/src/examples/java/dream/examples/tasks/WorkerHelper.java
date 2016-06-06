/**
 * 
 */
package dream.examples.tasks;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.examples.util.VectorClock;

/**
 * @author Ram
 *
 */
public class WorkerHelper implements Runnable {
	static int i = 0;
	UiUpdatesListner listner;
	VectorClock localClock = new VectorClock("p3");
	Var<Task> complexEvent = new Var<Task>("COMPEVENT", null);
	/**
	 * @param args
	 */
	private String processName;

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName
	 *            the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public WorkerHelper(String processName, String host) {
		this.setProcessName(processName);
	}

	public WorkerHelper() {
		// TODO Auto-generated constructor stub
	}

	public void isEvent(Message val) {
		// update clock on receiving new message
		localClock.updateClock();
		// check if the received message is new
		if (localClock.isNew(val.getClock())) {
			// update clock with the help of new message
			localClock.updateClock(val.getClock());
			Task task = val.getTask();
			listner.updateTasks("\nTask Name: " + task.getName() + "\n" + "Task Discription: " + task.getDescription()
					+ "\n" + "Assignee ID: " + task.getAssignee(), true);
			System.out.println("New Message and Accepted \n" + "Task Name: " + task.getName() + "\n"
					+ "Task Discription: " + task.getDescription() + "\n" + "Assignee ID: " + task.getAssignee());

		} else {
			Task task = val.getTask();
			listner.updateTasks("\nTask Name: " + task.getName() + "\n" + "Task Discription: " + task.getDescription()
					+ "\n" + "Assignee ID: " + task.getAssignee(), false);
			System.out.println("New Message and Rejected \n" + "Task Name: " + task.getName() + "\n"
					+ "Task Discription: " + task.getDescription() + "\n" + "Assignee ID: " + task.getAssignee());
		}
		listner.updateClockinUi(localClock.getLocalClock().toString());
	}

	public void run() {

		Consts.hostName = "Host3";

		RemoteVar<Message> task = new RemoteVar<Message>("Host1", "TASK2");
		RemoteVar<Message> taskDeligated = new RemoteVar<Message>("Host2", "TASK_ASSIGNED");

		Signal<Message> signalFromMaster = new Signal<Message>("s", () -> {
			return task.get();
		} , task);
		Signal<Message> signalFromDeligator = new Signal<Message>("s1", () -> {
			return taskDeligated.get();
		} , taskDeligated);

		// Register a handler which will be executed upon receiving the signal
		// from master process
		signalFromMaster.change().addHandler((oldVal, val) -> {
			if (val != null) {
				isEvent(val);
			}
		});

		// Register a handler which will be executed upon receiving the signal
		// from delegate process
		signalFromDeligator.change().addHandler((oldVal, val) -> {
			if (val != null) {
				isEvent(val);
			}
		});

	}

	public void addListners(WorkerProcess dsUi) {
		this.listner = dsUi;
	}

}
