package dream.examples.taskBoard;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small serializable class that represents a task assigned to a developer.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Task implements Serializable {
	private static final long serialVersionUID = 8329097603920137211L;
	public static String pattern = "D(\\d*):T(\\d*)";
	private int developer;
	private int task;

	public Task(String input) {
		Matcher m = Pattern.compile(pattern).matcher(input);
		if (m.matches()) {
			developer = Integer.parseInt(m.group(1));
			task = Integer.parseInt(m.group(2));
		} else
			throw new UnsupportedOperationException("Wrong Input");
	}

	public static boolean isValid(String input) {
		return input.matches(pattern);
	}

	public int getDeveloper() {
		return developer;
	}

	public int getTask() {
		return task;
	}

	public String getDevString() {
		return Integer.toString(developer);
	}

	public String getTaskString() {
		return Integer.toString(task);
	}

}
