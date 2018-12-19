import java.io.*;
import java.net.*;
import java.util.*;

public class YahtzeeServer {
	public static final int PORT = 5679;
	private List<GameRoom> gameRooms = new ArrayList<GameRoom>();
	private int[] highScore = new int[16];

	public List<GameRoom> getGameRooms() {
		return gameRooms;
	}

	public void addGameRoom(GameRoom gameRoom) {
		gameRooms.add(gameRoom);
	}

	public void setHighScore(int[] score) {
		highScore = score;
	}

	public int[] getHighScore() {
		return highScore;
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
	private DataInputStream in;
	private DataOutputStream out;
	private Socket clientSocket;
	private YahtzeeServer server;
	private GameRoom gameRoom;
	private String playerName;
	private boolean ready = false;
	private boolean stop = false;
	private int[] score = new int[16];
	String test = "";

	public Connection(Socket aClientSocket, YahtzeeServer s) {
		try {
			clientSocket = aClientSocket;
			clientSocket.setSoTimeout(120000);
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			server = s;
			List<GameRoom> gameRooms = server.getGameRooms();
			String message = "Available game rooms: \n";
			int i = 0;
			for (GameRoom room : gameRooms) {
				i++;
				message += i + ": " + room.getRoomName() + "\n";
			}
			if (i == 0) {
				message += "No game rooms available.\n";
			}
			message += "\nType + to add a new game room.";
			out.writeUTF(message);
			message = readUserInput();
			if (message.equals("+")) {
				GameRoom room = new GameRoom("gameRoom" + (gameRooms.size() + 1));
				room.addPlayer(this);
				server.addGameRoom(room);
				gameRoom = room;
				room.start();
			} else {
				for (GameRoom room : gameRooms) {
					if (room.getRoomName().equals("gameRoom" + message)) {
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
	
	public void setReady(boolean set) {
		ready = set;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String name) {
		playerName = name;
	}

	public int[] getScore() {
		return score;
	}

	public void setScore(int index, int newScore) {
		if (index == -1) {
			for (int i = 0; i < score.length; i++) {
				if (score[i] == -1) {
					score[i] = newScore;
					break;
				}
			}
			return;
		}
		score[index] = newScore;
		if (index < 6) {
			boolean firstSectionFilled = true;
			int sum = 0;
			for (int i = 0; i < 6; i++) {
				if (score[i] == -1) {
					firstSectionFilled = false;
					break;
				}
				sum += score[i];
			}
			if (firstSectionFilled) {
				score[6] = sum;
				if (sum >= 63) {
					score[7] = 35;
				}
			}
		}
		if (index == 15 && score[0] != -1) {
			int sum = score[6];
			for (int i = 8; i < score.length; i++) {
				sum += score[i];
			}
			score[index] = sum;
		}
	}

	public String readUserInput() {
		try {
			out.writeUTF("read");
			String response = in.readUTF();
			if(response.equals("blablabla")) {
				System.out.println("Hejsan svejsan");
			}
			if (response.equals("yes")) {
				gameRoom.playerReady(this);
				return "";
			} else {
				return response;
			}
		} catch (SocketTimeoutException ste) {
			return "timed out";
		} catch (EOFException eofe) {
			return "disconnected";
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

	public void setHighScore() {
		server.setHighScore(score);
	}

	public void run() {
		while (!stop) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!ready) {
				test = readUserInput();
			}
		}
	}
}
