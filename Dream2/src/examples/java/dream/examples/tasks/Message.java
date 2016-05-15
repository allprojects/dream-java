/**
 * 
 */
package dream.examples.tasks;

import java.io.Serializable;

/**
 * Message format used for communication between different nodes
 * 
 * @author Ram
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -9119849212879479791L;
	Task task;
	String clock;

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
	public String getClock() {
		return clock;
	}

	/**
	 * @param clock
	 *            the clock to set
	 */
	public void setClock(String clock) {
		this.clock = clock;
	}

}
