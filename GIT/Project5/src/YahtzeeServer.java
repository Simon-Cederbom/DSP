import java.io.*;
import java.net.*;
import java.util.*;

public class YahtzeeServer {
	public static final int PORT = 5679;
	List<GameRoom> gameRooms = new ArrayList<GameRoom>();

	public List<GameRoom> getGameRooms(){
		return gameRooms;
	}
	
	public void addGameRoom(GameRoom gameRoom) {
		gameRooms.add(gameRoom);
	}
	
	public static void main(String[] args) {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(PORT);
			YahtzeeServer s = new YahtzeeServer();
			while (true) {
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
	GameRoom gameRoom;
	boolean ready = false;
	boolean stop = false;

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
				message += i + ": " + room.getRoomName() + "\n";
			}
			if(i == 0) {
				message += "No game rooms available.\n";
			}
			message += "\nType + to add a new game room.";
			out.writeUTF(message);
			message = in.readUTF();
			if(message.equals("+")) {
				GameRoom room = new GameRoom("gameRoom" + (gameRooms.size() + 1));
				room.addPlayer(this);
				server.addGameRoom(room);
				gameRoom = room;
				room.start();
			}
			else {
				for(GameRoom room : gameRooms) {
					if(room.getRoomName().equals("gameRoom" + message)) {
						room.addPlayer(this);
						gameRoom = room;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}
	
	public void send(String message) {
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetReady() {
		ready = false;
	}
	
	public void run() {
		while(!stop) {
			try {
				String message = in.readUTF();
				if(message.equals("yes")) {
					ready = true;
					gameRoom.playerReady(this);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}