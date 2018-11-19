import java.util.Scanner;

public class MsgSender extends Thread{
	Client c = null;
	Scanner scanner;
	private volatile boolean stop = false;
	
	public MsgSender(Client client, Scanner s) {
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
