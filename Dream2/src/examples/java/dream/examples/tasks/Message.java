/**
 * 
 */
package dream.examples.tasks;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Message format used for communication between different nodes
 * 
 * @author Ram
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -9119849212879479791L;
	Task task;
	HashMap<String, Integer> clock;

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @return the clock
	 */
	public HashMap<String, Integer> getClock() {
		return clock;
	}

	/**
	 * @param clock
	 *            the clock to set
	 */
	public void setClock(HashMap<String, Integer> clock) {
		this.clock = clock;
	}

}
