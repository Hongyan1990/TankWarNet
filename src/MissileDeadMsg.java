import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MissileDeadMsg implements Msg {
	private int msgType = Msg.MISSILE_DEAD_MSG;
	private int tankId;
	private int id;
	private TankClient tc = null;
	
	public MissileDeadMsg(int tankId, int id) {
		this.tankId = tankId;
		this.id = id;
	}
	
	public MissileDeadMsg(TankClient tc) {
		this.tc = tc;
	}
	
	@Override
	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			dos.writeInt(id);
			
			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, port));
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		int tankId, id;
		
		try {
			tankId = dis.readInt();
			id = dis.readInt();
			for(int i=0; i<tc.ms.size(); i++) {
				Missile m = tc.ms.get(i);
				if(m.tankId==tankId && id==m.id) {
					m.setLive(false);
					Explode e = new Explode(m.x, m.y, tc);
					tc.explodes.add(e);
				}
			}
			System.out.println("missile id : " + id + ", tank id : " + tankId);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
