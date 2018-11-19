import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;
	boolean initialized = false;
	Scanner scanner = new Scanner(System.in);
	MsgReceiver r;
	MsgSender sender;

	public Client() {
		try {
			s = new Socket("localhost", Server.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			System.out.print("Welcome! What is your name?\t");
			send("-setName " + scanner.nextLine());
			boolean accepted = false;
			while (!accepted) {
				String message = in.readUTF();
				System.out.println(message);
				if (message.startsWith("Error 777")) {
					System.out.println("Enter new name: ");
					send("-setName " + scanner.nextLine());
				} else {
					accepted = true;
				}
			}
			r = new MsgReceiver(this);
			r.start();
			sender = new MsgSender(this, this.scanner);
			sender.start();
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	// Start message with @ followed by name to write a private message
	public void send(String msg) {
		try {
			out.writeUTF(msg);
			if (msg.startsWith("-quit")) {
				quit();
			}
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	public void receive() {
		try {
			String message = in.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public void quit() {
		r.stop();
		sender.stop();
	}

	public static void main(String[] args) {
		Client c = new Client();
		while (c.r.isAlive() && c.sender.isAlive()) {

		}
		try {
			c.s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
