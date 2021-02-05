
import java.awt.Color;
import java.awt.Graphics;

public class Explode {
	private int x;
	private int y;
	private int step = 0;
	private boolean live = true;
	private int[] size = {4, 7, 12, 18, 26, 32, 49, 30, 14, 6};
	
	TankClient tc = null;
	
	public Explode(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		if(!live) {
			tc.explodes.remove(this);
			return;
		}
		if(step == size.length-1) {
			live = false;
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.fillOval(x, y, size[step], size[step]);
		
		step++;
	}
}

