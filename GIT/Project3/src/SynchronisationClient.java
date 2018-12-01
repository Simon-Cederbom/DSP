import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

public class SynchronisationClient {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;
	String directoryPath;
	FileReceiver receiver;
	CommandSender sender;

	public SynchronisationClient(Scanner scanner) {
		try {
			s = new Socket("localhost", SynchronisationServer.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			String id = UUID.randomUUID().toString();
			out.writeUTF(id);
			System.out.println("What folder do you want to sync? ");
			File directory = new File(scanner.nextLine());
			directoryPath = directory.getPath();
			// directory = new File(scanner.nextLine());
			send(directoryPath);
			System.out.println(in.readUTF());
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	public void sendCommand(String message) {
		try {
			out.writeUTF(message);
			if (message.equals("-quit")) {
				quit();
			} else if (message.equals("-sync")) {
				send(directoryPath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(String path) {
		FileInputStream fileStream = null;
		try {
			File d = new File(path);
			out.writeInt(d.listFiles().length);
			for (File file : d.listFiles()) {
				byte[] bytes = new byte[(int) file.length()];
				fileStream = new FileInputStream(file);
				fileStream.read(bytes);
				out.writeUTF(file.getName());
				out.writeInt(bytes.length);
				out.write(bytes);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void receive() {
		FileOutputStream fileStream = null;
		try {
			int numberOfFiles = in.readInt();
			File tempDirectory = new File("Temp");
			tempDirectory.mkdir();
			for (int i = 0; i < numberOfFiles; i++) {
				File file = new File(tempDirectory.getName() + "\\" + in.readUTF());
				byte[] bytes = new byte[in.readInt()];
				in.read(bytes);
				fileStream = new FileOutputStream(file);
				fileStream.write(bytes);
				fileStream.close();
			}
			if (tempDirectory.listFiles().length == 0) {
				System.out.println("All files are up to date.");
			} else {
				System.out.println("The files that needs to be backed up is:");
				for (File tempFile : tempDirectory.listFiles()) {
					System.out.println(tempFile.getName());
					tempFile.delete();
				}
			}
			tempDirectory.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void quit() {
		receiver.stop();
		sender.stop();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		SynchronisationClient c = new SynchronisationClient(scanner);
		c.receiver = new FileReceiver(c);
		c.receiver.start();
		c.sender = new CommandSender(c, scanner);
		c.sender.start();
		while (c.receiver.isAlive() && c.sender.isAlive()) {

		}
	}
}