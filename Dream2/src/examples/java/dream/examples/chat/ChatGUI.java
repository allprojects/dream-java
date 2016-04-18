package dream.examples.chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

public class ChatGUI extends JFrame implements WindowListener {

	private static final long serialVersionUID = 4659984914364067514L;
	private JTextArea msgs;
	private JTextField sendText;
	private JList<String> statusList;
	private DefaultListModel<String> listModel;

	private Chat listener;

	public ChatGUI(String userName) {
		this.addWindowListener(this);
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

	public void setOnline(List<String> online) {
		List<String> offlineList = new ArrayList<String>();
		for (int i = 0; i < listModel.size(); i++) {
			if (!online.contains(listModel.get(i)))
				offlineList.add(listModel.get(i));
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				listModel.clear();
				for (String e : online) {
					listModel.addElement(e);
				}
				for (String e : offlineList)
					listModel.addElement(e);
				statusList.setSelectionInterval(0, online.size() - 1);
			}
		});

	}

	public void removeOnline(String name) {
		listModel.removeElement(name);
	}

	private void sendText() {
		listener.sendMessage(getTypedText());
		this.resetTypedText();
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
					sendText();
			}
		});
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				sendText();
			}
		});
		msgs = new JTextArea(5, 27);
		msgs.setEditable(false);
		msgs.setMaximumSize(null);

		listModel = new DefaultListModel<String>();
		statusList = new JList<String>(listModel);
		statusList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		statusList.setEnabled(false);
		statusList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		statusList.setVisibleRowCount(-1);
		// statusList.setSelectionBackground(Color.GREEN);
		// statusList.setSelectionForeground(Color.BLACK);
		// statusList.setForeground(Color.BLACK);
		// statusList.setBackground(Color.WHITE);
		// statusList.setCellRenderer(new DefaultListCellRenderer());

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
				label.setText(name);

				if (isSelected) {
					label.setBackground(backgroundSelectionColor);
					label.setForeground(textSelectionColor);
					label.setIcon(createImageIcon("status-online.png", "Online"));
				} else {
					label.setBackground(backgroundNonSelectionColor);
					label.setForeground(textNonSelectionColor);
					label.setIcon(createImageIcon("status-offline.png", "Offline"));
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
		File img = new File("./src/resources/dream/examples/chat/" + path);
		try {
			java.net.URL imgURL = img.toURI().toURL();
			if (imgURL != null) {
				return new ImageIcon(imgURL, description);
			} else {
				System.err.println("Couldn't find file: " + path);
				return null;
			}
		} catch (MalformedURLException e) {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

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
		listener.sendMessage("/quit");
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}
}
