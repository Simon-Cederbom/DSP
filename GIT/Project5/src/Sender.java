import java.util.Scanner;

public class Sender extends Thread{
	YahtzeeClient c = null;
	Scanner scanner;
	private volatile boolean stop = false;
	
	public Sender(YahtzeeClient client, Scanner s) {
		c = client;
		scanner = s;
	}
	
	public void requestStop() {
		scanner.close();
		stop = true;
	}
	
	public void run() {
		while(!stop) {
			c.send(scanner.nextLine());
		}
	}
}
