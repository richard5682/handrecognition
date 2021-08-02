package GJIMAGE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GJIMAGE.GJImageTune.Point;
import GJMATH.GJMATHUTIL;

public class GJImageProcess {
	public static Point GetFarthestPoint(Point[] p,Point ref){
		float maxdist=0;
		int index=0;
		for(int i=0;i<p.length;i++){
			float dist = GJMATHUTIL.GetDistance(p[i].x, p[i].y, ref.x, ref.y);
			if(dist>maxdist){
				maxdist = dist;
				index=i;
			}
		}
		return p[index];
	}
	public static Point GetCenterPoint(Point[] p){
		Point buffer = new Point(0,0);
		int counter=0;
		for(int i=0;i<p.length;i++){
			buffer.xf += p[i].x;
			buffer.yf += p[i].y;
			buffer.x += p[i].x;
			buffer.y += p[i].y;
			counter++;
		}
		if(counter > 0){
			buffer.xf = buffer.xf/counter;
			buffer.yf = buffer.yf/counter;
			buffer.x = (buffer.x/counter);
			buffer.y = (buffer.y/counter);
			return buffer;
		}else{
			return null;
		}
	}
	public static Point GetCenterImage(BufferedImage image,int minval){
		Point buffer = new Point(0,0);
		int counter=0;
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				if(c.getRed() > minval){
					buffer.xf += x;
					buffer.yf += y;
					buffer.x += x;
					buffer.y += y;
					counter++;
				}
			}
		}
		if(counter > 0){
			buffer.xf = buffer.xf/counter;
			buffer.yf = buffer.yf/counter;
			buffer.x = buffer.x/counter;
			buffer.y = buffer.y/counter;
			return buffer;
		}else{
			return null;
		}
	}
	public static BufferedImage SubtractImage(BufferedImage image1, BufferedImage image2){
		BufferedImage buffer = new BufferedImage(image1.getWidth(),image1.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image1.getWidth();x++){
			for(int y=0;y<image1.getHeight();y++){
				Color c1 = new Color(image1.getRGB(x, y));
				Color c2 = new Color(image2.getRGB(x, y));
				int red = c1.getRed()-c2.getRed();
				int green = c1.getGreen()-c2.getGreen();
				int blue = c1.getBlue()-c2.getBlue();
				if(green > 255){
					green = 255;
				}else if(green < 0){
					green = 0;
				}
				if(red > 255){
					red = 255;
				}else if(red < 0){
					red = 0;
				}
				if(blue > 255){
					blue = 255;
				}else if(blue < 0){
					blue = 0;
				}
				buffer.setRGB(x, y, new Color(red,green,blue).getRGB());
			}
		}
		return buffer;
	}
	public static float GetMinimumRadius(BufferedImage image,Point p){
		float minimum = 1000;
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				if(c.getRed() > 200){
					float distance = GJMATHUTIL.GetDistance(x, y, p.x, p.y);
					if(distance < minimum){
						minimum = distance;
					}
				}
			}
		}
		return minimum;
	}
	public static Point GetMinimumPoint(BufferedImage image,Point p){
		float minimum = 1000;
		Point buffer = null;
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				if(c.getRed() > 200){
					float distance = GJMATHUTIL.GetDistance(x, y, p.x, p.y);
					if(distance < minimum){
						minimum = distance;
						buffer = new Point(x,y);
					}
				}
			}
		}
		return buffer;
	}
	public static Point[] GetCirclePoints(Point center,float radius,float samplingsize){
		ArrayList<Point> points = new ArrayList<Point>();
		float teta = 0;
		while(teta<=360){
			int x = (int)(radius*Math.cos(teta*Math.PI/180)+center.x);
			int y= (int)(radius*Math.sin(teta*Math.PI/180)+center.y);
			boolean existing = false;
			innerloop:
			for(int i=0;i<points.size();i++){
				if(points.get(i).x == x && points.get(i).y == y){
					existing = true;
					break innerloop;
				}
			}
			if(!existing){
				points.add(new Point(x,y));
			}
			teta+=samplingsize;
		}
		Point[] buffer = new Point[points.size()];
		buffer = points.toArray(buffer);
		return buffer;
		
	}
	public static Point[] GetEnclosingBound(BufferedImage contourimage,Point[] guide){
		ArrayList<Point> points = new ArrayList<Point>();
		for(int i=0;i<guide.length;i++){
			Point point = GetMinimumPoint(contourimage,guide[i]);
			boolean existing = false;
			for(int v=0;v<points.size();v++){
				if(points.get(v).xf == point.xf && points.get(v).yf == point.yf){
					existing = true;
					break;
				}
			}
			if(!existing){
				points.add(point);
			}
		}
		Point[] polygon = new Point[points.size()];
		points.toArray(polygon);
		return polygon;
	}
}
