import java.util.Scanner;

public class MsgSender extends Thread{
	Client c = null;
	Scanner scanner;
	
	public MsgSender(Client client, Scanner s) {
		c = client;
		scanner = s;
	}
	
	public void run() {
		while(true) {
			c.send(scanner.nextLine());
		}
	}
}
