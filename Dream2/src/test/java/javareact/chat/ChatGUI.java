package javareact.chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class ChatGUI extends JFrame {

	private static final long serialVersionUID = 4659984914364067514L;
	private JTextArea msgs;
	private JTextField sendText;
	private JList<String> statusList;

	private Chat listener;

	public ChatGUI(String userName) {
		initUI(userName);
	}

	public void setListener(Chat c) {
		listener = c;
	}

	public String getTypedText() {
		return sendText.getText();
	}

	public void resetTypedText() {
		sendText.setText("");
	}

	public void displayMessage(String text) {
		if (msgs.getText().isEmpty())
			msgs.append(text);
		else
			msgs.append(System.lineSeparator() + text);
	}

	private void initUI(String userName) {
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
					listener.sendMessage();
			}
		});
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				listener.sendMessage();
			}
		});
		msgs = new JTextArea(5, 27);
		msgs.setEditable(false);
		msgs.setMaximumSize(null);

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("Alice");
		listModel.addElement("Bob");
		statusList = new JList<String>(listModel);
		statusList.setEnabled(false);
		statusList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		statusList.setVisibleRowCount(-1);

		statusList.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 9019815674349211344L;
			private JLabel label = new JLabel();
			private Color textSelectionColor = Color.BLACK;
			private Color backgroundSelectionColor = Color.CYAN;
			private Color textNonSelectionColor = Color.BLACK;
			private Color backgroundNonSelectionColor = Color.WHITE;

			@Override
			public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {

				String name = (String) value;
				label.setIcon(createImageIcon("status-offline.png", "Offline"));
				label.setText(name);

				if (isSelected) {
					label.setBackground(backgroundSelectionColor);
					label.setForeground(textSelectionColor);
				} else {
					label.setBackground(backgroundNonSelectionColor);
					label.setForeground(textNonSelectionColor);
				}

				return label;
			};

		});
		SpringLayout layout = new SpringLayout();

		// put messages on (5,5)
		layout.putConstraint(SpringLayout.WEST, msgs, 5, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, msgs, 5, SpringLayout.NORTH, getContentPane());

		// put textfield below messages
		layout.putConstraint(SpringLayout.NORTH, sendText, 5, SpringLayout.SOUTH, msgs);
		layout.putConstraint(SpringLayout.WEST, sendText, 5, SpringLayout.WEST, getContentPane());

		// put button next to the textfield
		layout.putConstraint(SpringLayout.NORTH, sendButton, 5, SpringLayout.SOUTH, msgs);
		layout.putConstraint(SpringLayout.WEST, sendButton, 5, SpringLayout.EAST, sendText);

		// make the frame big enough to fit all in
		layout.putConstraint(SpringLayout.EAST, getContentPane(), 10, SpringLayout.EAST, statusList);
		layout.putConstraint(SpringLayout.SOUTH, getContentPane(), 10, SpringLayout.SOUTH, sendText);

		layout.putConstraint(SpringLayout.NORTH, statusList, 5, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.WEST, statusList, 15, SpringLayout.EAST, sendButton);

		getContentPane().setLayout(layout);

		getContentPane().add(msgs);
		getContentPane().add(sendText);
		getContentPane().add(sendButton);
		getContentPane().add(statusList);

		setTitle("Chat - " + userName);
		// setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pack();
		setVisible(true);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
