import java.util.*;

public class GameRoom extends Thread {
	private List<Connection> players;
	private String name;
	private int readyCounter = 0;
	private String scoreboard;
	private String[] scoreRows = { "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Sum", "Bonus",
			"Three of a kind", "Four of a kind", "Full House", "Small Straight", "Large Straight", "Chance", "YAHTZEE",
			"Total" };

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

	public int[] generateDices() {
		Random random = new Random();
		int[] dices = new int[5];
		for (int i = 0; i < dices.length; i++) {
			dices[i] = random.nextInt(6) + 1;
		}
		return dices;
	}

	private void sendToPlayers(String message) {
		for (Connection player : players) {
			player.send(message);
		}
	}

	private void updateScoreBoard() {
		String separator = "---------------";
		scoreboard = "\t\t";
		for (int i = 1; i <= players.size(); i++) {
			scoreboard += "Player" + i + " ";
			separator += "--------";
		}
		for (int i = 0; i < scoreRows.length; i++) {
			if (i == 6 || i == 8 || i == 15) {
				scoreboard += "\n" + separator + "\n" + scoreRows[i];
				setScoreRow(i);
			} else {
				scoreboard += "\n" + scoreRows[i];
				setScoreRow(i);
			}
		}
		scoreboard += "\n" + separator;
	}

	private void setScoreRow(int index) {
		if (!(index < 13 && index > 7)) {
			scoreboard += "\t";
		}
		for (Connection player : players) {
			scoreboard += "\t  ";
			int score = player.getScore()[index];
			if (score == -1) {
				scoreboard += "";
			} else {
				scoreboard += score;
			}
		}
	}

	public void startGame() {
		for (Connection player : players) {
			int[] score = new int[16];
			for (int i = 0; i < 16; i++) {
				score[i] = -1;
			}
			player.setScore(score);
		}
		updateScoreBoard();
		sendToPlayers(scoreboard);
//		for (int i = 0; i < players.size(); i++) {
//			int[] dices = generateDices();
//			String message = "Player" + (i + 1) + ":\n" + "Dice1: " + dices[0] + " Dice2: " + dices[1] + " Dice3: "
//					+ dices[2] + " Dice4: " + dices[3] + " Dice5: " + dices[4];
//			sendToPlayers(message + "\n\nScoreboard:\n" + scoreboard);
//		}
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
