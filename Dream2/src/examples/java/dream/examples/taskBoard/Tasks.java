package dream.examples.taskBoard;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import dream.client.Var;
import dream.common.Consts;

/**
 * 
 * @author Min Yang
 * @date May 13, 2016
 * @description Creating tasks.
 */
public class Tasks extends JFrame implements WindowListener {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		new Tasks();
		// TODO
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

	@Override
	public void windowActivated(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent paramWindowEvent) {
		// TODO Auto-generated method stub

	}

}
