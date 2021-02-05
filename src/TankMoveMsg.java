import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TankMoveMsg implements Msg {
	private int id;
	private Dir dir;
	private int msgType = Msg.TANK_MOVE_MSG;
	
	TankClient tc = null;
	
	public TankMoveMsg(int id, Dir dir) {
		this.id = id;
		this.dir = dir;
	}
	
	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(dir.ordinal());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
		try {
			ds.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void parse(DataInputStream dis) {
		
		try {
			int id = dis.readInt();
			System.out.println("move tank..." + id);
			if(id == tc.myTank.id) return;
			Dir dir = Dir.values()[dis.readInt()];
			for(int i=0; i<tc.enemyTanks.size(); i++) {
				Tank t = tc.enemyTanks.get(i);
				if(t.id == id) {
					t.dir = dir;
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
