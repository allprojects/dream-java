/*
 * Created by JFormDesigner on Fri May 20 23:52:44 CEST 2016
 */

package dream.examples.taskBoard;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

/**
 * @author Min Yang
 */
public class Monitor {

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Monitor user = new Monitor();
				user.initComponents();
			}
		});

	}

	private void button1ActionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "SHOW") {
			TaskReviewer view = new TaskReviewer();
		} else {
			JOptionPane.showMessageDialog(null, "Ops, currently no task, please try again later.");
			log.info("Wrong input pattern of tasks");
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Ashleeeeee Yang
		frame1 = new JFrame();
		textArea1 = new JTextArea();
		label1 = new JLabel();
		label2 = new JLabel();
		textArea2 = new JTextArea();
		button1 = new JButton();

		// ======== frame1 ========
		{
			Container frame1ContentPane = frame1.getContentPane();
			// ---- label1 ----
			label1.setText("Current devs:");

			// ---- label2 ----
			label2.setText("Current tests:");

			// ---- button1 ----
			button1.setText("Show me tasks");
			button1.addActionListener(e -> button1ActionPerformed(e));
			button1.setActionCommand("SHOW");

			GroupLayout frame1ContentPaneLayout = new GroupLayout(frame1ContentPane);
			frame1ContentPane.setLayout(frame1ContentPaneLayout);
			frame1ContentPaneLayout.setHorizontalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap()
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label1).addComponent(
									textArea2, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
							.addGap(30, 30, 30)
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label2).addComponent(
									textArea1, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
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
									.addComponent(textArea2, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
									.addComponent(textArea1, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame1.setVisible(true);
		}
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Ashleeeeee Yang
	private JFrame frame1;
	private static JTextArea textArea1;
	private JLabel label1;
	private JLabel label2;
	private static JTextArea textArea2;
	private JButton button1;
	private Logger log = Logger.getLogger("Monitor");
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
