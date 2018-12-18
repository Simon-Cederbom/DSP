import java.util.*;

public class GameRoom extends Thread {
	private List<Connection> players;
	private String name;
	private int readyCounter = 0;
	private int playerNumber = 0;
	private boolean gameRunning = false;
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
		playerNumber++;
		player.setPlayerName("Player" + playerNumber);
		players.add(player);
		player.send("Welcome " + player.getPlayerName() + " to " + name + "!");
		if (players.size() <= 1) {
			player.send("Waiting on more players...");
		} else if (!gameRunning) {
			player.send("Are you ready to start?");
		} else {
			player.send("Wait for the current game to finish...");
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

	public int generateDice() {
		Random random = new Random();
		return random.nextInt(6) + 1;
//		int[] dices = new int[5];
//		for (int i = 0; i < dices.length; i++) {
//			dices[i] = 
//		}
//		return dices;
	}

	private void sendToPlayers(String message) {
		for (Connection player : players) {
			player.send(message);
		}
	}

	private void updateScoreBoard() {
		String separator = "---------------";
		scoreboard = "\t\t";
		for (int i = 0; i < players.size(); i++) {
			scoreboard += players.get(i).getPlayerName() + " ";
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
		gameRunning = true;
		for (Connection player : players) {
			for (int i = 0; i < 16; i++) {
				player.setScore(i, -1);
			}
		}
		updateScoreBoard();
		for (int i = 0; i < players.size() * 13; i++) {
			for (Connection player : players) {
				boolean[] savedDices = { false, false, false, false, false };
				int[] dices = new int[5];
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < dices.length; k++) {
						if (!savedDices[k]) {
							dices[k] = generateDice();
						} else {
							savedDices[k] = false;
						}
					}
					String message = player.getPlayerName() + ":\nDice1: " + dices[0] + " Dice2: " + dices[1]
							+ " Dice3: " + dices[2] + " Dice4: " + dices[3] + " Dice5: " + dices[4] + "\nScoreboard:\n"
							+ scoreboard;
					sendToPlayers(message);
					String response = player.readUserInput();
					if (response.equals("save")) {
						break;
					} else if (response.contains(",")) {
						String[] dicesToSave = response.split(",");
						for (int k = 0; k < dicesToSave.length; k++) {
							savedDices[Integer.parseInt(dicesToSave[k]) - 1] = true;
						}
					} else if(!response.equals("0")){
						savedDices[Integer.parseInt(response) - 1] = true;
					}
				}
				player.send("What score do you want to set?");
				// calculate score
			}
		}
	}

	public void run() {
		while (true) {
			while (players.size() <= 1) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			players.get(0).send("Are you ready to start?");
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
}
