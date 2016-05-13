package dream.examples.taskBoard;

import dream.client.Var;
import dream.common.Consts;

public class Tasks {
	public static void main(String[] args) {
		new Tasks();
	}

	public Tasks() {
		Consts.hostName = "host1";
		Var<String> v = new Var<String>("toServerVar", "D1:T1");
		try {
			int i = 0;
			while (i < 10) {
				Thread.sleep(1000);
				v.set("D" + i + ":" + "T" + i);
				i++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
