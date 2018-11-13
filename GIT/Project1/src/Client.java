import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client extends Thread {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;

	public Client(String name) {
		try {
			s = new Socket("localhost", Server.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}
	
	public void send(String msg) {
		try {
			out.writeUTF("Message: " + msg);
		}catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}	
	}
	
	public void recive() {
		try {
			System.out.println(in.readUTF());
		}catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}	
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Client c = new Client("Kalle");
		MsgReceiver r = new MsgReceiver(c);
		r.start();
		MsgSender s = new MsgSender(c);
		s.start();
	}

}

