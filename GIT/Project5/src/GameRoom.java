import java.util.*;

public class GameRoom extends Thread {
	private List<Connection> players;
	private String name;
	private int readyCounter = 0;

	public GameRoom(String roomName) {
		name = roomName;
		players = new ArrayList<Connection>();
	}

	public String getRoomName() {
		return name;
	}

	public void addPlayer(Connection player) {
		players.add(player);
		player.send("Welcome to " + name + "!");
		if (players.size() <= 1) {
			player.send("Waiting on more players...");
		}
	}

	public void playerReady(Connection player) {
		readyCounter++;
		if (readyCounter != players.size()) {
			player.send("Waiting for all to be ready...");
		}
	}

	public void removePlayer(Connection player) {
		players.remove(player);
	}

	public void startGame() {
		for (Connection player : players) {
			player.send("Game starts!");
		}
	}

	public void run() {
		while (players.size() <= 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (Connection player : players) {
			player.send("Are you ready to start?");
		}
		while (readyCounter != players.size()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		startGame();
	}
}
