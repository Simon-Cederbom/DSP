import java.io.*;
import java.net.*;
import java.util.Scanner;

public class YahtzeeClient {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;
	Receiver receiver;
	Sender sender;
	boolean read = false;

	public YahtzeeClient() {
		try {
			s = new Socket("localhost", YahtzeeServer.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			Scanner scanner = new Scanner(System.in);
			sender = new Sender(this, scanner);
			sender.start();
			receiver = new Receiver(this);
			receiver.start();
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	public void receive() {
		try {
			String message = in.readUTF();
			if (message.equals("read")) {
				read = true;
			} else {
				System.out.println(message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(String message) {
		try {
			if (read) {
				out.writeUTF(message);
				read = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		YahtzeeClient c = new YahtzeeClient();
		while (true) {

		}
//		while(c.sender.isAlive() || c.receiver.isAlive()) {
//			
//		}
//		try {
//			c.s.close();
//		} catch (IOException e) {
//			System.out.println("IO: " + e.getMessage());
//		}
	}
}
