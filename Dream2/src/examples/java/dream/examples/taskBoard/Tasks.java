package dream.examples.taskBoard;

import java.awt.EventQueue;
import java.util.Random;
import java.util.logging.Logger;

import dream.client.Var;
import dream.common.Consts;
import dream.examples.util.Client;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Creating tasks.
 */
public class Tasks extends Client {

	public Tasks(String input) {
		super("TaskNode" + new Random().nextInt(100));
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

	public static void main(String[] args) {
		if (args.length < 1) {
			Logger.getGlobal().severe("developer/tasks missing");
			return;
		}
		EventQueue.invokeLater(() -> new Tasks(args[0]));
	}
}
