
public class MsgReceiver extends Thread{
	Client c = null;
	private volatile boolean stop = false;
	
	public MsgReceiver(Client client) {
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
