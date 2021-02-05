
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Missile {
	public static final int SPEED_X = 10;
	public static final int SPEED_Y = 10;
	
	public static final int WIDTH = 8;
	public static final int HEIGHT = 8;
	
	private static int ID = 1;
	
	public int x = 0;
	public int y = 0;
	
	public boolean live = true;
	
	public boolean good;
	public int tankId;
	public int id;
	
//	private static Image[] missileImages = null;
//	private static Toolkit tk = Toolkit.getDefaultToolkit();
//	private static Map<String, Image> imgs = new HashMap<String, Image>();
	
	Dir dir;
	
	TankClient tc = null;
	
//	static {
//		missileImages = new Image[] {
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileL.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileLU.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileU.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileRU.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileR.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileRD.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileD.gif")),
//				tk.getImage(Missile.class.getClassLoader().getResource("images/missileLD.gif"))
//		};
//		
//		imgs.put("L", missileImages[0]);
//		imgs.put("LU", missileImages[1]);
//		imgs.put("U", missileImages[2]);
//		imgs.put("RU", missileImages[3]);
//		imgs.put("R", missileImages[4]);
//		imgs.put("RD", missileImages[5]);
//		imgs.put("D", missileImages[6]);
//		imgs.put("LD", missileImages[7]);
//	}
	
	public Missile(int x, int y, Dir dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.id = ID++;
	}
	
	public Missile(int x, int y, Dir dir, boolean good, TankClient tc) {
		this(x, y, dir);
		this.tc = tc;
		this.good = good;
	}
	
	public Missile(int x, int y, Dir dir, boolean good, int id, TankClient tc) {
		this(x, y, dir, good, tc);
		this.tankId = id;
	}
	
	public void drawMissile(Graphics g) {
		if(!live) {
			tc.ms.remove(this);
			return;
		}
		Color c = g.getColor();
		if(good) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.BLACK);
		}
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		move();
		checkDead();
	}
	
	public void move() {
		switch(dir) {
		case L:
			x -= SPEED_X;
			break;
		case LU:
			x -= SPEED_X;
			y -= SPEED_Y;
			break;
		case U:
			y -= SPEED_Y;
			break;
		case RU:
			y -= SPEED_Y;
			x += SPEED_X;
			break;
		case R:
			x += SPEED_X;
			break;
		case RD:
			x += SPEED_X;
			y += SPEED_Y;
			break;
		case D:
			y += SPEED_Y;
			break;
		case LD:
			y += SPEED_Y;
			x -= SPEED_X;
			break;
		default:
			x += 0;
			y += 0;
		}
	}
	
	public void checkDead() {
		if(x<0 || y<0 || x>TankClient.WIDTH || y>TankClient.HEIGHT) {
			this.setLive(false);
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	} 
	
	public boolean hitTank(Tank t) {
		if(this.getRect().intersects(t.getRect()) && t.isLive() && tankId != t.id) {
//			if(t.good) {
//				t.setLife(t.getLife() - 20);
//				if(t.getLife() <= 0) t.setLive(false);
//			} else {
//				t.setLive(false);
//			}
			t.setLive(false);
			this.live = false;
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}
	
	public boolean hitTanks(List<Tank> tanks) {
		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			if(hitTank(t)) {
				return true;
			}
		}
		return false;
	}
	
//	public boolean hitWall(Wall w) {
//		if(this.getRect().intersects(w.getRect())) {
//			this.live = false;
//			return true;
//		}
//		return false;
//	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
	public boolean getLive() {
		return live;
	}
	
}

