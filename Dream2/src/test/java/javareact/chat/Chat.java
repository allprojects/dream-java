package javareact.chat;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Var;

public class Chat extends JFrame implements ReactiveChangeListener<String> {

	private RemoteVar<String> remoteMessages;
	private Var<String> messages;
	private String userName;
	private JTextArea msgs;
	private JTextField sendText;
	public Chat(String username) throws Exception {
		Consts.hostName = username;
		messages = new Var<String>("message", "");
		remoteMessages = new RemoteVar<String>("message@*");
		remoteMessages.addReactiveChangeListener(this);
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
    	msgs = new JTextArea(5, 20);
    	msgs.setEditable(false);
    	JPanel panel = new JPanel();
    	panel.add(msgs);
	    panel.add(sendText);
	    panel.add(sendButton);
	    getContentPane().add(panel);  
	    
        setTitle("Chat - " + userName);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

	@Override
	public void notifyReactiveChanged(String newValue) {
		String[] msg = newValue.split(":", 2);
		if (!msg[0].equals(userName))
			displayMessage(msg[0] + ": " +msg[1]);
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
//			// read from command line
//			BufferedReader commandLine = new java.io.BufferedReader(
//					new InputStreamReader(System.in));
//
//			// loop until the word "exit" is typed
//			while (true) {
//				String s = commandLine.readLine();
//				if (s.equalsIgnoreCase("exit")) {
//					System.exit(0);// exit program
//				} else
//					chat.writeMessage(s);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}