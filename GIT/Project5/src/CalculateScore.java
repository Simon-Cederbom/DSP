
public class CalculateScore {

	public static int calculate(int[] dices, String save) {

		save = save.toLowerCase();

		if (save.equals("ones")) {
			return saveNumber(dices, 1);
		}
		if (save.equals("twos")) {
			return saveNumber(dices, 2);
		}
		if (save.equals("threes")) {
			return saveNumber(dices, 3);
		}
		if (save.equals("fours")) {
			return saveNumber(dices, 4);
		}
		if (save.equals("fives")) {
			return saveNumber(dices, 5);
		}
		if (save.equals("sixes")) {
			return saveNumber(dices, 6);
		}
		if (save.equals("three of a kind")) {
			return threeOfKind(dices);
		}
		if (save.equals("four of a kind")) {
			return fourOfKind(dices);
		}
		if (save.equals("full house")) {
			return fullHouse(dices);
		}
		if (save.equals("small straight")) {
			return smallStraight(dices);
		}
		if (save.equals("large straight")) {
			return largeStraight(dices);
		}
		if (save.equals("chance")) {
			return chance(dices);
		}
		if (save.equals("yahtzee")) {
			return yahtzee(dices);
		}

		return 0;
	}

	private static int yahtzee(int[] dices) {
		for (int i = 1; i < dices.length; i++) {
			if (dices[i] != dices[i - 1]) {
				return 0;
			}
		}
		return 50;
	}

	private static int chance(int[] dices) {
		return dices[0] + dices[1] + dices[2] + dices[3] + dices[4] + dices[5];
	}

	private static int largeStraight(int[] dices) {
		boolean[] diceSum = { false, false, false, false, false, false };

		for (int i = 0; i < dices.length; i++) {
			diceSum[dices[i]] = true;
		}

		if ((diceSum[0] && diceSum[1] && diceSum[2] && diceSum[3] && diceSum[4]))
			return 40;
		if ((diceSum[1] && diceSum[2] && diceSum[3] && diceSum[4] && diceSum[5]))
			return 40;

		return 0;
	}

	private static int smallStraight(int[] dices) {
		boolean[] diceSum = { false, false, false, false, false, false };

		for (int i = 0; i < dices.length; i++) {
			diceSum[dices[i]] = true;
		}

		if ((diceSum[0] && diceSum[1] && diceSum[2] && diceSum[3]))
			return 30;
		if ((diceSum[1] && diceSum[2] && diceSum[3] && diceSum[4]))
			return 30;
		if ((diceSum[2] && diceSum[3] && diceSum[4] && diceSum[5]))
			return 30;

		return 0;
	}

	private static int fullHouse(int[] dices) {
		boolean twoSame = false;
		boolean threeSame = false;
		int[] diceAmount = new int[6];

		for (int i = 0; i < dices.length; i++) {
			diceAmount[dices[i]]++;
		}
		for (int i = 0; i < diceAmount.length; i++) {
			if (diceAmount[i] == 2)
				twoSame = true;
			if (diceAmount[i] == 3)
				threeSame = true;
		}
		if (twoSame && threeSame)
			return 25;

		return 0;
	}

	private static int fourOfKind(int[] dices) {
		int kind = -1;
		int[] amountDices = new int[6];
		for (int i = 0; i < amountDices.length; i++) {
			amountDices[dices[i]]++;
		}
		for (int i = 0; i < amountDices.length; i++) {
			if(amountDices[i] >= 4) {
				kind = amountDices[i];
				break;
			}
		}
		if(kind == -1) return 0;
		int sum = 0;
		int isFour = 0;
		for (int i = 0; i < dices.length; i++) {
			if (dices[i] == kind) {
				isFour++;
			}
			sum += dices[i];
		}
		if (isFour < 4)
			sum = 0;
		return sum;
	}

	private static int threeOfKind(int[] dices) {
		int kind = -1;
		int[] amountDices = new int[6];
		for (int i = 0; i < amountDices.length; i++) {
			amountDices[dices[i]]++;
		}
		for (int i = 0; i < amountDices.length; i++) {
			if(amountDices[i] >= 3) {
				kind = amountDices[i];
				break;
			}
		}
		if(kind == -1) return 0;
		int sum = 0;
		int isThree = 0;
		for (int i = 0; i < dices.length; i++) {
			if (dices[i] == kind) {
				isThree++;
			}
			sum += dices[i];
		}
		if (isThree < 3)
			sum = 0;
		return sum;
	}

	private static int saveNumber(int[] dices, int saveNumber) {
		int sum = 0;
		for (int i = 0; i < dices.length; i++) {
			if (dices[i] == saveNumber)
				sum += dices[i];
		}
		return sum;
	}

}
