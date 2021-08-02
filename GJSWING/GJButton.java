package GJSWING;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class GJButton extends GJPanel implements GJAction{
	ButtonRenderer buttonrenderer;
	String text;
	boolean pressed=false;
	GJAction buttonaction;
	Color c;
	public GJButton(int x, int y,String text, Color c,GJAction action) {
		super(x, y, 100, 50);
		buttonrenderer = new ButtonRenderer(this);
		this.buttonaction = action;
		this.renderer = buttonrenderer;
		this.action = this;
		this.text = text;
		this.c = c;
		this.repaint();
		// TODO Auto-generated constructor stub
	}
	public class ButtonRenderer extends GJGraphics{
		GJButton button;
		public ButtonRenderer(GJButton button){
			this.button = button;
		}
		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			if(pressed){
				g2d.setColor(Color.lightGray);
			}else{
				g2d.setColor(c);
			}
		
			g2d.fillRect(0, 0, 100, 50);
			g2d.setColor(Color.black);
			GJGraphics.drawCenteredString(g2d, text, new Rectangle(0,0,100,50), new Font("Arial",Font.BOLD,20));
		}
		
	}
	@Override
	public void Clicked(int x,int y) {
		// TODO Auto-generated method stub
		this.buttonaction.Clicked(x,y);
	}
	@Override
	public void Pressed() {
		// TODO Auto-generated method stub
		this.buttonaction.Pressed();
		this.pressed = true;
		this.window.render();
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		this.buttonaction.Release();
		this.pressed = false;
		this.window.render();
	}
	@Override
	public void MouseMove(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void MouseEntered() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void MouseExit() {
		// TODO Auto-generated method stub
		
	}
}
