package protopeer.time;

import protopeer.util.quantities.*;

public abstract class Event implements Comparable<Event> {

	private Time executionTime;

	private static int nextEventID = 0;

	private int eventID;

	public abstract void execute();

	public Event() {
		this.eventID = nextEventID++;
	}

	public int compareTo(Event otherEvent) {
		int c = executionTime.compareTo(otherEvent.executionTime);
		if (c == 0) {
			c = eventID - otherEvent.eventID;
		}		
		return c;
	}

	@Deprecated
	public double getExecutionTime() {
		return Time.inMilliseconds(executionTime);
	}

	@Deprecated
	public void setExecutionTime(double executionTime) {
		this.executionTime = Time.inMilliseconds(executionTime);
	}

	public Time getExecTime() {
		return executionTime;
	}

	public void setExecTime(Time executionTime) {
		this.executionTime = executionTime;
	}
	
	public int getEventID() {
		return eventID;
	}

	public String toString() {
		String s = "(";
		if (executionTime != null) {
			s += Time.inMilliseconds(executionTime);  
		} else {
			s += "null";
		}
		s += ", " + eventID + ")";
		return s;
	}

}
