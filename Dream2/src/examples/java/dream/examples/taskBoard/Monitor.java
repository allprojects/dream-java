package dream.examples.taskBoard;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

public class Monitor {
	public static JTextField textField1;
	static boolean flag = false;
	static Logger log = Logger.getLogger("Monitor");

	static boolean taskValid() {
		return flag;
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Monitor().initComponents();
			}
		});
	}

	void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Min Yang
		frame1 = new JFrame();
		textField1 = new JTextField();
		button1 = new JButton();
		textArea2 = new JTextArea();
		label1 = new JLabel();
		label2 = new JLabel();
		textArea3 = new JTextArea();

		// ======== frame1 ========
		{
			Container frame1ContentPane = frame1.getContentPane();

			// ---- button1 ----
			button1.setText("Add Task");
			button1.addActionListener(new ButtonListener());
			button1.setActionCommand("ADD");

			// ---- label1 ----
			label1.setText("Current devs:");

			// ---- label2 ----
			label2.setText("Current tests:");

			GroupLayout frame1ContentPaneLayout = new GroupLayout(frame1ContentPane);
			frame1ContentPane.setLayout(frame1ContentPaneLayout);
			frame1ContentPaneLayout.setHorizontalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addGroup(frame1ContentPaneLayout
							.createParallelGroup()
							.addGroup(frame1ContentPaneLayout.createSequentialGroup().addGap(20, 20, 20).addComponent(
									textField1, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
							.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap().addComponent(
									textArea3, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
							.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap()
									.addComponent(label1)))
							.addGap(18, 18, 18)
							.addGroup(frame1ContentPaneLayout.createParallelGroup()
									.addComponent(textArea2, GroupLayout.PREFERRED_SIZE, 300,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(button1).addComponent(label2))
							.addContainerGap(30, Short.MAX_VALUE)));
			frame1ContentPaneLayout.setVerticalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addGap(19, 19, 19)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(button1).addComponent(textField1, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label2).addComponent(label1))
							.addGap(13, 13, 13)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(textArea2, GroupLayout.PREFERRED_SIZE, 300,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(textArea3, GroupLayout.PREFERRED_SIZE, 300,
											GroupLayout.PREFERRED_SIZE))
							.addContainerGap()));
			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame1.setVisible(true);
		}
		// //GEN-END:initComponents

	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Min Yang
	private JFrame frame1;
	private JButton button1;
	private JTextArea textArea2;
	private JLabel label1;
	private JLabel label2;
	private JTextArea textArea3;

	// JFormDesigner - End of variables declaration //GEN-END:variables
	static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			if (paramActionEvent.getActionCommand() == "ADD") {
				String toTasks = textField1.getText();
				// TODO task format valid
				if (toTasks.contains(":") && toTasks.split(":")[0].matches("D\\d*")
						&& toTasks.split(":")[1].matches("T\\d*")) {
					flag = true;
				} else {
					log.info("Wrong input pattern of tasks");
				}
				flag = true;
			}
		}
	}
}
