package dream.examples.scrumBoard;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.examples.util.Client;

/**
 * Displays both lists, the developers and the tasks.
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class Monitor extends Client {

	public static final String NAME = "Monitor";

	private final MonitorGUI gui;
	private final RemoteVar<String> devs;
	private final RemoteVar<String> tasks;
	private final Signal<String> sigDevs;
	private final Signal<String> sigTasks;

	@Override
	protected List<String> waitForVars() {
		return Arrays.asList(toVar(Server.NAME, Server.VAR_developers), toVar(Server.NAME, Server.VAR_tasks));
	}

	public Monitor() {
		super(NAME);
		gui = new MonitorGUI(this);

		devs = new RemoteVar<String>(Server.NAME, Server.VAR_developers);
		sigDevs = new Signal<String>("sigDevs", () -> {
			return devs.get();
		}, devs);

		tasks = new RemoteVar<String>(Server.NAME, Server.VAR_tasks);
		sigTasks = new Signal<String>("sigTests", () -> {
			return tasks.get();
		}, tasks);

		sigDevs.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal devs:" + newVal);
			// gui.setDevs(newVal);
		});

		sigTasks.change().addHandler((oldVa, newVal) -> {
			System.out.println("newVal tasks:" + newVal);
			// gui.setTasks(newVal);
		});
	}

	public static void main(String[] args) {
		new Monitor();
	}

	public void clickButton1() {
		readLock(toVar(Server.NAME, Server.VAR_tasks), toVar(Server.NAME, Server.VAR_developers));
		gui.setTasks(tasks.get());
		gui.setDevs(devs.get());
		unlock();
	}
}

class MonitorGUI {
	private JFrame frame1;
	private JTextArea textAreaTasks;
	private JLabel label1;
	private JLabel label2;
	private JTextArea textAreaDevs;
	private JButton button1;
	private Monitor monitor;

	public MonitorGUI(Monitor m) {
		this.monitor = m;
		frame1 = new JFrame();
		label1 = new JLabel();
		label2 = new JLabel();
		button1 = new JButton();
		textAreaTasks = new JTextArea();
		textAreaDevs = new JTextArea();
		textAreaDevs.setEditable(false);
		textAreaTasks.setEditable(false);

		// ======== frame1 ========
		{
			Container frame1ContentPane = frame1.getContentPane();
			// ---- label1 ----
			label1.setText("Current devs:");

			// ---- label2 ----
			label2.setText("Current tasks:");

			// ---- button1 ----
			button1.setText("Show me tasks");
			button1.addActionListener(e -> button1ActionPerformed(e));

			GroupLayout frame1ContentPaneLayout = new GroupLayout(frame1ContentPane);
			frame1ContentPane.setLayout(frame1ContentPaneLayout);
			frame1ContentPaneLayout.setHorizontalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap()
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label1).addComponent(
									textAreaDevs, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
							.addGap(30, 30, 30)
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label2).addComponent(
									textAreaTasks, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(9, Short.MAX_VALUE))
					.addGroup(GroupLayout.Alignment.TRAILING, frame1ContentPaneLayout.createSequentialGroup()
							.addContainerGap(11, Short.MAX_VALUE).addComponent(button1).addGap(71, 71, 71)));
			frame1ContentPaneLayout.setVerticalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addComponent(button1)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label2).addComponent(label1))
							.addGap(13, 13, 13)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(textAreaDevs, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
									.addComponent(textAreaTasks, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame1.setVisible(true);
		}
	}

	private void button1ActionPerformed(ActionEvent e) {
		monitor.clickButton1();
	}

	public void setDevs(String value) {
		textAreaDevs.setText(value.replace(":", "\n"));
	}

	public void setTasks(String value) {
		textAreaTasks.setText(value.replace(":", "\n"));
	}
}