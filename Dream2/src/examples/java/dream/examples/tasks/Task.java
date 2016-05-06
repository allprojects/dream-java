/**
 * 
 */
package dream.examples.tasks;

import java.util.ArrayList;

/**
 * @author Ram
 *
 */
public class Task {
	private String name;
	private String assignee;
	private String parentTask;
	private ArrayList<Task> subTasks;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * @param assignee
	 *            the assignee to set
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * @return the parentTask
	 */
	public String getParentTask() {
		return parentTask;
	}

	/**
	 * @param parentTask
	 *            the parentTask to set
	 */
	public void setParentTask(String parentTask) {
		this.parentTask = parentTask;
	}

	/**
	 * @return the subTasks
	 */
	public ArrayList<Task> getSubTasks() {
		return subTasks;
	}

	/**
	 * @param subTasks
	 *            the subTasks to set
	 */
	public void setSubTasks(ArrayList<Task> subTasks) {
		this.subTasks = subTasks;
	}

}