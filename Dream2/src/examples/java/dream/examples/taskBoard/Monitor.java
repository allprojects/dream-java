/*
 * Created by JFormDesigner on Fri May 20 23:52:44 CEST 2016
 */

package dream.examples.taskBoard;

import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Min Yang
 */
public class Monitor extends JPanel {
	public Monitor() {
		initComponents();
	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Monitor user = new Monitor();
				user.initComponents();
			}
		});

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Min Yang
		frame1 = new JFrame();
		textArea1 = new JTextArea();
		label1 = new JLabel();
		label2 = new JLabel();
		textArea2 = new JTextArea();

		// ======== frame1 ========
		{
			Container frame1ContentPane = frame1.getContentPane();

			// ---- label1 ----
			label1.setText("Current devs:");

			// ---- label2 ----
			label2.setText("Current tests:");

			GroupLayout frame1ContentPaneLayout = new GroupLayout(frame1ContentPane);
			frame1ContentPane.setLayout(frame1ContentPaneLayout);
			frame1ContentPaneLayout.setHorizontalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap()
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label1).addComponent(
									textArea2, GroupLayout.PREFERRED_SIZE, 570, GroupLayout.PREFERRED_SIZE))
							.addGap(30, 30, 30)
							.addGroup(frame1ContentPaneLayout.createParallelGroup().addComponent(label2).addComponent(
									textArea1, GroupLayout.PREFERRED_SIZE, 570, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(30, Short.MAX_VALUE)));
			frame1ContentPaneLayout.setVerticalGroup(frame1ContentPaneLayout.createParallelGroup()
					.addGroup(frame1ContentPaneLayout.createSequentialGroup().addContainerGap()
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label2).addComponent(label1))
							.addGap(13, 13, 13)
							.addGroup(frame1ContentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addComponent(textArea2, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
									.addComponent(textArea1, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
							.addContainerGap(15, Short.MAX_VALUE)));

			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
			frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame1.setVisible(true);
		}
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Min Yang
	private JFrame frame1;
	private JTextArea textArea1;
	private JLabel label1;
	private JLabel label2;
	private JTextArea textArea2;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
