package dream.examples.taskBoard;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import dream.client.Var;
import dream.examples.util.Client;

/**
 * Interface to create new Tasks. May be started multiple times!
 * 
 * @author Min Yang
 * @author Tobias Becker
 */
public class TaskCreater extends Client {
	private Var<Task> var;

	public TaskCreater() {
		super("TaskCreater" + new Random().nextInt(1000));
		var = new Var<Task>("FromTaskNode", null);
		new TaskCreaterGUI(this);
	}

	public static void main(String[] args) {
		new TaskCreater();
	}

	public Logger getLogger() {
		return logger;
	}

	public void addTask(String toTasks) {
		var.set(new Task(toTasks));
	}
}

class TaskCreaterGUI {
	private JTextField textField1;
	private JFrame frame1;
	private JButton button1;
	private TaskCreater taskCreater;

	public TaskCreaterGUI(TaskCreater t) {
		this.taskCreater = t;
		initComponents();
	}

	void initComponents() {
		frame1 = new JFrame();
		frame1.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		textField1 = new JTextField();
		button1 = new JButton();
		button1.setActionCommand("ADD");

		// ======== frame1 ========
		{
			Container frame1ContentPane = frame1.getContentPane();

			// ---- button1 ----
			button1.setText("New Task");
			button1.addActionListener(new ButtonListener());

			GroupLayout frame1ContentPaneLayout = new GroupLayout(frame1ContentPane);
			frame1ContentPane.setLayout(frame1ContentPaneLayout);
			frame1ContentPaneLayout
					.setHorizontalGroup(frame1ContentPaneLayout.createParallelGroup()
							.addGroup(frame1ContentPaneLayout.createSequentialGroup().addGap(20, 20, 20)
									.addComponent(textField1, GroupLayout.PREFERRED_SIZE, 590,
											GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18).addComponent(button1).addContainerGap(34, Short.MAX_VALUE)));

			frame1ContentPaneLayout.setVerticalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addGap(19, 19, 19)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(button1).addComponent(textField1, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(24, Short.MAX_VALUE)));

			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame1.setVisible(true);
		}
	}

	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			if (paramActionEvent.getActionCommand() == "ADD") {
				String toTasks = textField1.getText();
				if (Task.isValid(toTasks)) {
					taskCreater.addTask(toTasks);
					textField1.setText("");
				} else {
					textField1.setText("");
					JOptionPane.showMessageDialog(null, "Please input the right pattern of task. (D<Int>:T<Int>)");
					taskCreater.getLogger().info("Wrong input pattern of tasks");
				}
			}
		}
	}
}
