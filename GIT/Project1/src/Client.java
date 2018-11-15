import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client extends Thread {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;
	String name;

	public Client(String name) {
		try {
			this.name = name;
			s = new Socket("localhost", Server.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF("-setName " + name);
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}
	
	//Start message with @ followed by name to write a private message 
	public void send(String msg) {
		try {
			out.writeUTF(msg);
		}catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}	
	}
	
	public void receive() {
		try {
			System.out.println(in.readUTF());
		}catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}	
	}
	
	public static void main(String[] args) {
		System.out.print("Welcome! What is your name?\t");
		Scanner scanner = new Scanner(System.in);
		Client c = new Client(scanner.nextLine());
		//System.out.println("Okidoki " + c.name +", if you say so.\nType a message and press enter to send.");
		MsgReceiver r = new MsgReceiver(c);
		r.start();
		MsgSender sender = new MsgSender(c);
		sender.start();
	}

}

