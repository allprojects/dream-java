/**
 * 
 */
package dream.examples.tasks;

import java.util.ArrayList;
import java.util.HashMap;

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
	int localClock = 0;
	UiUpdatesListner listner;
	ArrayList<HashMap<Integer, Task>> disClock = new ArrayList<>();
	HashMap<Integer, Task> p1 = new HashMap<Integer, Task>();
	HashMap<Integer, Task> p2 = new HashMap<Integer, Task>();
	HashMap<Integer, Task> p3 = new HashMap<Integer, Task>();
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

	public void isEvent() {
		boolean COMPLEX_EVENT = false;
		for (Integer i : p1.keySet()) {
			for (Integer j : p2.keySet()) {
				COMPLEX_EVENT = (p2.get(j).getId() == p1.get(i).getId() ? true : false);
				if (COMPLEX_EVENT) {
					Task task = p1.get(i);
					task.setAssignee(p2.get(j).getAssignee());
					p1.remove(i);
					p2.remove(j);
					complexEvent.set(task);
					break;
				}
			}
		}
	}

	public void run() {

		Consts.hostName = "Host3";

		RemoteVar<Message> task = new RemoteVar<Message>("Host1", "TASK1");
		RemoteVar<Message> taskDeligated = new RemoteVar<Message>("Host2", "TASK_ASSIGNED");

		Signal<Message> signalFromMaster = new Signal<Message>("s", () -> {
			return task.get();
		} , task);
		Signal<Message> signalFromDeligator = new Signal<Message>("s1", () -> {
			return taskDeligated.get();
		} , taskDeligated);

		Signal<Task> signalForComplexEvent = new Signal<Task>("s2", () -> {
			return complexEvent.get();
		} , complexEvent);

		// Register a handler which will be executed upon receiving the signal
		// from master process
		signalFromMaster.change().addHandler((oldVal, val) -> {
			Task t = (Task) val.getTask();
			p1.put(t.getClock(), t);
			if (t != null) {
				System.out.println("[MASTER]\nAssignee:" + t.getAssignee() + "\nTaskName: " + t.getName()
						+ "\nDescription " + t.getDescription() + " \nAnnotated clock: " + val.getClock());

				System.out.println("_________________________________________________");
			}
			isEvent();
		});

		// Register a handler which will be executed upon receiving the signal
		// from delegate process
		signalFromDeligator.change().addHandler((oldVal, val) -> {
			Task t = (Task) val.getTask();
			if (t != null) {
				System.out.println("[DELEGATOR]\nTask Id:" + t.getId() + "\nAssignee:" + t.getAssignee()
						+ "\nTaskName: " + t.getName() + "\nDescription " + t.getDescription() + "\nAnnotated clock: "
						+ val.getClock());
				System.out.println("_________________________________________________");
			}
			p2.put(t.getClock(), t);
			isEvent();
		});
		signalForComplexEvent.change().addHandler((oldVal, val) -> {
			Task t = val;
			listner.updateTasks("Task Id:" + t.getId() + "\nAssignee:" + t.getAssignee() + "\nTaskName: " + t.getName()
					+ "\nDescription " + t.getDescription());
		});
	}

	public void addListners(WorkerProcess dsUi) {
		this.listner = dsUi;
	}

}
