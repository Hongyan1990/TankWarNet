import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankDeadMsg implements Msg {
	
	private int tankId;
	private int msgType = Msg.TANK_DEAD_MSG;
	private TankClient tc = null;
	
	public TankDeadMsg(int id) {
		this.tankId = id;
	}
	
	public TankDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			
			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, port));
			ds.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		int id;
		try {
			id = dis.readInt();
			if(id == tc.myTank.id) return;
			for(int i=0; i<tc.enemyTanks.size(); i++) {
				Tank t = tc.enemyTanks.get(i);
				if(t.id == id) {
					t.setLive(false);
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
