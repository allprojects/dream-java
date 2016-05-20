package dream.examples.taskBoard;

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
		Consts.hostName = "host";
		Var<String> v = new Var<String>("toServerVar", input);
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			v.set(input);
		}
	}
}
