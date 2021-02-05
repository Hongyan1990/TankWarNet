import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class NewTankMsg implements Msg {
	Tank tank = null;
	TankClient tc = null;
	public int msgType = Msg.NEW_TANK_MSG;
	public NewTankMsg(Tank t) {
		this.tank = t;
	}
	
	public NewTankMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String IP, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		if(tank != null) {
			
			try {
				dos.writeInt(msgType);
				dos.writeInt(tank.id);
				dos.writeInt(tank.x);
				dos.writeInt(tank.y);
				dos.writeInt(tank.dir.ordinal());
				dos.writeBoolean(tank.good);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf,buf.length, new InetSocketAddress(IP, port));
			try {
				ds.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		System.out.println("create tank!");
		int id;
		try {
			id = dis.readInt();
			if(id == tc.myTank.id) return;
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			boolean exsit = false;
			for(int i=0; i<tc.enemyTanks.size(); i++) {
				Tank tank = tc.enemyTanks.get(i);
				if(tank.id == id) {
					exsit = true;
					break;
				}
			}
			if(!exsit) {
				Msg msg = new NewTankMsg(tc.myTank);
				tc.nc.send(msg);
				
				Tank t = new Tank(x, y, good, dir, tc);
				t.id = id;
				tc.enemyTanks.add(t);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
