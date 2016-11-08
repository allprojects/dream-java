package dream.examples.form.complete_glitchfree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FormGUI extends JFrame {

	private static final long serialVersionUID = 9205614575242281482L;
	private JTextField[] sendText;
	private FormClient listener;
	private JLabel display;
	private String[] labelText;

	public FormGUI(String name, String... labelText) {
		setTitle(name);
		this.labelText = labelText;
		initUI();
	}

	private void initUI() {
		getContentPane().setLayout(new BorderLayout());
		JPanel all = new JPanel();
		all.setLayout(new GridLayout(0, 2));
		getContentPane().add(all, BorderLayout.CENTER);

		sendText = new JTextField[labelText.length];

		for (int i = 0; i < labelText.length; i++) {
			sendText[i] = new JTextField(20);
			sendText[i].addKeyListener(new MultiListener(i));
			JButton sendButton = new JButton("Set");
			sendButton.addActionListener(new MultiListener(i));

			JLabel label = new JLabel(labelText[i] + ": ");

			JPanel p = new JPanel();
			p.add(label);
			p.add(sendText[i]);

			all.add(p);
			all.add(sendButton);
		}

		display = new JLabel("");
		display.setPreferredSize(new Dimension(100, 30));
		display.setMinimumSize(new Dimension(100, 30));
		display.setOpaque(true);
		getContentPane().add(display, BorderLayout.NORTH);

		// setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	public void setInitValues(String... values) {
		for (int i = 0; i < values.length; i++) {
			if (sendText[i].getText() == null || sendText[i].getText().isEmpty()) {
				sendText[i].setText(values[i]);
			}
		}
	}

	private void sendText(int i) {
		listener.typedText(i, getTypedText(i));
	}

	public void setListener(FormClient c) {
		listener = c;
	}

	public String getTypedText(int i) {
		return sendText[i].getText();
	}

	public void setText(String text) {
		display.setText(text);
	}

	public void setColor(Color bg) {
		display.setBackground(bg);
	}

	class MultiListener implements KeyListener, ActionListener {
		private int i;

		public MultiListener(int i) {
			this.i = i;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				sendText(i);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			sendText(i);
		}
	}
}
