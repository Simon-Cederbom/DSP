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

	public void send(byte[] bytes) {
		try {
			out.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SynchronisationClient c = new SynchronisationClient();
		File folder = new File("E:\\DSP\\Testmapp");
		byte[] bytes = new byte[(int) folder.length()];
//		try {
//			//c.send(bytes);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
		File[] list = folder.listFiles();
		InputStream fileStream = null;
		try {
			fileStream = new FileInputStream(list[0]);
			fileStream.read(bytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (File file : list) {
			System.out.println(file.getName());
		}
	}
}