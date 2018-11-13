import java.util.Scanner;

public class MsgSender extends Thread{
	Client c = null;	
	public MsgSender(Client client) {
		c = client;
	}
	
	public void run() {		
		Scanner scanner = new Scanner(System.in);
		while(true) {
			c.send(scanner.nextLine());
		}
	}
}
