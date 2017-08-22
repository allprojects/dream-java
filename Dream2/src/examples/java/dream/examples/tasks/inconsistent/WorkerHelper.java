/**
 * 
 */
package dream.examples.tasks.inconsistent;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;

/**
 * @author Ram
 *
 */
public class WorkerHelper implements Runnable {
	static int i = 0;
	UiUpdatesListner listner;
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

	public WorkerHelper() {
		this.setProcessName(processName);

	}

	public void handleEvent(Message p1, Message p2) {
		// update clock on receiving new message
		Task task = p1.getTask();
		listner.updateTasks("\nTask Name: " + task.getName() + "\n" + "Task Discription: " + task.getDescription()
				+ "\n" + "Assignee ID: " + p1.getTask().getAssignee(), true);

	}

	public void isEvent(String val) {
		listner.updateTasks(val, false);

	}

	public void run() {
		Consts.setHostName("Host3");

		RemoteVar<Message> task = new RemoteVar<Message>("Host1", "TASK2");
		RemoteVar<Message> taskDeligated = new RemoteVar<Message>("Host2", "TASK_ASSIGNED");

		Signal<Message> signalFromMaster = new Signal<Message>("s", () -> {
			return task.get();
		}, new Message(), task);
		Signal<Message> signalFromDeligator = new Signal<Message>("s1", () -> {
			return taskDeligated.get();
		}, new Message(), taskDeligated);

		// Register a handler which will be executed upon receiving the signal
		// from master process
		signalFromMaster.change().addHandler((oldVal, val) -> {
			if (val != null) {
				handleEvent(val, null);
			}
		});

		// Register a handler which will be executed upon receiving the signal
		// from delegate process
		signalFromDeligator.change().addHandler((oldVal, val) -> {
			if (val != null) {
				handleEvent(val, null);
			}
		});

	}

	public void addListners(WorkerProcess dsUi) {
		this.listner = dsUi;
	}

}
