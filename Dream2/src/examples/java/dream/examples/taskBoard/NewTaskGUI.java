package dream.examples.taskBoard;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * 
 * @author Min Yang
 * @date May 15, 2016
 * @description
 */
public class NewTaskGUI {
	public static JTextField textField1;
	static Logger log = Logger.getLogger("Monitor");

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NewTaskGUI user = new NewTaskGUI();
				user.initComponents();

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
		// //GEN-END:initComponents

	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Min Yang
	private JFrame frame1;
	private JButton button1;

	// JFormDesigner - End of variables declaration //GEN-END:variables
	static class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			if (paramActionEvent.getActionCommand() == "ADD") {
				String toTasks = textField1.getText();
				if (toTasks.contains(":") && toTasks.split(":")[0].matches("D\\d*")
						&& toTasks.split(":")[1].matches("T\\d*")) {
					// TODO: think! to make it more user friendly
					new Tasks(toTasks);
				} else {
					textField1.setText("");
					JOptionPane.showMessageDialog(null, "Please input the right pattern of task.");
					log.info("Wrong input pattern of tasks");
				}
			}
		}
	}
}
