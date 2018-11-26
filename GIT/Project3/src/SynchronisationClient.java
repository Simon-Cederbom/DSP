import java.io.*;
import java.net.*;

public class SynchronisationClient {
	Socket s = null;
	DataInputStream in;
	DataOutputStream out;

	public SynchronisationClient() {
		try {
			s = new Socket("localhost", SynchronisationServer.PORT);
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

	public void send(File directory) {
		FileInputStream fileStream = null;
		try {
			out.writeInt((int) directory.listFiles().length);
			for (File file : directory.listFiles()) {
				byte[] bytes = new byte[(int) file.length()];
				fileStream = new FileInputStream(file);
				fileStream.read(bytes);
				out.writeUTF(file.getName());
				out.writeInt((int) file.length());
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
		File file = null;
		try {
			byte[] bytes = new byte[in.readInt()];
			in.read(bytes);
			file = new File("test");
			fileStream = new FileOutputStream(file);
			fileStream.write(bytes);
//			File folder = new File("E:\\DSP\\Testmapp");
//			File[] list = folder.listFiles();
//			System.out.println(file.compareTo(list[0]));
			// File file = File.createTempFile("test", ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (fileStream != null) {
					fileStream.close();
				}
				if (file != null) {
					//file.delete();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		SynchronisationClient c = new SynchronisationClient();
		FileReceiver receiver = new FileReceiver(c);
		receiver.start();
		File folder = new File("Testmapp");
		// System.out.println(list[0].getName());
		c.send(folder);
//		for (File file : list) {
//			System.out.println(file.getName());
//		}
	}
}