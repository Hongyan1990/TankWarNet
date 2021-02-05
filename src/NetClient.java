import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetClient {
//	private static int UDP_PORT_START = 2223;
	private int udpPort;
	
	TankClient tc = null;
	DatagramSocket ds = null;
	
	public NetClient(TankClient tc) {

		this.tc = tc;
	}
	
	public void connect(String IP, int port) {
		Socket s = null;
		
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new UDPRecvThread()).start();
		
		try {
			s = new Socket(IP, port);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpPort);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			int id = dis.readInt();
			tc.myTank.id = id;
			if(id%2 == 0) {
				tc.myTank.good = true;
			} else {
				tc.myTank.good = false;
			}
			System.out.println("Connected to server");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(s != null) {
				try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				s = null;
			}
		}
		
		Msg msg = new NewTankMsg(tc.myTank);
		send(msg);
	}
	
	public void send(Msg msg) {
		msg.send(ds, "localhost", TankServer.UDP_PORT);
	}
	
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	private class UDPRecvThread implements Runnable {
		public byte[] buf = new byte[1024];
		@Override
		public void run() {
			while(ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parse(dp);
				System.out.println("recv UDP msg");
			}
		}
		
		public void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			Msg msg = null;
			
			try {
				msgType = dis.readInt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(msgType) {
			case Msg.NEW_TANK_MSG:
				msg = new NewTankMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_MOVE_MSG:
				msg = new TankMoveMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_MSG:
				msg = new MissileMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.TANK_DEAD_MSG:
				msg = new TankDeadMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			case Msg.MISSILE_DEAD_MSG:
				msg = new MissileDeadMsg(NetClient.this.tc);
				msg.parse(dis);
				break;
			}
		}
		
	}
	
}
