package GJSWING;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import javax.swing.JFrame;

public class GJWindow extends JFrame{
	static GraphicsEnvironment GRAPHICSENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice GRAPHICSDEVICE = GJWindow.GRAPHICSENVIRONMENT.getDefaultScreenDevice();
	static DisplayMode DISPLAYMODE = GJWindow.GRAPHICSDEVICE.getDisplayMode();
	static int SCREEN_WIDTH = DISPLAYMODE.getWidth();
	static int SCREEN_HEIGHT = DISPLAYMODE.getHeight();
	ArrayList<GJPanel> components = new ArrayList<GJPanel>();
	public GJPanel main_panel;
	public GJWindow(int width,int height,Color c,GJPanel panel){
		this.main_panel = panel;	
		this.add(main_panel);
		components.add(main_panel);
		this.setBounds((SCREEN_WIDTH-width)/2,(SCREEN_HEIGHT-height)/2, width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(c);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Test");
	}
	public GJWindow(int x,int y,int width,int height,Color c,GJPanel panel){
		this.main_panel = panel;	
		this.add(main_panel);
		components.add(main_panel);
		this.setBounds(x,y, width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(c);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Test");
	}
	public GJWindow(int width,int height,Color c){
		this.setBounds((SCREEN_WIDTH-width)/2,(SCREEN_HEIGHT-height)/2, width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(c);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Test");
	}
	public GJWindow(int x,int y,int width,int height,Color c){
		this.setBounds(x,y, width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(c);
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("Test");
	}
	public void AddComponent(GJPanel p){
		components.add(p);
		p.window = this;
		this.add(p);
	}
	public void render(){
		for(int i=0;i<components.size();i++){
			components.get(i).repaint();;
		}
	}
}
