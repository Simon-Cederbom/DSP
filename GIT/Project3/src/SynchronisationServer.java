import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class SynchronisationServer {
	public static final int PORT = 6789;

	public static void main(String[] args) {
		ServerSocket listenSocket = null;
		try {
			listenSocket = new ServerSocket(PORT);
			while (true) {
				Connection connection = new Connection(listenSocket.accept());
				connection.start();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		} finally {
			if (listenSocket != null) {
				try {
					listenSocket.close();
				} catch (IOException e) {
					System.out.println("IO: " + e.getMessage());
				}
			}
		}
	}
}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	ArrayList<File> directory = new ArrayList<File>();

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		FileOutputStream fileOutStream = null;
		try {
			int numberOfFiles = in.readInt();
			System.out.println(numberOfFiles);
			for (int i = 0; i < numberOfFiles; i++) {
				File file = new File("E:\\DSP\\" + in.readUTF());
				fileOutStream = new FileOutputStream(file);
				byte[] bytes = new byte[in.readInt()];
				in.read(bytes);
				fileOutStream.write(bytes);
				directory.add(file);
				fileOutStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}