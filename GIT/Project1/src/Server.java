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
	
	public void updatePrivate(String msg, String name) {
		for(Connection connection : connectionList) {
			if(connection.name.equals(name)) {
				connection.send(msg);
			}
		}
	}

	public static void main(String args[]) {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(PORT);
			Server s = new Server();
			while (true) {
				Connection connection = new Connection(listenSocket.accept(), s);
				connectionList.add(connection);
				connection.start();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		}finally {
			if(listenSocket != null) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	Server server;
	String name;

	public Connection(Socket aClientSocket, Server s) {
		try {
			clientSocket = aClientSocket;
			server = s;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			String command = in.readUTF();
			if(command.contains("-setName")) {
				name = command.split(" ")[1];
			}
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
				String message = in.readUTF();
				if(message.startsWith("@")) {
					server.updatePrivate(name + ": " + message.substring(message.indexOf(" ")),
							message.substring(1, message.indexOf(" ")));
				}
				else {
					server.updateClients(name + ": " + message);
				}
			} catch (EOFException e) {
				System.out.println("EOF:" + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO:" + e.getMessage());
			} 
		}
	}
}