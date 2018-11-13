import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	public static final int PORT = 5678;
	private static List<Connection> connectionList = new ArrayList<Connection>();
	
	public void updateClients(String msg) {
		for (Connection connection : connectionList) {
			connection.send(msg);
		}
	}

	public static void main(String args[]) {
		try {
			ServerSocket listenSocket = new ServerSocket(PORT);
			Server s = new Server();
			while (true) {
				Connection connection = new Connection(listenSocket.accept(), s);
				connectionList.add(connection);
				connection.start();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		}
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	Server server;

	public Connection(Socket aClientSocket, Server s) {
		try {
			clientSocket = aClientSocket;
			server = s;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}
	
	public void send(String msg) {
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(true) {
			try { // an echo server
				//out.writeUTF(in.readUTF());
				server.updateClients(in.readUTF());
			} catch (EOFException e) {
				System.out.println("EOF:" + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO:" + e.getMessage());
			} 
		}
	}
}