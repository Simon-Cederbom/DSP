
public class FileReceiver extends Thread{
	SynchronisationClient synchClient = null;
	boolean stop = false;
	
	public FileReceiver(SynchronisationClient c) {
		synchClient = c;
	}
	
	public void requestStop() {
		stop = true;
	}
	
	public void run() {
		while(!stop) {
			synchClient.receive();
		}
	}
	
}
