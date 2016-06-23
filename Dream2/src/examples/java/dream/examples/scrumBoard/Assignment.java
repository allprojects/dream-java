package dream.examples.scrumBoard;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small serializable class that represents a task assigned to a developer.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Assignment implements Serializable, Comparable<Assignment> {
	private static final long serialVersionUID = 8329097603920137211L;
	public static String pattern = "D(\\d*):T(\\d*)";
	private int developer;
	private int task;
	private Date time;

	public Assignment(String input) {
		Matcher m = Pattern.compile(pattern).matcher(input);
		if (m.matches()) {
			developer = Integer.parseInt(m.group(1));
			task = Integer.parseInt(m.group(2));
			time = new Date();
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

	public Date getTime() {
		return time;
	}

	@Override
	public int compareTo(Assignment o) {
		return time.compareTo(o.time);
	}

}
