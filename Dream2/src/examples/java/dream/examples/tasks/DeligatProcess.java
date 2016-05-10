/**
 * 
 */
package dream.examples.tasks;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;

/**
 * @author Ram
 *
 */
public class DeligatProcess {

	static int i;
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

	public DeligatProcess(String processName, String host) {
		this.setProcessName(processName);
	}

	public static void main(String[] args) {

		Consts.hostName = "Host2";
		RemoteVar<Task> rv = new RemoteVar<Task>("Host1", "TASK");
		Var<Task> myVar = new Var<Task>("TASK_ASSIGNED", null);

		Signal<Task> s = new Signal<Task>("s", () -> {
			return rv.get();
		} , rv);

		// Register a handler which will be executed upon receiving the signal
		s.change().addHandler((oldVal, val) -> {

			val.setAssignee(i++ + "");
			myVar.set(val);

		});
	}

}
