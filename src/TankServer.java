import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6000;
	public static int ID = 100;
	List<Client> clients = new ArrayList<Client>();
	
	public void start() {
		new Thread(new UDPThread()).start();
		try {
			ServerSocket ss = new ServerSocket(TCP_PORT);
			while(true) {
				Socket s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String IP = s.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				Client c = new Client(IP, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ID++);
				s.close();
				System.out.println("A Client Connect, Addr-" + s.getInetAddress() + ":" + s.getPort() + "---UDP Port:" + udpPort);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new TankServer().start();
	}
	
	private class Client {
		int udpPort;
		String IP;
		
		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
	}
	
	private class UDPThread implements Runnable {
		byte[] buf = new byte[1024];
		@Override
		public void run() {
			DatagramSocket ds = null;
			
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					for(int i=0; i<clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
						ds.send(dp);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
