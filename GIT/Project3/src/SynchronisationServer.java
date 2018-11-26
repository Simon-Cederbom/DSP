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

	public ArrayList<File> compareFiles(String id) {
		File fileDirectory = new File(id);
		ArrayList<File> diff = new ArrayList<File>();
		for (File tempFile : directory) {
			boolean found = false;
			for (File file : fileDirectory.listFiles()) {
				FileInputStream tempStream = null;
				FileInputStream fileStream = null;
				try {
					tempStream = new FileInputStream(tempFile);
					byte[] tempBytes = new byte[(int) tempFile.length()];
					tempStream.read(tempBytes);
					fileStream = new FileInputStream(file);
					byte[] bytes = new byte[(int) file.length()];
					if (tempBytes.equals(bytes)) {
						found = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (tempStream != null) {
							tempStream.close();
						}
						if (fileStream != null) {
							fileStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(!found) {
				diff.add(tempFile);
			}
		}
		return diff;
	}

	public void run() {
		FileOutputStream fileOutStream = null;
		try {
			int numberOfFiles = in.readInt();
			File tempDirectory = new File("ServerTemp");
			tempDirectory.mkdir();
			for (int i = 0; i < numberOfFiles; i++) {
				File file = new File(tempDirectory.getName() + "\\" + in.readUTF());
				fileOutStream = new FileOutputStream(file);
				byte[] bytes = new byte[in.readInt()];
				in.read(bytes);
				fileOutStream.write(bytes);
				directory.add(file);
				fileOutStream.close();
			}
			tempDirectory.delete();
			ArrayList<File> diff = this.compareFiles("ServerTest");
			for(File file : diff) {
				System.out.println(file.getName());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}