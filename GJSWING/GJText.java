package GJSWING;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class GJText extends GJPanel {
	TextRenderer textrenderer = new TextRenderer();
	String text = "HELLO";
	Color bg,fc;
	public GJText(int x, int y, int width, int height,Color fc,Color bg) {
		super(x, y, width, height);
		this.bg = bg;
		this.fc = fc;
		this.renderer = textrenderer;
		// TODO Auto-generated constructor stub
	}
	public void ChangeString(String text){
		this.text = text;
		this.window.render();
	}
	public class TextRenderer extends GJGraphics{

		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			g2d.setColor(bg);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			g2d.setColor(fc);
			GJGraphics.drawCenteredString(g2d, text, new Rectangle(0,0,getWidth(),getHeight()), new Font("Arial",Font.BOLD,20));
		}
		
	}
}
