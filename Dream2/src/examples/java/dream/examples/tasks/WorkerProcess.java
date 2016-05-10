/**
 * 
 */
package dream.examples.tasks;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

/**
 * @author Ram
 *
 */
public class WorkerProcess {
	static int i = 0;
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

	public WorkerProcess(String processName, String host) {
		this.setProcessName(processName);
	}

	public static void main(String[] args) {

		Consts.hostName = "Host3";

		RemoteVar<Task> task = new RemoteVar<Task>("Host1", "TASK");
		RemoteVar<Task> taskDeligated = new RemoteVar<Task>("Host2", "TASK_ASSIGNED");

		Signal<Task> signalFromMaster = new Signal<Task>("s", () -> {
			return task.get();
		} , task);
		Signal<Task> signalFromDeligator = new Signal<Task>("s1", () -> {
			return taskDeligated.get();
		} , taskDeligated);

		// Register a handler which will be executed upon receiving the signal
		// from master process
		signalFromMaster.change().addHandler((oldVal, val) -> {
			System.out.println("FROM MASTER : " + val.getAssignee() + " " + val.getName());
		});

		// Register a handler which will be executed upon receiving the signal
		// from delegate process
		signalFromDeligator.change().addHandler((oldVal, val) -> {
			System.out.println("FROM DELIGATOR : " + val.getAssignee() + " " + val.getName());
		});
	}

}
