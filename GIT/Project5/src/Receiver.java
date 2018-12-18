
public class Receiver extends Thread{
	YahtzeeClient c = null;
	private volatile boolean stop = false;
	
	public Receiver(YahtzeeClient client) {
		c = client;
	}
	
	public void requestStop() {
		stop = true;
	}
	
	public void run() {		
		while(!stop) {
			c.receive();
		}
	}
}
