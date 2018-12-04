import java.util.Scanner;

public class CommandSender extends Thread{
	SynchronisationClient synchClient = null;
	Scanner scanner;
	volatile boolean stop = false;
	
	public CommandSender(SynchronisationClient c, Scanner s) {
		synchClient = c;
		scanner = s;
	}
	
	public void requestStop() {
		scanner.close();
		stop = true;
	}
	
	public void run() {
		while(!stop) {
			synchClient.sendCommand(scanner.nextLine());
		}
	}

}
