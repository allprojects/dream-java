package javareact.chat;

import java.awt.Color;
import java.awt.EventQueue;
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
import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;

public class Chat extends JFrame {

	private static final long serialVersionUID = 390641070042167681L;
	private RemoteVar<String> remoteMessages;
	private Var<String> messages;
	private String userName;
	private JTextArea msgs;
	private JTextField sendText;
	private JList<String> statusList;

	public Chat(String username) throws Exception {
		Consts.hostName = username;
		messages = new Var<String>("message", "");
		remoteMessages = new RemoteVar<String>("message@*");

		// split Sender + Message into two Signals (sHost + sMessage)
		Signal<String> sHost = new Signal<String>("sHost", () -> {
			if (remoteMessages.get() == null)
				return "";
			else 
				return remoteMessages.get().split(":", 2)[0];
		}, remoteMessages);
		
		Signal<String> sMessage = new Signal<String>("sMessage", () -> {
			if (remoteMessages.get() == null)
				return "";
			else 
				return remoteMessages.get().split(":", 2)[1];
		}, remoteMessages);
		
		Signal<String> display = new Signal<String>("display", () -> {
			if (!sHost.get().equals(userName))
				return sMessage.get();
			else
				return "";				
		}, sHost, sMessage);
		
		display.change().addHandler((oldValue, newValue) -> {
			if (!sHost.get().equals(userName))
				displayMessage(sHost.get() + ": " + newValue);
		});
				
		
		this.userName = username;
		initUI();
	}

	private void initUI() {
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
					sendMessage();
			}
		});
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendMessage();
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

	protected void sendMessage() {
		messages.set(userName + ":" + sendText.getText());
		displayMessage("You: " + sendText.getText());
		sendText.setText("");
	}

	protected void displayMessage(String text) {
		if (msgs.getText().isEmpty())
			msgs.append(text);
		else
			msgs.append(System.lineSeparator() + text);
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1)
				System.out.println("username missing");

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					try {
						Chat chat = new Chat(args[0]);
						chat.setVisible(true);
					} catch (Exception e) {
					}

				}
			});
			// // read from command line
			// BufferedReader commandLine = new java.io.BufferedReader(
			// new InputStreamReader(System.in));
			//
			// // loop until the word "exit" is typed
			// while (true) {
			// String s = commandLine.readLine();
			// if (s.equalsIgnoreCase("exit")) {
			// System.exit(0);// exit program
			// } else
			// chat.writeMessage(s);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}