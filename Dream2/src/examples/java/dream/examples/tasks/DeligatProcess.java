/**
 * 
 */
package dream.examples.tasks;

import java.util.HashMap;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.client.Var;
import dream.common.Consts;
import dream.examples.util.VectorClock;

/**
 * @author Ram
 *
 */
public class DeligatProcess {

	static int i;
	int clock;
	VectorClock localClock = new VectorClock("p2");
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

	public DeligatProcess() {
		// TODO Auto-generated constructor stub
	}

	private void init() {
		Consts.hostName = "Host2";
		RemoteVar<Message> rv = new RemoteVar<Message>("Host1", "TASK");
		Var<Message> myVar = new Var<Message>("TASK_ASSIGNED", null);

		Signal<Message> s = new Signal<Message>("s", () -> {
			return rv.get();
		} , rv);

		// Register a handler which will be executed upon receiving the signal
		s.change().addHandler((oldVal, val) -> {

			localClock.updateClock();
			HashMap<String, Integer> messageClock = val.getClock();
			if (localClock.isNew(messageClock)) {
				localClock.updateClock(messageClock);
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			val.getTask().setAssignee(i++ % 10 + "");
			localClock.updateClock();
			val.setClock(localClock.getLocalClock());
			myVar.set(val);

		});
	}

	public static void main(String[] args) {
		new DeligatProcess().init();

	}

}
