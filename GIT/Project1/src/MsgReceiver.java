
public class MsgReceiver extends Thread{
	Client c = null;	
	public MsgReceiver(Client client) {
		c = client;
	}
	
	public void run() {		
		while(true) {
			c.receive();
		}
	}
}
