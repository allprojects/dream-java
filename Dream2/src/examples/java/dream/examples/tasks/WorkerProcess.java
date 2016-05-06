/**
 * 
 */
package dream.examples.tasks;

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

		if (args.length < 2) {
			System.out.println("Usage WorkerProcess <name> <host>\n Example : WorkerProcess \"worker1\" \"Host1\"");
		}
		new WorkerProcess(args[0], args[1]);
	}

}
