package prk;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LanMessengerServer {
	ArrayList<PrintWriter> outputStreams;
	String ip;
	JLabel ipLabel;

	public class CustomerService implements Runnable {
		BufferedReader reader;
		Socket socket;

		public CustomerService(Socket clientSocket) {
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("Readed: " + message);
					sendToAll(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new LanMessengerServer().run();
	}

	public void run() {
		try {
			ip = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(ip);
		ipLabel = new JLabel("Server IP: " + ip);
		outputStreams = new ArrayList<PrintWriter>();
		JFrame frame = new JFrame("Serwer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.add(ipLabel);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(400, 100);
		frame.setVisible(true);
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSock = new ServerSocket(5000);
			while (true) {
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				outputStreams.add(writer);
				Thread t = new Thread(new CustomerService(clientSocket));
				t.start();
				System.out.println("Connection established");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendToAll(String message) {
		Iterator<PrintWriter> it = outputStreams.iterator();
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}