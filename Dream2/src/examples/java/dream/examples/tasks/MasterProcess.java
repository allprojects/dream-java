/**
 * 
 */
package dream.examples.tasks;

import dream.client.Var;
import dream.common.Consts;

/**
 * @author Ram
 *
 */
public class MasterProcess {

	private void init() {
		Consts.hostName = "Host1";
		int clock = 0;
		Var<Message> initTask = new Var<Message>("TASK", null);
		Var<Message> initTask1 = new Var<Message>("TASK1", null);
		try {

			int i = 0;
			while (true) {
				Message m = new Message();
				Thread.sleep(5000);
				Task t = new Task("Task" + i);
				t.setId(1000 + i);
				m.setTask(t);
				clock++;
				m.setClock("@p1:" + clock);
				initTask.set(m);

				t.setClock(clock);
				t.setDescription("This is " + i + "th task");

				initTask1.set(m);
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
