
public class FileReceiver extends Thread{
	SynchronisationClient synchClient = null;
	
	public FileReceiver(SynchronisationClient c) {
		synchClient = c;
	}
	
	public void run() {
		while(true) {
			synchClient.receive();
		}
	}
	
}
