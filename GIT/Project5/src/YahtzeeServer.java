import java.io.*;
import java.net.*;
import java.util.*;

public class YahtzeeServer {
	public static final int PORT = 5679;
	private List<GameRoom> gameRooms = new ArrayList<GameRoom>();
	private int[] highScore = new int[16];
	// private int[] highScore = { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
	// 10, 10, 10, 10 };

	public List<GameRoom> getGameRooms() {
		return gameRooms;
	}

	public void addGameRoom(GameRoom gameRoom) {
		gameRooms.add(gameRoom);
	}

	public void setHighScore(int[] score) {
		highScore = score;
	}

	public String getHighScore() {
		if (highScore[7] == -1) {
			highScore[7] = 0;
		}
		return "Ones\t\t" + highScore[0] + "\nTwos\t\t" + highScore[1] + "\nThrees\t\t" + highScore[2] + "\nFours\t\t"
				+ highScore[3] + "\nFives\t\t" + highScore[4] + "\nSixes\t\t" + highScore[5]
				+ "\n-----------------------\nSum\t\t" + highScore[6] + "\nBonus\t\t" + highScore[7]
				+ "\n-----------------------\nThree of a kind\t" + highScore[8] + "\nFour of a kind\t" + highScore[9]
				+ "\nFull House\t" + highScore[10] + "\nSmall Straight\t" + highScore[11] + "\nLarge Straight\t"
				+ highScore[12] + "\nChance\t\t" + highScore[13] + "\nYAHTZEE\t\t" + highScore[14]
				+ "\n-----------------------\nTotal\t\t" + highScore[15];
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
	private boolean readOnly = false;
	private boolean playing = false;
	private String userMessage = "";
	private boolean isRemoved = false;

	public Connection(Socket aClientSocket, YahtzeeServer s) {
		try {
			clientSocket = aClientSocket;
			clientSocket.setSoTimeout(120000);
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			server = s;

		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void showGameRooms() {
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
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean correctInput = false;
		while (!correctInput) {
			readUserInput();
			message = userMessage;
			//message = readUserInput();
			if (message.equals("+")) {
				GameRoom room = new GameRoom("gameRoom" + (gameRooms.size() + 1));
				room.addPlayer(this);
				server.addGameRoom(room);
				gameRoom = room;
				room.start();
				correctInput = true;
			} else {
				for (GameRoom room : gameRooms) {
					String[] messages = new String[1];
					if (message.contains(" ")) {
						messages = message.split(" ");
					} else {
						messages[0] = message;
					}
					if (room.getRoomName().equals("gameRoom" + messages[0])) {
						if (messages.length > 1 && messages[1].equals("read-only")) {
							readOnly = true;
						}
						room.addPlayer(this);
						gameRoom = room;
						correctInput = true;
					}
				}
			}
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
			if(score[7] != -1) {
				sum += score[7];
			}
			for (int i = 8; i < score.length; i++) {
				sum += score[i];
			}
			score[index] = sum;
		}
	}

	public void readUserInput() {
		try {
			int i = 0;
			out.writeUTF("read");
			String response = in.readUTF();
			if (response.equals("exit")) {
				gameRoom.removePlayer(this);
				showGameRooms();
			} else if (response.equals("HighScore")) {
				out.writeUTF(server.getHighScore());
			} else if (response.equals("yes") && !readOnly && playing) {
				gameRoom.playerReady(this);
			} else {
				userMessage = response;
				i += 1;
				return;
				//return response;
			}

//			if (response.equals("yes")) {
//				gameRoom.playerReady(this);
//				return "";
//			} else {
//				return response;
//			}
		} catch (SocketTimeoutException ste) {
			userMessage = "timed out";
			return;
			//return "timed out";
		} catch (EOFException eofe) {
			userMessage = "disconnected";
			return;
			//return "disconnected";
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		userMessage = "";
		//return "";
	}

	public void setHighScore() {
		server.setHighScore(score);
	}

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setPlaying(boolean isPlaying) {
		playing = isPlaying;
	}

	public boolean getPlaying() {
		return playing;
	}
	
	public String getUserMessage() {
		return userMessage;
	}
	
	public void setUserMessage(String message) {
		userMessage = message;
	}
	
	public void setRemoved(boolean removed) {
		isRemoved = removed;
	}
	
	public boolean getRemoved() {
		return isRemoved;
	}

	public void run() {
		showGameRooms();
		while (!stop) {
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if (!ready) {
				readUserInput();
//				String message = readUserInput();
//				if(message.equals("yes")) {
//					gameRoom.playerReady(this);
//				}
//			}
//			if (!ready) {

//			if (!ready && readUserInput().equals("yes")) {
//				gameRoom.playerReady(this);
//				// return "";
//			}

//				;
//			}
		}
	}
}
