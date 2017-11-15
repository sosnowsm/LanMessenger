package prk;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LanMessengerClient {
	JTextArea incomingMessages;
	JTextField message;
	JLabel nickLabel;
	JTextField nick;
	JLabel ipLabel;
	BufferedReader reader;
	PrintWriter writer;
	Socket socket;
	String ip;
	String ipSerwera;

	public static void main(String[] args) {
		LanMessengerClient client = new LanMessengerClient();
		client.run();
	}

	public void run() {
		getLocalIP();
		makeGUI();
		getServerIP();
		setNick();
		configureCommunication();
		Thread recipientThread = new Thread(new CommunicatsRecipient());
		recipientThread.start();
	}

	private void getLocalIP() {
		try {
			ip = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void makeGUI() {
		ipLabel = new JLabel(ip);
		JFrame ramka = new JFrame("LAN Messenger Client");
		ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		incomingMessages = new JTextArea(15, 30);
		incomingMessages.setLineWrap(true);
		incomingMessages.setWrapStyleWord(true);
		incomingMessages.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(incomingMessages);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		nickLabel = new JLabel("Nick: ");
		nick = new JTextField(20);
		message = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(ipLabel);
		mainPanel.add(scrollPane);
		mainPanel.add(nickLabel);
		mainPanel.add(nick);
		mainPanel.add(message);
		mainPanel.add(sendButton);
		ramka.getContentPane().add(BorderLayout.CENTER, mainPanel);
		ramka.setSize(400, 500);
		ramka.setVisible(true);
	}

	private void configureCommunication() {

		try {
			socket = new Socket(ipSerwera, 5000);
			InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(inputStreamReader);
			writer = new PrintWriter(socket.getOutputStream());
			System.out.println("Communication Configured");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void getServerIP() {
		ipSerwera = JOptionPane.showInputDialog("Enter Server IP ", "127.0.0.1");

	}

	private void setNick() {
		nick.setText(JOptionPane.showInputDialog("Enter nick"));
	}

	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				writer.println(nick.getText() + ": " + message.getText());
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			message.setText("");
			message.requestFocus();
		}
	}

	public class CommunicatsRecipient implements Runnable {
		public void run() {
			String message2;
			try {
				while ((message2 = reader.readLine()) != null) {
					System.out.println("Readed: " + message2);
					incomingMessages.append(message2 + "\n");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}