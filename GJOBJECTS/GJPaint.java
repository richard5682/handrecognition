package GJOBJECTS;

import java.awt.Color;
import java.awt.Graphics2D;

import GJSWING.GJGraphics;

public class GJPaint extends GJObject{
	public GJPaint(){
		
	}
	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, 100, 100);
	}
	
}
