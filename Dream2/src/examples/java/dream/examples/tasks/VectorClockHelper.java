/**
 * 
 */
package dream.examples.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Ram
 *
 */
public class VectorClockHelper implements Runnable {

	HashMap<String, Integer> localClock;
	String processId;
	private WorkerHelper helper;

	public VectorClockHelper(String processId, WorkerHelper helper) {
		this.processId = processId;
		this.helper = helper;
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

	java.util.List<Message> queue1 = Collections.synchronizedList(new ArrayList<Message>());
	java.util.List<Message> queue2 = Collections.synchronizedList(new ArrayList<Message>());

	synchronized void checkEvent(Message msg) {

		if (newEvent(msg) == Clock.NEW && msg.getId().equals("p1")) {
			updateClock(msg.getId(), msg.getClock().get(msg.getId()));
			helper.updateClock();
			queueEvent(msg, true);

		}
		if (newEvent(msg) == Clock.QUEUE && msg.getId().equals("p2")) {
			updateClock(msg.getId(), msg.getClock().get(msg.getId()));
			helper.updateClock();
			queueEvent(msg, false);
		}

	}

	private synchronized void queueEvent(Message msg, boolean newEvent) {

		if (newEvent) {
			helper.listner.updateTasks(
					"ENQUEUE to Q2 " + msg.getTask().getId() + " Message is from process " + msg.getId(), false);
			queue2.add(msg);
		} else {
			helper.listner.updateTasks(
					"ENQUEUE to Q1 " + msg.getTask().getId() + " Message is from process " + msg.getId(), false);
			queue1.add(msg);
		}
	}

	private synchronized void composeEvent(Message msg) {

	}

	void updateClock() {
		checkNull();
		int clock = localClock.get(processId);
		clock++;
		updateClock(processId, clock);

	}

	private void updateClock(String id, int clock) {

		localClock.put(id, clock);

	}

	private Clock newEvent(Message msg) {
		int flag = 0;
		checkNull();
		for (String key : localClock.keySet()) {
			if (msg.getClock().keySet().contains(key)) {
				if (localClock.get(key) <= msg.getClock().get(key) && !key.equals(msg.getId())) {
					return Clock.QUEUE;
				}
				if (localClock.get(key) >= msg.getClock().get(key) && !key.equals(msg.getId())) {
					flag = 1;
				}
			} else {
				return Clock.NEW;
			}
		}
		if (flag == 1 && localClock.keySet().contains(msg.getId())
				&& localClock.get(msg.getId()) < msg.getClock().get(msg.getId())) {
			return Clock.NEW;
		}
		return Clock.QUEUE;
	}

	@Override
	public void run() {
		while (true) {
			helper.updateClock();
			int flag = 0;
			Iterator<Message> iterator2 = queue2.iterator();
			while (iterator2.hasNext()) {
				Message msg = iterator2.next();
				Iterator<Message> iterator = queue1.iterator();
				while (iterator.hasNext()) {
					Message qMsg = iterator.next();
					if (msg.getId().equals("p1") && qMsg.getId().equals("p2")
							&& msg.getClock().get(msg.getId()).equals(qMsg.getClock().get(msg.getId()))) {
						helper.handleEvent(msg, qMsg);
						helper.listner.updateTasks("Event detected with timestamp :" + msg.getClock().get(msg.getId()),
								false);
						flag = 1;
						helper.listner.updateTasks("DQUEUE msg  :" + qMsg.getTask().getId(), false);
						iterator.remove();

					}

				}
				if (flag == 0) {
					queue1.add(msg);
					updateClock(msg.getId(), msg.getClock().get(msg.getId()));
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
