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

		Consts.hostName = "Host2";
		RemoteVar<Task> rv = new RemoteVar<Task>("Host1", "TASK_");

		Signal<Task> s = new Signal<Task>("s", () -> {
			System.out.println("received New Object" + rv.get().getName());
			return rv.get();
		} , rv);

		// Register a handler which will be executed upon receiving the signal
		s.change().addHandler((oldVal, val) -> {
			System.out.println("Consumed: " + val.getName());
		});
	}

}
