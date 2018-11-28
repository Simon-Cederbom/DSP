import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

public class SynchronisationClient {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;

	public SynchronisationClient() {
		try {
			s = new Socket("localhost", SynchronisationServer.PORT);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			Scanner scanner = new Scanner(System.in);
			System.out.println("What folder do you want to sync? ");
			File directory = new File(scanner.nextLine());
			scanner.close();
			String id = UUID.randomUUID().toString();
			out.writeUTF(id);
			send(directory);
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	public void send(File directory) {
		FileInputStream fileStream = null;
		try {
			out.writeInt(directory.listFiles().length);
			for (File file : directory.listFiles()) {
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
			for(int i = 0; i < numberOfFiles; i++) {
				File file = new File(tempDirectory.getName() + "\\" + in.readUTF());
				fileStream = new FileOutputStream(file);
				byte[] bytes = new byte[in.readInt()];
				in.read(bytes);
				fileStream.write(bytes);
				fileStream.close();
			}
			System.out.println("The files that needs to be backed up is:");
			for(File tempFile : tempDirectory.listFiles()) {
				System.out.println(tempFile.getName());
				tempFile.delete();
			}
			tempDirectory.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SynchronisationClient c = new SynchronisationClient();
		FileReceiver receiver = new FileReceiver(c);
		receiver.start();
		//File folder = new File("Testmapp");
		// System.out.println(list[0].getName());
		//c.send(folder);
//		for (File file : list) {
//			System.out.println(file.getName());
//		}
	}
}