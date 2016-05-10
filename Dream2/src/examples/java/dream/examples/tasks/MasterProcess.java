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

		Var<Task> myVar = new Var<Task>("TASK", new Task("Task1"));
		try {

			int i = 0;
			while (true) {
				Thread.sleep(2000);
				myVar.set(new Task("Task" + i));
				i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
