package GJSWING;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import GJOBJECTS.GJObject;

public class GJPanel extends JPanel{
	public Graphics2D g2d;
	public Graphics g;
	public GJGraphics renderer;
	public GJObject[] objects;
	public GJAction action;
	public GJWindow window;
	public GJPanel(int x,int y,int width,int height,GJGraphics renderer,GJAction action){
		this.setBounds(x, y, width, height);
		this.renderer = renderer;
		MouseAction mouseaction = new MouseAction();
		this.addMouseListener(mouseaction);
		this.addMouseMotionListener(mouseaction);
		this.repaint();
		this.action = action;
		this.show();
	}
	public GJPanel(int x,int y,int width,int height,GJGraphics renderer){
		this.setBounds(x, y, width, height);
		this.renderer = renderer;
		MouseAction mouseaction = new MouseAction();
		this.addMouseListener(mouseaction);
		this.addMouseMotionListener(mouseaction);
		this.show();
	}
	public GJPanel(int x,int y,int width,int height,GJAction action){
		this.setBounds(x, y, width, height);
		this.renderer = renderer;
		MouseAction mouseaction = new MouseAction();
		this.addMouseListener(mouseaction);
		this.addMouseMotionListener(mouseaction);
		this.show();
	}
	public GJPanel(int x,int y,int width,int height){
		this.setBounds(x, y, width, height);
		this.renderer = renderer;
		MouseAction mouseaction = new MouseAction();
		this.addMouseListener(mouseaction);
		this.addMouseMotionListener(mouseaction);
		this.show();
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g2d = (Graphics2D)g;
		this.renderer.draw(g2d);
	}
	public class MouseAction implements MouseListener,MouseMotionListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1){
				if(action != null){
					action.Clicked(e.getX(),e.getY());
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(action != null){
				action.MouseEntered();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			if(action != null){
				action.MouseExit();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getButton() == 1){
				if(action != null){
					action.Pressed();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getButton() == 1){
				if(action != null){
					action.Release();
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(action != null){
				action.MouseMove(e.getX(), e.getY());
			}
		}};
}
