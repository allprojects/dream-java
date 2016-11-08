/**
 * 
 */
package dream.examples.tasks.inconsistent;

import java.util.ArrayList;

/**
 * @author Ram
 *
 */
public class Task extends ArrayList<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7635579009380741902L;
	private String name;
	private String assignee;
	private int id;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the clock
	 */
	public int getClock() {
		return clock;
	}

	/**
	 * @param clock
	 *            the clock to set
	 */
	public void setClock(int clock) {
		this.clock = clock;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	private int clock;
	private String description;

	public Task(String name) {
		this.setName(name);
	}

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

}
