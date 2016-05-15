package dream.examples.taskBoard;

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

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		new Tasks();
		// TODO accept inputs of new tasks from Monitor
		/*
		 * javax.swing.SwingUtilities.invokeLater(new Runnable() { public void
		 * run() { Monitor user1 = new Monitor(); user1.initComponents(); if
		 * (Monitor.taskValid()) { new Tasks(); } } });
		 */
	}

	public Tasks() {
		int j = 0;
		while (j < 3) {
			Consts.hostName = "host" + j;
			System.out.println("In Tasks");
			Var<String> v = new Var<String>("toServerVar", "");
			try {
				int i = 0;
				while (i + j < 10) {
					Thread.sleep(1000);
					v.set("D" + i + "From host" + j + ":" + "T" + i + "From host" + j);
					i++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			j++;
		}
	}
}
