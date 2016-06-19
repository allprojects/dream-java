/**
 * 
 */
package dream.examples.tasks;

import dream.client.Var;
import dream.common.Consts;
import dream.examples.util.VectorClock;

/**
 * @author Ram
 *
 */
public class MasterProcess {

	private void init() {
		Consts.hostName = "Host1";

		VectorClock vectorClock = new VectorClock("p1");
		Var<Message> initTask = new Var<Message>("TASK", null);
		Var<Message> initTask2 = new Var<Message>("TASK2", null);
		try {

			int i = 1;
			while (true) {
				// Create a message to be distributed
				Message message = new Message();
				message.setId("p1");
				Thread.sleep(5000);

				// Add task to a message
				Task task = new Task("Task" + i);

				// Set id for the task
				task.setId(1000 + i);

				message.setTask(task);
				// increment local clock
				vectorClock.updateClock();

				// set clock for the task
				message.setClock(vectorClock.getLocalClock());

				// Add description to task
				task.setDescription("This is " + i + "th task");

				// Send task to Deligator
				initTask.set(message);

				// link latency
				Thread.sleep(5000);
				// Send task to worker
				initTask2.set(message);
				i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MasterProcess().init();
	}

}
