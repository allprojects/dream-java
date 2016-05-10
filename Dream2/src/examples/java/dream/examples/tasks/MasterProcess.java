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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Consts.hostName = "Host1";

		Var<Task> myVar = new Var<Task>("TASK_", new Task("Task1"));
		try {

			int i = 0;
			while (true) {
				myVar.set(new Task("Task" + i));
				Thread.sleep(2000);
				i++;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
