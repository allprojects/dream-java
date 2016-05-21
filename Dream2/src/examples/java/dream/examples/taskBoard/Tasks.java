package dream.examples.taskBoard;

import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JFrame;

import dream.client.Var;
import dream.common.Consts;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Creating tasks.
 */
public class Tasks extends JFrame {
	Logger log = Logger.getLogger("Tasks");
	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	public Tasks(String input) {
		Random rm = new Random();
		Consts.hostName = "TaskNode" + rm.nextInt(100);
		System.out.println("HOST name:" + Consts.hostName);
		Var<String> v = new Var<String>("FromTaskNode", input);
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			v.set(input);
		}
	}
}
