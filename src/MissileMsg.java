import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileMsg implements Msg {
	
	private Missile m = null;
	private TankClient tc = null;
	
	public MissileMsg(Missile m) {
		this.m = m;
	}
	
	public MissileMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		int msgType = Msg.MISSILE_MSG;
		int tankId = m.tankId;
		int x = m.x;
		int y = m.y;
		int dir = m.dir.ordinal();
		boolean good = m.good;
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir);
			dos.writeBoolean(good);
			
			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, port));
			ds.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void parse(DataInputStream dis) {
		int id = 0;
		
		try {
			id = dis.readInt();
			System.out.println(id + " luanch missile");
			if(id == tc.myTank.id) return;
			
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
			Missile m = new Missile(x, y, dir, good, id, tc);
			tc.ms.add(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
