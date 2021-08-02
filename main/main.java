package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import GJFILE.GJFileLoader;
import GJIMAGE.GJImageProcess;
import GJIMAGE.GJImageTune;
import GJIMAGE.GJImageTune.Point;
import GJIMAGE.GJImageTune.Symbol;
import GJIMAGE.GJImageUtil;
import GJMATH.GJMATHUTIL;
import GJSWING.GJAction;
import GJSWING.GJButton;
import GJSWING.GJGraphics;
import GJSWING.GJPanel;
import GJSWING.GJWindow;

public class main {
	public static final String COLOR_DIRECTORY = "C:\\Users\\GJ TECSON\\Desktop\\Training Data\\Color_Data";
	public static capture_graphics captureg = new capture_graphics();
	public static GJPanel main_panel = new GJPanel(100,310,400,400,new main_graphics());
	public static GJPanel capture_panel = new GJPanel(100,0,300,300,captureg, new capture_action());
	public static GJButton button1 = new GJButton(0,0,"Capture",Color.gray,new button1action());
	public static GJButton button2 = new GJButton(0,50,"Process",Color.gray,new button2action());
	public static GJButton button3 = new GJButton(0,100,"Reset",Color.gray,new button3action());
	public static GJButton button4 = new GJButton(0,150,"Save Color",Color.gray,new button4action());
	public static GJWindow window1 = new GJWindow(800,800,Color.DARK_GRAY,main_panel);
	public static GJWindow window2 = new GJWindow(100,200,400,400,Color.green);
	public static BufferedImage capture_image = null;
	public static Color[] colors_array = new Color[0];
	public static BufferedImage image1,image2,image3,image4;
	public static Point PalmCenter;
	public static Symbol hand;
	public static Point[] palm_polygon = new Point[0];
	public static Point[] wrist_points  = new Point[0];
	public static Point[][] finger_points  = new Point[0][0];
	public static ArrayList<Color> colors = new ArrayList<Color>();
	public static Hand hand_obj;
	public static int mx,my;
	public static void main(String args[]){
		window1.AddComponent(button1);
		window1.AddComponent(button2);
		window1.AddComponent(button3);
		window1.AddComponent(button4);
		window1.AddComponent(capture_panel);
		window1.show();
		window2.show();
		LoadColor();
		while(true){
			Capture();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static class main_graphics extends GJGraphics{

		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			g2d.setColor(Color.white);
			g2d.drawImage(image1,0,0,200,200,null);
			g2d.drawImage(image2,200,0,200,200,null);
			g2d.drawImage(image3,0,200,200,200,null);
			g2d.drawImage(image4,200,200,200,200,null);
			g2d.setColor(Color.red);
			g2d.drawRect(mx*200/300,my*200/300, 1, 1);
			if(main.hand_obj != null){
				hand_obj.drawHand(g2d, new Point(0,0), 200, image1);
				hand_obj.drawHandUnscale(g2d, new Point(200,0), 200, image2);
			}
			
		}
		
	}
	public static class capture_graphics extends GJGraphics{
		ArrayList<Point> points = new ArrayList<Point>();
		int x,y;
		boolean draw_mouse=false;
		@Override
		public void draw(Graphics2D g2d) {
			// TODO Auto-generated method stub
			g2d.drawImage(capture_image, 0, 0,300,300, null);
			if(draw_mouse){
				g2d.setColor(Color.red);
				g2d.drawRect(x-3, y-3, 6, 6);
			}
			for(int i=0;i<points.size();i++){
				g2d.setColor(Color.black);
				g2d.drawRect(points.get(i).x-3, points.get(i).y-3, 6, 6);
			}
			if(main.PalmCenter != null){
				hand_obj.drawHand(g2d, new Point(0,0), 300, capture_image);
			}
		}
		
	}
	public static class capture_action implements GJAction{

		@Override
		public void Clicked(int x,int y) {
			// TODO Auto-generated method stub
			captureg.points.add(new Point(x,y));
			float ratio = ((float)capture_image.getWidth()/300);
			colors.add(new Color(capture_image.getRGB((int)(x*ratio), (int)(y*ratio))));
			colors_array = new Color[colors.size()];
			colors_array = colors.toArray(colors_array);
		}

		@Override
		public void Pressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Release() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void MouseMove(int x, int y) {
			// TODO Auto-generated method stub
			captureg.x = x;
			captureg.y = y;
			main.mx = x;
			main.my = y;
			window1.render();
		}

		@Override
		public void MouseEntered() {
			// TODO Auto-generated method stub
			captureg.draw_mouse = true;
			window1.render();
		}

		@Override
		public void MouseExit() {
			// TODO Auto-generated method stub
			captureg.draw_mouse = false;
			window1.render();
		}
		
	}
	public static void Capture(){
		capture_image = GJImageUtil.CaptureScreen(window2.bounds().x, window2.bounds().y, window2.bounds().width, window2.bounds().height);
		main.image1 = GJImageTune.FilterImage(colors_array, capture_image,0.1f,0.05f,0.1f);
		image2 = GJImageTune.BlackAndWhite(image1);
		image2 = GJImageTune.BinarizeImage(image2, 0.1f, image2.getWidth(), image2.getHeight());
		image2 = GJImageTune.InverseImage(image2);
		image2 = GJImageTune.DilateImage(image2,1, 2, 200);
		Symbol[] symbols = GJImageTune.GetSymbols(image2);
		
		if(symbols.length > 0){
			int size=0,index=0;
			for(int i=0;i<symbols.length;i++){
				if(i==0){
					size = symbols[i].points.length;
				}else{
					if(symbols[i].points.length > size && symbols[i].points.length > 50){
						size = symbols[i].points.length;
						index = i;
					}
				}
			}
			hand = symbols[index];
			if(hand != null){
				capture_image = GJImageTune.ReDrawImage(capture_image, hand, false, new Point(0,0));
				image2 = Symbol.ScaleSymbol(hand, 80).ToBufferImage();
				image3 = GJImageTune.DistanceTransform(image2,10);
				PalmCenter = GJImageProcess.GetCenterImage(image3, 150);
				image3 = GJImageTune.GetCoutour(image2);
				if(PalmCenter != null){
					float palm_radius = GJImageProcess.GetMinimumRadius(image3, PalmCenter);
					palm_polygon = GJImageProcess.GetEnclosingBound(image3, GJImageProcess.GetCirclePoints(PalmCenter, palm_radius*1.1f, 1));
					float maxdistance=0;
					Point[] bwrist_point = new Point[2];
					ArrayList<Point[]> finger_point = new ArrayList<Point[]>();
					for(int i=0;i<palm_polygon.length;i++){
						Point p1 = palm_polygon[i];
						Point p2 = null;
						if(i==0){
							p2 = palm_polygon[palm_polygon.length-1];
						}else{
							p2 = palm_polygon[i-1];
						}
						float distance = GJMATHUTIL.GetDistance(p1.x, p1.y, p2.x, p2.y);
							if(distance > maxdistance){
								maxdistance = distance;
								bwrist_point[0] = p1;
								bwrist_point[1] = p2;
							}
						wrist_points = bwrist_point;
					}
					for(int i=0;i<palm_polygon.length;i++){
						Point p1 = palm_polygon[i];
						Point p2 = null;
						if(i==0){
							p2 = palm_polygon[palm_polygon.length-1];
						}else{
							p2 = palm_polygon[i-1];
						}
						float distance = GJMATHUTIL.GetDistance(p1.x, p1.y, p2.x, p2.y);
						if(distance > palm_radius/2.5f && distance < maxdistance*0.9f){
							Point[] fp = new Point[2];
							fp[0] = p1;
							fp[1] = p2;
							finger_point.add(fp);
						}
					}
					finger_points = new Point[finger_point.size()][];
					finger_points = finger_point.toArray(finger_points);	
					image3 = GJImageTune.CropImagePolygon(image2, palm_polygon, PalmCenter);
					image4 = GJImageProcess.SubtractImage(image2, image3);
					image4 = GJImageTune.ErodeImage(image4, 1, 1, 200);
					Symbol[] hand_symbols = GJImageTune.GetSymbols(image4);
					image4 = GJImageTune.ReDrawImage(image2, hand_symbols, true, new Point(0,0));
					if(wrist_points != null){
						Hand handobj = new Hand(wrist_points,finger_points,PalmCenter,hand_symbols,palm_polygon,hand,palm_radius);
						main.hand_obj = handobj;
					}
					
					
				}
			}else{
				image3 = null;
			}
		}
		window1.render();
	}
	public static class Hand{
		Finger[] fingers;
		Point[] wrist_points;
		Point palm_center,wrist_center;
		Point[] hand_symbol_center;
		Symbol[] hand_symbols;
		Symbol[] fingers_symbols;
		Symbol wrist_symbol;
		Point[][] fingers_points;
		Point[] palm_polygon;
		Symbol hand;
		float PalmRadius;
		String recognition="";
		public Hand(Point[] wrist_point,Point[][] fingers_points,Point palm_center,Symbol[] hand_symbols,Point[] palm_polygon,Symbol hand,float PalmRadius){
			this.wrist_points = wrist_point;
			this.palm_center = palm_center;
			this.hand_symbols = hand_symbols;
			this.fingers_points = fingers_points;
			this.palm_polygon = palm_polygon;
			this.hand = hand;
			this.PalmRadius = PalmRadius;
			if(wrist_points[0] != null && wrist_points[1] != null){
				this.wrist_center = GJMATHUTIL.GetCenterPoint(wrist_points[0], wrist_points[1]);
				Point ref_vec = GJMATHUTIL.SubtractPoint(palm_center,wrist_center);
				
				ArrayList<Symbol> array_fingers = new ArrayList<Symbol>();
				hand_symbol_center = new Point[hand_symbols.length];
				for(int i=0;i<hand_symbols.length;i++){
					Point symbol_center = GJImageProcess.GetCenterPoint(hand_symbols[i].points);
					hand_symbol_center[i] = symbol_center;
					Point symbol_vec = GJMATHUTIL.SubtractPoint(symbol_center,wrist_center);
					float dot = GJMATHUTIL.DotProduct(ref_vec, symbol_vec);
					if(dot >= 0){
						array_fingers.add(hand_symbols[i]);
					}else{
						wrist_symbol = hand_symbols[i];
					}
				}
				this.fingers_symbols = new Symbol[array_fingers.size()];
				this.fingers_symbols = array_fingers.toArray(this.fingers_symbols);
				image4 = GJImageTune.ReDrawImage(image2, fingers_symbols, true, new Point(0,0));
				Initialize();
				Recognize();
			}
			
		}
		public void Recognize(){
			boolean[] fact = new boolean[5];
			for(int i=0;i<fact.length;i++){
				fact[i] = false;
			}
			for(int i=0;i<fingers.length;i++){
				int index = fingers[i].index;
				if(fingers[i].activated){
					fact[index] = true;
				}
			}
			recognition = "";
			if(fact[0] && !fact[1] && !fact[2] && !fact[3] && !fact[4]){
				recognition = "THUMBS UP";
			}else if(!fact[0] && fact[1] && !fact[2] && !fact[3] && !fact[4]){
				recognition = "ONE";
			}else if(!fact[0] && fact[1] && fact[2] && !fact[3] && !fact[4]){
				recognition = "TWO";
			}else if(!fact[0] && fact[1] && fact[2] && fact[3] && !fact[4]){
				recognition = "THREE";
			}else if(!fact[0] && fact[1] && fact[2] && fact[3] && fact[4]){
				recognition = "FOUR";
			}else if(fact[0] && fact[1] && fact[2] && fact[3] && fact[4]){
				recognition = "HIGH FIVE!";
			}else if(fact[0] && fact[1] && !fact[2] && !fact[3] && fact[4]){
				recognition = "ROCK N ROLL  \\m/";
			}else if(fact[0] && !fact[1] && !fact[2] && !fact[3] && fact[4]){
				recognition = "CALL ME ;)";
			}else if(!fact[0] && !fact[1] && !fact[2] && !fact[3] && fact[4]){
				recognition = "PINKY";
			}else if(!fact[0] && !fact[1] && !fact[2] && fact[3] && !fact[4]){
				recognition = "RING FINGER";
			}
			
		}
		public void Initialize(){
			fingers = new Finger[finger_points.length];
			Finger.index_taken.clear();
			for(int i=0;i<finger_points.length;i++){
				fingers[i] = new Finger(finger_points[i],fingers_symbols,this);
			}
		}
		public void drawHand(Graphics2D g2d,Point offset,int size,BufferedImage image){
			Point p = GetCoord(PalmCenter,size,image);
//			g2d.setColor(Color.red);
//			for(int i=0;i<palm_polygon.length;i++){
//				int v=i-1;
//				if(i==0){
//					v=palm_polygon.length-1;
//				}
//				Point p1 = GetCoord(palm_polygon[i],size,image);
//				Point p2 = GetCoord(palm_polygon[v],size,image);
//				int x0 = (p1.x)+offset.x;
//				int y0  = (p1.y)+offset.y;
//				int x1 = (p2.x)+offset.x;
//				int y1 = (p2.y)+offset.y;
//				g2d.drawOval(x0-3,y0-3 , 6, 6);
//				g2d.drawLine(x0, y0, x1, y1);
//			}
			g2d.drawOval(p.x-3, p.y-3, 6, 6);
			g2d.setColor(Color.GREEN);
			if(wrist_points.length == 2){
				Point p1 = GetCoord(wrist_points[0],size,image);
				Point p2 = GetCoord(wrist_points[1],size,image);
				int x0 = (p1.x)+offset.x;
				int y0  = (p1.y)+offset.y;
				int x1 = (p2.x)+offset.x;
				int y1 = (p2.y)+offset.y;
				g2d.drawLine(x0,y0 ,x1 ,y1 );
			}
			g2d.setColor(Color.BLUE);
			for(int i=0;i<finger_points.length;i++){
				Point p1 = GetCoord(finger_points[i][0],size,image);
				Point p2 = GetCoord(finger_points[i][1],size,image);
				int x0 = (p1.x)+offset.x;
				int y0  = (p1.y)+offset.y;
				int x1 = (p2.x)+offset.x;
				int y1 = (p2.y)+offset.y;
				g2d.drawLine(x0,y0 ,x1 ,y1 );
			}
			//
			g2d.setColor(Color.RED);
			for(int i=0;i<finger_points.length;i++){
				Point p1 = GetCoord(finger_points[i][0],size,image);
				Point p2 = GetCoord(finger_points[i][1],size,image);
				int x0 = (p1.x)+offset.x;
				int y0 = (p1.y)+offset.y;
				int x1 = (p2.x)+offset.x;
				int y1 = (p2.y)+offset.y;
				g2d.drawLine(x0,y0 ,x1 ,y1 );
			}
			for(int i=0;i<hand_symbol_center.length;i++){
				Point p1 = GetCoord(hand_symbol_center[i],size,image);
				int x0 = (p1.x)+offset.x;
				int y0 = (p1.y)+offset.y;
				g2d.drawOval(x0-3,y0-3 , 6, 6);
			}
			for(int i=0;i<fingers.length;i++){
				if(fingers[i].sharp_point != null){
					g2d.setColor(Color.RED);
					Point p1 = GetCoord(fingers[i].joint_point,size,image);
					int x0 = (p1.x)+offset.x;
					int y0  = (p1.y)+offset.y;
					g2d.drawOval(x0-3,y0-3 , 6, 6);
					p1 = GetCoord(fingers[i].sharp_point,size,image);
					int x1 = (p1.x)+offset.x;
					int y1  = (p1.y)+offset.y;
					g2d.drawOval(x1-3,y1-3 , 6, 6);
					p1 = GetCoord(fingers[i].center_point,size,image);
					Point p2 = GetCoord(PalmCenter,size,image);
					int x2 = (p1.x)+offset.x;
					int y2  = (p1.y)+offset.y;
					int x3 = (p2.x)+offset.x;
					int y3  = (p2.y)+offset.y;
					g2d.drawOval(x2-3,y2-3 , 6, 6);
					g2d.setColor(Color.green);
					g2d.drawLine(x0, y0, x2, y2);
					g2d.drawLine(x2, y2,x1,y1);
					g2d.drawLine(x0, y0, x3, y3);
					if(fingers[i].activated){
						g2d.setColor(Color.magenta);
					}else{
						g2d.setColor(Color.red);
					}
					GJGraphics.drawCenteredString(g2d, fingers[i].finger_name, new Rectangle(x1, y1-10,10,10), new Font("Arial",Font.BOLD,15));
				}
				
			}
			Point p1 = GetCoord(PalmCenter,size,image);
			Point p2 = GetCoord(wrist_center,size,image);
			int x0 = (p1.x)+offset.x;
			int y0 = (p1.y)+offset.y;
			int x1 = (p2.x)+offset.x;
			int y1 = (p2.y)+offset.y;
			g2d.drawLine(x0, y0, x1, y1);
			g2d.setColor(Color.RED);
			GJGraphics.drawCenteredString(g2d, recognition, new Rectangle(x1, y1,100,50), new Font("Arial",Font.BOLD,25));
		}
		public void drawHandUnscale(Graphics2D g2d,Point offset,int size,BufferedImage image){
			if(PalmCenter != null){
				g2d.setColor(Color.red);
				g2d.drawOval((PalmCenter.x*size/image.getWidth())+offset.x-3, (PalmCenter.y*size/image.getWidth())+offset.y-3, 6, 6);
				g2d.drawOval((wrist_center.x*size/image.getWidth())+offset.x-3, (wrist_center.y*size/image.getWidth())+offset.y-3, 6, 6);
				for(int i=0;i<palm_polygon.length;i++){
					int v=i-1;
					if(i==0){
						v=palm_polygon.length-1;
					}
					int x0 = (palm_polygon[i].x*size/image.getWidth())+offset.x;
					int y0 = (palm_polygon[i].y*size/image.getWidth())+offset.y;
					int x1 = (palm_polygon[v].x*size/image.getWidth())+offset.x;
					int y1 = (palm_polygon[v].y*size/image.getWidth())+offset.y;
					g2d.drawOval(x0-3,y0-3 , 6, 6);
					g2d.drawLine(x0, y0, x1, y1);
				}
				g2d.setColor(Color.GREEN);
				if(wrist_points.length == 2){
					int x0 = (wrist_points[0].x*size/image.getWidth())+offset.x;
					int y0 = (wrist_points[0].y*size/image.getWidth())+offset.y;
					int x1 = (wrist_points[1].x*size/image.getWidth())+offset.x;
					int y1 = (wrist_points[1].y*size/image.getWidth())+offset.y;
					g2d.drawLine(x0,y0 ,x1 ,y1 );
				}
				g2d.setColor(Color.BLUE);
				for(int i=0;i<finger_points.length;i++){
					Point p1 = finger_points[i][0];
					Point p2 = finger_points[i][1];
					int x0 = (p1.x*size/image.getWidth())+offset.x;
					int y0 = (p1.y*size/image.getWidth())+offset.y;
					int x1 = (p2.x*size/image.getWidth())+offset.x;
					int y1 = (p2.y*size/image.getWidth())+offset.y;
					g2d.drawLine(x0,y0 ,x1 ,y1 );
				}
				for(int i=0;i<hand_symbol_center.length;i++){
					int x0 = (hand_symbol_center[i].x*size/image.getWidth())+offset.x;
					int y0 = (hand_symbol_center[i].y*size/image.getWidth())+offset.y;
					g2d.drawOval(x0-3,y0-3 , 6, 6);
				}
				for(int i=0;i<fingers.length;i++){
					if(fingers[i].sharp_point != null){
						g2d.setColor(Color.blue);
						int x0 = (fingers[i].joint_point.x*size/image.getWidth())+offset.x;
						int y0  = (fingers[i].joint_point.y*size/image.getWidth())+offset.y;
						g2d.drawOval(x0-3,y0-3 , 6, 6);
						
						int x1 = (fingers[i].sharp_point.x*size/image.getWidth())+offset.x;
						int y1  = (fingers[i].sharp_point.y*size/image.getWidth())+offset.y;
						g2d.drawOval(x1-3,y1-3 , 6, 6);
						int x2 = (fingers[i].center_point.x*size/image.getWidth())+offset.x;
						int y2  = (fingers[i].center_point.y*size/image.getWidth())+offset.y;
						int x3 = (PalmCenter.x*size/image.getWidth())+offset.x;
						int y3  = (PalmCenter.y*size/image.getWidth())+offset.y;
						g2d.drawOval(x2-3,y2-3 , 6, 6);
						g2d.setColor(Color.green);
						g2d.drawLine(x0, y0, x2, y2);
						g2d.drawLine(x2, y2,x1,y1);
						g2d.drawLine(x0, y0, x3, y3);
						if(fingers[i].activated){
							g2d.setColor(Color.magenta);
						}else{
							g2d.setColor(Color.red);
						}
						
						GJGraphics.drawCenteredString(g2d, fingers[i].finger_name, new Rectangle(x1, y1,10,10), new Font("Arial",Font.BOLD,15));
					}
					
					
				}
				g2d.setColor(Color.blue);
				int x0 = (PalmCenter.x*size/image.getWidth())+offset.x;
				int y0 = (PalmCenter.y*size/image.getWidth())+offset.y;
				int x1 = (wrist_center.x*size/image.getWidth())+offset.x;
				int y1 = (wrist_center.y*size/image.getWidth())+offset.y;
				g2d.drawLine(x0, y0, x1, y1);
			
			}
			
		}
		public Point GetCoord(Point p,int size,BufferedImage image){
			int x = 0;
			int y = 0;
			if(hand.raw_height>=hand.raw_width){
				x=(int)(((float)p.x*(float)hand.raw_width/((float)hand.height*(float)hand.raw_width/(float)hand.raw_height)+(float)hand.minbounds.x)*size/(float)image.getWidth());
				y=(int)(((float)p.y*(float)hand.raw_height/hand.height+(float)hand.minbounds.y)*size/(float)image.getHeight())-3;
			}else{
				x=(int)(((float)p.x*(float)hand.raw_width/((float)hand.width)+(float)hand.minbounds.x)*size/(float)image.getWidth())-3;
				y=(int)(((float)p.y*(float)hand.raw_height/((float)hand.width*(float)hand.raw_height/(float)hand.raw_width)+(float)hand.minbounds.y)*size/(float)image.getHeight());
			}
			return new Point(x,y);
		}
	}
	public static class Finger{
		Symbol finger_symbol;
		Point[] finger_points;
		Point center_point;
		Point joint_point;
		Point sharp_point; 
		Hand hand;
		int index=-1;
		boolean activated = false;
		String finger_name="";
		static ArrayList<Integer> index_taken = new ArrayList<Integer>();
		public Finger(Point[] finger_points,Symbol[] finger_symbols,Hand hand){
			this.hand = hand;
			joint_point = GJMATHUTIL.GetCenterPoint(finger_points[0], finger_points[1]);
			float mindist=1000;
			int index=0;
			for(int i=0;i<finger_symbols.length;i++){
				Point symbol_center = GJImageProcess.GetCenterPoint(finger_symbols[i].points);
				Point joint_direction = GJMATHUTIL.SubtractPoint(symbol_center, hand.wrist_center);
				joint_direction = GJMATHUTIL.ScalePoint(joint_direction, 0.8f);
				joint_direction = GJMATHUTIL.AddPoint(joint_direction, hand.wrist_center);
				float dist = GJMATHUTIL.GetDistance(joint_point.x,joint_point.y,joint_direction.x, joint_direction.y);
				if(dist < mindist){
					boolean taken=false;
					innerloop:
					for(int v=0;v<index_taken.size();v++){
						if(i == index_taken.get(v)){
							taken = true;
							break innerloop;
						}
					}
					if(!taken){
						index = i;
						mindist = dist;
						center_point = symbol_center;
						finger_symbol = finger_symbols[i];
					}
				}
			}
			index_taken.add(index);
			if(finger_symbol != null){
				sharp_point = GJImageProcess.GetFarthestPoint(finger_symbol.points, joint_point);
				GetIndex(hand.palm_center,hand.wrist_center);
			}
			
		
		}
		public void GetIndex(Point palm_center,Point wrist_center){
			Point ref_vec = GJMATHUTIL.SubtractPoint(palm_center, wrist_center);
			Point joint_vec = GJMATHUTIL.SubtractPoint(joint_point, palm_center);
			float angle = GJMATHUTIL.GetAngleDeg(joint_vec, ref_vec);
			this.finger_name = String.format("%.0f", angle);
			if(angle > -100 && angle < -50){
				finger_name = "THUMB";
				index=0;
			}else if(angle > -50 && angle < -15){
				finger_name = "1";
				index=1;
			}else if(angle > -15 && angle < 0){
				finger_name = "2";
				index=2;
			}else if(angle > 0 && angle < 40){
				finger_name = "3";
				index=3;
			}else if(angle > 40 && angle < 50){
				finger_name = "4";
				index=4;
			}else if(angle > -360 && angle < -290){
				finger_name = "4";
				index=4;
			}
			if(index != -1){
				Point act_vec = GJMATHUTIL.SubtractPoint(sharp_point, joint_point);
				if(GJMATHUTIL.GetDistance(act_vec.xf, act_vec.yf, 0, 0) > hand.PalmRadius){
					activated=true;
				}
			}
		}
	}
	public static class button1action implements GJAction{

		@Override
		public void Clicked(int x,int y) {
			// TODO Auto-generated method stub
			Capture();
		}

		@Override
		public void Pressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Release() {
			// TODO Auto-generated method stub
			
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
	public static class button2action implements GJAction{

		@Override
		public void Clicked(int x,int y) {
			// TODO Auto-generated method stub
			Color[] colors_array = new Color[colors.size()];
			colors_array = colors.toArray(colors_array);
			main.image1 = GJImageTune.FilterImage(colors_array, capture_image,0.15f,0.1f,0.2f);
			window1.render();
		}

		@Override
		public void Pressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Release() {
			// TODO Auto-generated method stub
			
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
	public static class button3action implements GJAction{

		@Override
		public void Clicked(int x,int y) {
			
			colors_array = new Color[0];
			colors.clear();
			captureg.points.clear();
		}

		@Override
		public void Pressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Release() {
			// TODO Auto-generated method stub
			
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
	public static class button4action implements GJAction{

		@Override
		public void Clicked(int x,int y) {
			if(COLOR_DIRECTORY != null){
				String text = "";
				for(int i=0;i<colors_array.length;i++){
					if(i!=0){
						text+=":";
					}
					text += colors_array[i].getRGB();
				}
				try {
					GJFileLoader.SaveText(text, COLOR_DIRECTORY);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void Pressed() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Release() {
			// TODO Auto-generated method stub
			
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
	public static void LoadColor(){
		String data = GJFileLoader.LoadText(COLOR_DIRECTORY);
		if(data != null){
			String[] RGBColors = data.split(":");
			for(int i=0;i<RGBColors.length;i++){
				Color c = new Color(Integer.parseInt(RGBColors[i]));
				colors.add(c);
				colors_array = new Color[colors.size()];
				colors_array = colors.toArray(colors_array);
			}
		}
	}
}
