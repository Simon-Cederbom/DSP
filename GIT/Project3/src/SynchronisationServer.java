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
	String id;
	volatile boolean stop = false;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			id = in.readUTF();
			File directory = new File(id);
			directory.mkdir();
			int numberOfFiles = in.readInt();
			FileOutputStream fileOutStream = null;
			for (int i = 0; i < numberOfFiles; i++) {
				File file = new File(directory.getName() + "\\" + in.readUTF());
				fileOutStream = new FileOutputStream(file);
				byte[] bytes = new byte[in.readInt()];
				in.read(bytes);
				fileOutStream.write(bytes);
				fileOutStream.close();
			}
			out.writeUTF("Files are now backed up");
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public ArrayList<File> compareFiles() {
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
					fileStream.read(bytes);
					if (compareBytes(tempBytes, bytes)) {
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
			if (!found) {
				diff.add(tempFile);
			}
		}
		return diff;
	}

	public boolean compareBytes(byte[] a, byte[] b) {
		if (a.length != b.length) {
			return false;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] != b[i]) {
					return false;
				}
			}
		}
		return true;
	}

	public void requestStop() {
		stop = true;
	}
	
	public void deleteDirectory(File folder) {
		for(File file : folder.listFiles()) {
			file.delete();
		}
		folder.delete();
	}

	public void run() {
		FileOutputStream fileOutStream = null;
		FileInputStream fileInStream = null;
		try {
			while (!stop) {
				String command = in.readUTF();
				if (command.equals("-quit")) {
					deleteDirectory(new File(id));
					requestStop();
					out.writeUTF(command);
				} else if(command.equals("-sync")){
					out.writeUTF(command);
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
					ArrayList<File> diff = this.compareFiles();
					out.writeInt(diff.size());
					for (File file : diff) {
						byte[] bytes = new byte[(int) file.length()];
						fileInStream = new FileInputStream(file);
						fileInStream.read(bytes);
						out.writeUTF(file.getName());
						out.writeInt(bytes.length);
						out.write(bytes);
						fileInStream.close();
					}
					directory.clear();
					diff.clear();
					deleteDirectory(tempDirectory);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}