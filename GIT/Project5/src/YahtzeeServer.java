import java.io.*;
import java.net.*;
import java.util.*;

public class YahtzeeServer {
	public static final int PORT = 5679;
	List<GameRoom> gameRooms = new ArrayList<GameRoom>();

	public List<GameRoom> getGameRooms(){
		return gameRooms;
	}
	
	public static void main(String[] args) {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(PORT);
			while (true) {
				YahtzeeServer s = new YahtzeeServer();
				Connection connection = new Connection(listenSocket.accept(), s);
				connection.start();
			}
		} catch (IOException ioe) {
			System.out.println("IO: " + ioe.getMessage());
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
	YahtzeeServer server;

	public Connection(Socket aClientSocket, YahtzeeServer s) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			server = s;
			List<GameRoom> gameRooms = server.getGameRooms();
			String message = "Available game rooms: \n";
			int i = 0;
			for(GameRoom room : gameRooms) {
				i++;
				message += room.GetName() + i + "\n";
			}
			if(i == 0) {
				message += "No game rooms available.";
			}
			message += "\nType + to add a new game room.";
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}
}
