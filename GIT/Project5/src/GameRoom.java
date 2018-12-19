import java.util.*;

public class GameRoom extends Thread {
	private List<Connection> players;
	private String name;
	private int readyCounter = 0;
	private int playerNumber = 0;
	private boolean gameRunning = false;
	private boolean waiting = false;
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

	public boolean gameRunning() {
		return gameRunning;
	}

	public void addPlayer(Connection player) {
		if(player.getReadOnly()) {
			players.add(player);
			return;
		}
		playerNumber++;
		player.setPlayerName("Player" + playerNumber);
		players.add(player);
		player.send("Welcome " + player.getPlayerName() + " to " + name + "!");
		if (players.size() <= 1) {
			player.send("Waiting on more players...");
		} else if (waiting) {
			player.send("Are you ready to start?");
			// player.readUserInput();
		} else if (gameRunning) {
			player.send("Wait for the current game to finish...");
		}
	}

	public void playerReady(Connection player) {
		player.setReady(true);
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
			if(!players.get(i).getPlaying() || players.get(i).getReadOnly()) {
				continue;
			}
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
			if(!player.getPlaying() || player.getReadOnly()) {
				continue;
			}
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
				player.setReady(false);
				player.setPlaying(true);
			}
		}
		updateScoreBoard();
		for (int i = 0; i < 13; i++) {
			for (Connection player : players) {
				if(!player.getPlaying() || player.getReadOnly()) {
					continue;
				}
				player.setReady(true);
				boolean[] savedDices = { false, false, false, false, false };
				int[] dices = new int[5];
				boolean skip = false;
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < dices.length; k++) {
						if (!savedDices[k]) {
							dices[k] = generateDice();
						} else {
							savedDices[k] = false;
						}
					}
					String message = player.getPlayerName() + ":\nDiceOne: " + dices[0] + " DiceTwo: " + dices[1]
							+ " DiceThree: " + dices[2] + " DiceFour: " + dices[3] + " DiceFive: " + dices[4]
							+ "\nScoreboard:\n" + scoreboard;
					sendToPlayers(message);
					if (j != 2) {
						boolean correctInput = false;
						boolean saveResult = false;
						while (!correctInput) {
							String response = player.readUserInput();
							if (response.equals("timed out") || response.equals("disconnected")) {
								player.setScore(-1, 0);
								updateScoreBoard();
								skip = true;
								break;
							}
							if (response.equals("save")) {
								saveResult = true;
							} else if (response.contains(",")) {
								String[] dicesToSave = response.split(",");
								for (int k = 0; k < dicesToSave.length; k++) {
									try {
										savedDices[Integer.parseInt(dicesToSave[k]) - 1] = true;
									} catch (Exception e) {
										continue;
									}
								}
							} else if (!response.equals("0")) {
								try {
									savedDices[Integer.parseInt(response) - 1] = true;
								} catch (Exception e) {
									continue;
								}
							}
							correctInput = true;
						}
						if(saveResult) {
							break;
						}
					}
				}
				if (skip) {
					continue;
				}
				player.send("What score do you want to set?");
				int score = -1;
				String scoreToSave = "";
				while(score == -1) {
					scoreToSave = player.readUserInput();
					if(scoreToSave.equals("timed out") || scoreToSave.equals("disconnected")) {
						break;
					}
					score = CalculateScore.calculate(dices, scoreToSave);
				}
				if (scoreToSave.equals("timed out") || scoreToSave.equals("disconnected")) {
					player.setScore(-1, 0);
				} else {
					for (int k = 0; k < scoreRows.length; k++) {
						if (scoreRows[k].toLowerCase().equals(scoreToSave.toLowerCase())) {
							player.setScore(k, score);
							break;
						}
					}
				}
				updateScoreBoard();
				player.setReady(false);
			}

		}
		Connection playerWithHighestScore = players.get(0);
		for (Connection player : players) {
			player.setScore(15, 0);
			if (player.getScore()[15] > playerWithHighestScore.getScore()[15]) {
				playerWithHighestScore = player;
			}
			player.setReady(false);
		}
		playerWithHighestScore.setHighScore();
		updateScoreBoard();
		sendToPlayers("Final score: \n" + scoreboard);
		gameRunning = false;
		readyCounter = 0;
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
			sendToPlayers("Are you ready to start?");
			waiting = true;
			while (readyCounter != players.size()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			waiting = false;
			startGame();
		}
	}
}
