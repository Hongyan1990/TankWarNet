
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {
	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;
	Image imageScreenOff = null;
	
	TankThread tankThread = new TankThread();
	Tank myTank = new Tank(50, 50, true,this);
	Tank enemyTank = new Tank(300, 200, false, this);
//	Wall w1 = new Wall(100, 100, 20, 60, this);
//	Wall w2 = new Wall(200, 150, 60, 20, this);
//	Missile m = null;
	List<Missile> ms = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> enemyTanks = new ArrayList<Tank>();
//	List<Blood> bloods = new ArrayList<Blood>();
	
	NetClient nc = new NetClient(this);
	PortDialog dialog = new PortDialog();
	
	public void launchFrame() {
		this.setLocation(300, 200);
		this.setSize(WIDTH, HEIGHT);
		this.setBackground(Color.GRAY);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		this.addKeyListener(new MonitorKey());
		this.setResizable(false);
		this.setVisible(true);
//		for(int i=1; i<=2; i++) {
//			this.enemyTanks.add(new Tank(50 + 40*i, 50 + 30*i, false, this));
//		}
		new Thread(tankThread).start();
//		nc.connect("127.0.0.1", TankServer.TCP_PORT);
	}
	
//	public void createBlood() {
//		for(int i=0; i<5; i++) {
//			this.bloods.add(new Blood(i));
//		}
//	}

	@Override
	public void update(Graphics g) {
		if(imageScreenOff == null) {
			imageScreenOff = this.createImage(WIDTH, HEIGHT);
		}
		Graphics gOff = imageScreenOff.getGraphics();
		Color c = g.getColor();
		gOff.setColor(Color.GRAY);
		gOff.fillRect(0, 0, WIDTH, HEIGHT);
		gOff.setColor(c);
		paint(gOff);
		g.drawImage(imageScreenOff, 0, 0, null);
	}



	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.YELLOW);
		g.drawString("missile count: " + ms.size(), 10, 50);
		g.drawString("explode count: " + explodes.size(), 10, 70);
		g.drawString("tank count: " + enemyTanks.size(), 10, 90);
		g.setColor(c);
//		w1.draw(g);
//		w2.draw(g);
		for(int i=0; i<ms.size(); i++) {
			Missile m = ms.get(i);
//			m.hitTanks(enemyTanks);
			if(m.hitTank(myTank)) {
				Msg msg = new TankDeadMsg(myTank.id);
				nc.send(msg);
				Msg missileMsg = new MissileDeadMsg(m.tankId, m.id);
				nc.send(missileMsg);
			};
//			m.hitWall(w1);
//			m.hitWall(w2);
			m.drawMissile(g);
		}
		
		for(int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}
		for(int i=0; i<enemyTanks.size(); i++) {
			Tank t = enemyTanks.get(i);
//			t.collidesWithWall(w1);
//			t.collidesWithWall(w2);
			t.collidesWithTank(enemyTanks);
			t.drawTank(g);
		}
//		for(int i=0; i<bloods.size(); i++) {
//			Blood b = bloods.get(i);
//			b.draw(g);
//		}
		myTank.drawTank(g);
//		myTank.eat(bloods);
//		b.draw(g);
//		enemyTank.drawTank(g);
	}
	
	class TankThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	class MonitorKey extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_C) {
				dialog.setVisible(true);
			} else {
				myTank.keyPressed(e);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

	}
	
	class PortDialog extends Dialog {
		TextField tf = new TextField("2223", 4);
		Button btn = new Button("save port");
		public PortDialog() {
			super(TankClient.this, true);
			this.setLayout(new FlowLayout());
			this.add(new Label("UDPPort:"));
			this.add(tf);
			this.add(btn);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
				
			});
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int udpPort = Integer.parseInt(tf.getText().trim());
					nc.setUdpPort(udpPort);
					nc.connect("127.0.0.1", TankServer.TCP_PORT);
					setVisible(false);
				}
			});
		}
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();

	}

}

