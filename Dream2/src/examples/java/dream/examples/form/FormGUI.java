package dream.examples.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FormGUI extends JFrame {

	private static final long serialVersionUID = 9205614575242281482L;
	private JTextField sendText;
	private FormClient listener;
	private JLabel display;

	public FormGUI(String name, String labelText) {
		initUI(labelText);
		setTitle(name);
	}

	private void initUI(String labelText) {
		sendText = new JTextField(20);
		sendText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					sendText();
			}
		});
		JButton sendButton = new JButton("Set");
		sendButton.addActionListener((e) -> sendText());

		display = new JLabel("");
		display.setPreferredSize(new Dimension(100, 30));
		display.setMinimumSize(new Dimension(100, 30));
		display.setOpaque(true);

		JLabel label = new JLabel(labelText + ": ");

		JPanel p = new JPanel();
		p.add(label);
		p.add(sendText);

		BorderLayout lay = new BorderLayout();
		getContentPane().setLayout(lay);
		getContentPane().add(p, BorderLayout.WEST);
		getContentPane().add(sendButton, BorderLayout.EAST);
		getContentPane().add(display, BorderLayout.NORTH);
		// setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	private void sendText() {
		listener.typedText(getTypedText());
	}

	public void setListener(FormClient c) {
		listener = c;
	}

	public String getTypedText() {
		return sendText.getText();
	}

	public void setText(String text) {
		display.setText(text);
	}

	public void setColor(Color bg) {
		display.setBackground(bg);
	}

}
