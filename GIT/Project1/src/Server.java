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

	public void updatePrivate(String msg, String name, String nameSender) {
		for (Connection connection : connectionList) {
			if (connection.name.equals(name) || connection.name.equals(nameSender)) {
				connection.send(msg);
			}
		}
	}

	public boolean checkName(String name) {
		boolean result = false;
		for (Connection connection : connectionList) {
			if (connection.name.equals(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void addConnection(Connection c) {
		connectionList.add(c);
	}

	public void removeConnection(Connection c) {
		connectionList.remove(c);
	}

	public void banUser(String name) {
		for (Connection connection : connectionList) {
			if (connection.name.equals(name)) {
				connection.ban();
				break;
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
				connection.start();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		} finally {
			if (listenSocket != null) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					System.out.println("IO: " + e.getMessage());
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
	String userType = "normal";
	boolean banned = false;
	private volatile boolean stop = false;

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
			if (!banned) {
				out.writeUTF(msg);
			}
		} catch (IOException e) {
			System.out.println("Send: " + e.getMessage());
		}
	}

	public void ban() {
		banned = true;
	}

	public void requestStop() {
		stop = true;
	}

	public void run() {
		while (!stop) {
			try {
				String message = in.readUTF();
				if (message.startsWith("-quit")) {
					requestStop();
					server.removeConnection(this);
					server.updateClients(name + " disconnected from chat.");
					out.writeUTF("-quit");
					continue;
				}
				if (!banned) {
					if (message.startsWith("-setName")) {
						String username = message.split(" ")[1];
						if (server.checkName(username)) {
							send("Error 777! Name is already taken");
						} else {
							name = username;
							server.addConnection(this);
							send("Welcome " + username + "!");
						}
					} else if (message.startsWith("@")) {
						server.updatePrivate(name + ": " + message.substring(message.indexOf(" ")),
								message.substring(1, message.indexOf(" ")), this.name);
					} else if (message.startsWith("-setPrivilege")) {
						String[] messageSplit = message.split(" ");
						if (messageSplit.length > 1) {
							userType = messageSplit[1];
							send("You are now a " + userType + " user!");
						}
					} else if (message.startsWith("-ban")) {
						if (userType.equals("super")) {
							String banName = message.split(" ")[1];
							server.updateClients(banName + " has been banned from the chat by " + name);
							server.banUser(banName);
						} else {
							send("You don't have permission to do that!");
						}
					} else {
						server.updateClients(name + ": " + message);
					}
				}
			} catch (EOFException e) {
				System.out.println("EOF:" + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO:" + e.getMessage());
			}
		}
	}
}