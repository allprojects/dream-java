/**
 * 
 */
package dream.examples.util;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Ram
 *
 */
public class VectorClock {

	HashMap<String, Integer> localClock;
	String processId;

	public VectorClock(String processId) {
		this.processId = processId;
	}

	/**
	 * @return the localClock
	 */
	public HashMap<String, Integer> getLocalClock() {
		checkNull();
		return localClock;
	}

	void checkNull() {
		if (localClock == null) {
			localClock = new HashMap<>();
			localClock.put(processId, 0);
		}
	}

	/**
	 * Method to be used to compare two clock vectors, true will be returned if
	 * both clocks have same keyset and have same values for individual keys.
	 * Usually used to compare if the arrived message is older than the local
	 * state.
	 * 
	 * @param localClock
	 * @param messageClock
	 * @return
	 */
	public Clock compareClock(HashMap<String, Integer> messageClock) {
		checkNull();
		if (localClock.keySet().containsAll(messageClock.keySet())
				&& localClock.keySet().size() == messageClock.keySet().size()) {
			for (String key : localClock.keySet()) {
				if (!localClock.get(key).equals(messageClock.get(key)) && localClock.get(key) > messageClock.get(key)) {
					return Clock.OLD;
				}
				if (!localClock.get(key).equals(messageClock.get(key)) && localClock.get(key) < messageClock.get(key)) {
					return Clock.NEW;
				}
			}
			return Clock.EQUAL;

		} else if (localClock.keySet().containsAll(messageClock.keySet())
				&& localClock.keySet().size() > messageClock.keySet().size()) {
			for (String key : messageClock.keySet()) {
				if (!localClock.get(key).equals(messageClock.get(key)) && localClock.get(key) > messageClock.get(key)) {
					return Clock.NEW;
				}
				if (!localClock.get(key).equals(messageClock.get(key)) && localClock.get(key) < messageClock.get(key)) {
					return Clock.OLD;
				}
			}
			return Clock.EQUAL;

		}

		return null;

	}

	public boolean isNew(HashMap<String, Integer> messageClock) {
		checkNull();
		Set<String> localKeyList = localClock.keySet();
		for (String key : messageClock.keySet()) {

			if (localKeyList.contains(key)) {
				if (messageClock.get(key) > localClock.get(key)) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method invoked by local process to update the clock after execution of an
	 * instruction which has global effect Ex: Sending of message or receiving
	 * of message. processId should be provided as an input key and local clock
	 * vector is provided as localClock.
	 * 
	 * @param localClock
	 * @param key
	 * @return
	 */

	public HashMap<String, Integer> updateClock() {
		checkNull();
		localClock.put(processId, localClock.get(processId) + 1);
		return localClock;

	}

	/**
	 * @param localClock
	 * @param messageClock
	 * @return
	 */
	public void updateClock(HashMap<String, Integer> messageClock) {
		checkNull();
		Set<String> localKeyList = localClock.keySet();
		for (String key : messageClock.keySet()) {
			if (localKeyList.contains(key)) {
				if (messageClock.get(key) > localClock.get(key)) {
					localClock.put(key, messageClock.get(key));
				}
			} else {
				localClock.put(key, messageClock.get(key));
			}
		}
	}

	public Integer getTimeStamp() {
		checkNull();
		return localClock.get(processId);
	}
}
