package protopeer.scenarios;

import java.lang.reflect.*;

import org.apache.log4j.*;

import protopeer.*;

/**
 * Represents a single scenario event, an event specifies the range of peer
 * indices going from <code> startPeerIndex </code> to
 * <code> endPeerIndex</code> on which a method will be executed at the
 * <code>executionTime</code>. The method to call is specified by the
 * <code>clazz</code> on which the call should happend and the
 * <code>method</code> method object.
 * 
 * 
 */

public class ScenarioEvent implements Comparable<ScenarioEvent> {

	private Logger logger = Logger.getLogger(Scenario.class);

	private Class<?> clazz;

	private Method methodToCall;

	private double executionTime = -1;

	private int startPeerIndex = -2;

	private int endPeerIndex = -2;

	private static int nextEventID = 0;

	private int eventID;

	public ScenarioEvent() {
		eventID = nextEventID++;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	void setEndPeerIndex(int endPeerIndex) {
		this.endPeerIndex = endPeerIndex;
	}

	void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}

	void setMethodToCall(Method methodToCall) {
		this.methodToCall = methodToCall;
	}

	void setStartPeerIndex(int startPeerIndex) {
		this.startPeerIndex = startPeerIndex;
	}

	public int getEndPeerIndex() {
		return endPeerIndex;
	}

	public double getExecutionTime() {
		return executionTime;
	}

	public Method getMethodToCall() {
		return methodToCall;
	}

	public int getStartPeerIndex() {
		return startPeerIndex;
	}

	private String getGeneralFailureString(Peer peer) {
		return "Scenario event \"" + toString() + "\" failed on peer index number " + peer.getIndexNumber();
	}

	/**
	 * Executes the <code>method</code> of the event on the <code>peer</code>
	 * specified as the argument. If the <code>clazz</code> of the event is
	 * <code>Peer.class</code> the method is executed directly on the peer,
	 * otherwise the peerlet of class <code>clazz</code> is located in the
	 * <code>peer</code> and then the method is executed on that peerlet.
	 * 
	 * @param peer
	 */
	public void executeMethodOnPeer(Peer peer) {
		try {
			methodToCall.setAccessible(true);
			if (clazz == Peer.class) {
				methodToCall.invoke(peer);
			} else {
				Object target = peer.getPeerletOfType(clazz);
				if (target == null) {
					logger.error(getGeneralFailureString(peer) + ": peerlet of type " + clazz.getCanonicalName()
							+ " not found");
					return;
				} else {
					methodToCall.invoke(target);
				}
			}
		} catch (Exception e) {
			logger.error(getGeneralFailureString(peer), e);
		}
	}

	public int compareTo(ScenarioEvent otherEvent) {
		int comparisonByTime = (int) (this.executionTime - otherEvent.executionTime);
		if (comparisonByTime == 0) {
			return this.eventID - otherEvent.eventID;
		}
		return comparisonByTime;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (startPeerIndex == -1) {
			buffer.append("*");
		} else if (startPeerIndex == endPeerIndex) {
			buffer.append(startPeerIndex);
		} else {
			buffer.append(startPeerIndex + "-" + endPeerIndex);
		}
		buffer.append("\t");
		buffer.append(executionTime);
		buffer.append("\t");
		if (clazz != null && methodToCall != null) {
			buffer.append(clazz.getCanonicalName() + "." + methodToCall.getName() + "()");
		} else {
			buffer.append("!null!");
		}

		return buffer.toString();
	}

	public boolean indexNumberMatches(int peerIndexNumber) {
		if (startPeerIndex == -1 && endPeerIndex == -1) {
			return true;
		}

		return startPeerIndex <= peerIndexNumber && peerIndexNumber <= endPeerIndex;
	}
}
