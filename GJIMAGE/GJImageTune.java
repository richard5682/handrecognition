package GJIMAGE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import GJFILE.GJFileLoader;
import GJMATH.GJMATHUTIL;
import GJNEURAL.GJTrainerUtil;
import GJNEURAL.GJNeural.Organism;

public class GJImageTune {
	public static class Point{
		public int x;
		public int y;
		public float xf;
		public float yf;
		public Point(int x,int y){
			this.x = x;
			this.y = y;
			this.xf = x;
			this.yf = y;
		}
		public Point(float x,float y){
			this.xf = x;
			this.yf = y;
			this.x = (int)x;
			this.y = (int)y;
		}
	}
	public static class Symbol{
		public Point[] points;
		public Point[] raw_points;
		public Point minbounds;
		public Point maxbounds;
		public int width=0,height=0,raw_width=0,raw_height=0;
		public String value;
		float ratio;
		public Symbol(Point[] points){
			this.points = points;
			this.raw_points = new Point[points.length];
			for(int i=0;i<points.length;i++){
				raw_points[i] = new Point(points[i].x,points[i].y);
			}
			for(int i=0;i<points.length;i++){
				if(i==0){
					maxbounds = new Point(points[i].x,points[i].y);
					minbounds = new Point(points[i].x,points[i].y);
				}
				if(points[i].x > maxbounds.x){
					maxbounds.x = points[i].x;
				}
				if(points[i].y > maxbounds.y){
					maxbounds.y = points[i].y;
				}
				if(points[i].x < minbounds.x){
					minbounds.x = points[i].x;
				}
				if(points[i].y < minbounds.y){
					minbounds.y = points[i].y;
				}
			}
			width = maxbounds.x - minbounds.x;
			height = maxbounds.y - minbounds.y;
			raw_width = width;
			raw_height = height;
			ratio = (float)height/width;
		}
		public BufferedImage ToBufferImage(){
			int dimension = 0;
			if(width > height){
				dimension = width+1;
			}else{
				dimension = height+1;
			}
			BufferedImage buffer = new BufferedImage(dimension,dimension,BufferedImage.TYPE_3BYTE_BGR);
			for(int x=0;x<dimension;x++){
				for(int y=0;y<dimension;y++){
					buffer.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			for(int i=0;i<points.length;i++){
				buffer.setRGB(points[i].x, points[i].y, Color.WHITE.getRGB());
			}
			return buffer;
		}
		public void ComputeSymbol(Organism organism,int size,String[] output){
			BufferedImage buffer = this.ToBufferImage();
			buffer = InverseImage(buffer);
			organism.ComputeOutput(GJTrainerUtil.ProcessImage(buffer));
			float value=0;
			int index=0;
			for(int i=0;i<organism.neuralnetwork.output_layer.nodes.length;i++){
				float bufferval = organism.neuralnetwork.output_layer.nodes[i].value;
				if(bufferval > value){
					value = bufferval;
					index = i;
				}
			}
			this.value = output[index];
		}
		public void SaveSymbol(String directory){
			BufferedImage buffer = this.ToBufferImage();
			buffer = InverseImage(buffer);
				int v=0;
				File f = new File(directory+"//"+v+".png");
				while(f.exists()){
					v++;
					f = new File(directory+"//"+v+".png");
				}
				GJFileLoader.SaveImage(buffer, directory+"//"+v+".png");
		}
		public static Symbol CombineSymbols(Symbol[] symbols){
			ArrayList<Point> points = new ArrayList<Point>();
			String value = "";
			for(int i=0;i<symbols.length;i++){
				value+=symbols[i].value;
//				System.out.println(value);
				for(int v=0;v<symbols[i].raw_points.length;v++){
					points.add(symbols[i].raw_points[v]);
				}
			}
			Point[] buffer = new Point[points.size()];
			buffer = points.toArray(buffer);
			Symbol s = new Symbol(buffer);
			s.value = value;
			return s;
		}
		public static Symbol[][] GetSymbolsGroup(Symbol[] symbols){
			ArrayList<Symbol[]> buffergroup = new ArrayList<Symbol[]>();
			ArrayList<Symbol[]> LineSymbols = new ArrayList<Symbol[]>();
			ArrayList<Symbol> ArraySymbols = new ArrayList<Symbol>();
			int average_height=0;
			int average_width=0;
			int cheight=0;
			for(int i=0;i<symbols.length;i++){
				ArraySymbols.add(symbols[i]);
				average_height += symbols[i].height;
				average_width += symbols[i].width;
			}
			average_height = (int)Math.floor((float)average_height/symbols.length);
			average_width = (int)Math.floor((float)average_width/symbols.length);
			if(ArraySymbols.size()>0)
			cheight = ArraySymbols.get(0).minbounds.y;
			for(int i=0;i<ArraySymbols.size();i++){
				if(ArraySymbols.get(i).minbounds.y < cheight){
					cheight = ArraySymbols.get(i).minbounds.y;
				}
			}
			cheight += average_height;
			while(ArraySymbols.size()>0){
				ArrayList<Symbol> LineSymbol = new ArrayList<Symbol>();
				boolean found = true;
				while(found){
					found = false;
					for(int i=0;i<ArraySymbols.size();i++){
						if(ArraySymbols.get(i).minbounds.y <= cheight){
							LineSymbol.add(ArraySymbols.get(i));
							ArraySymbols.remove(i);
							found = true;
							break;
						}
					}
				}
				
				if(LineSymbol.size() > 0){
					Symbol[] buffer_symbol = new Symbol[LineSymbol.size()];
					buffer_symbol = LineSymbol.toArray(buffer_symbol);
					LineSymbols.add(buffer_symbol);
				}
				cheight += average_height*1f;
				
			}
			for(int i=0;i<LineSymbols.size();i++){
				ArrayList<Symbol> horizontalsymbol = new ArrayList<Symbol>();
				ArrayList<Symbol> linesymbols = new ArrayList<Symbol>();
				for(int v=0;v<LineSymbols.get(i).length;v++){
					linesymbols.add(LineSymbols.get(i)[v]);
				}
				while(linesymbols.size() > 0){
					int minimumx = linesymbols.get(0).minbounds.x;
					int minindex = 0;
					for(int v=0;v<linesymbols.size();v++){
						if(linesymbols.get(v).minbounds.x < minimumx){
							minimumx = linesymbols.get(v).minbounds.x;
							minindex = v;
						}
					}
					horizontalsymbol.add(linesymbols.get(minindex));
					linesymbols.remove(minindex);
				}
				while(horizontalsymbol.size() > 0){
					ArrayList<Symbol> buffersymbol = new ArrayList<Symbol>();
					int lastminx = horizontalsymbol.get(0).minbounds.x;
					buffersymbol.add(horizontalsymbol.get(0));
					horizontalsymbol.remove(0);
					if(horizontalsymbol.size() > 0){
						int difference = horizontalsymbol.get(0).minbounds.x - lastminx;
						while(difference < average_width*1.5f){
							buffersymbol.add(horizontalsymbol.get(0));
							lastminx = horizontalsymbol.get(0).minbounds.x;
							horizontalsymbol.remove(0);
							if(horizontalsymbol.size() == 0){
								break;
							}
							difference = horizontalsymbol.get(0).minbounds.x - lastminx;
						}
					}
					Symbol[] b1 = new Symbol[buffersymbol.size()];
					b1 = buffersymbol.toArray(b1);
					buffergroup.add(b1);
				}
			}
//			System.out.println();
//			for(int i=0;i<buffergroup.size();i++){
//				System.out.println();
//				for(int v=0;v<buffergroup.get(i).length;v++){
//					System.out.print(buffergroup.get(i)[v].value);
//				}
//			}
			Symbol[][] buffer = new Symbol[buffergroup.size()][];
			buffer = buffergroup.toArray(buffer);
			return buffer;
		}
		public static Symbol ScaleSymbol(Symbol symbol,int height){
			ArrayList<Point> points = new ArrayList<Point>();
			Point[] symbol_points = symbol.points;
			float ratio;
			if(symbol.height > symbol.width){
				ratio = (float)height/symbol.height;
				symbol.width = (int)Math.round(ratio*symbol.width);
				symbol.height = height;
			}else{
				ratio = (float)height/symbol.width;
				symbol.height = (int)Math.round(ratio*symbol.width);
				symbol.width = height;
			}
			for(int i=0;i<symbol_points.length;i++){
				Point b_point = symbol_points[i];
				b_point.x -= symbol.minbounds.x;
				b_point.y -= symbol.minbounds.y;
				b_point.x = Math.round((float)b_point.x*ratio);
				b_point.y = Math.round((float)b_point.y*ratio);
				boolean existing=false;
				innerloop:
				for(int v=0;v<points.size();v++){
					if(points.get(v).x == b_point.x && points.get(v).y == b_point.y){
						existing=true;
						break innerloop;
					}
				}
				if(!existing){
					points.add(new Point(b_point.x,b_point.y));
				}
			}
			Point[] new_point = new Point[points.size()];
			new_point = points.toArray(new_point);
			return new Symbol(new_point);
		}
		public boolean FilterSymbol(int minwidth,int maxwidth,int minheight,int maxheight,float minratio,float maxratio,int minarea){
			if(width <= minwidth || width >= maxwidth){
				return false;
			}
			if(height <= minheight || height >= maxheight){
				return false;
			}
			if(ratio < minratio || ratio > maxratio){
				return false;
			}
			if(points.length < minarea){
				return false;
			}
			return true;
		}
		public static Symbol[] FilterSymbols(Symbol[] symbol,int minwidth,int maxwidth,int minheight,int maxheight,int minarea,float minratio,float maxratio){
			ArrayList<Symbol> symbols = new ArrayList<Symbol>();
			for(int i=0;i<symbol.length;i++){
				if(symbol[i].FilterSymbol(minwidth,maxwidth, minheight,maxheight, minratio, maxratio,minarea)){
					symbols.add(symbol[i]);
				}
			}
			Symbol[] buffer = new Symbol[symbols.size()];
			buffer = symbols.toArray(buffer);
			return buffer;
		}
	}
	public static BufferedImage CropImage(BufferedImage image,Symbol s,int offset){
			BufferedImage buffer = new BufferedImage(s.width,s.height,BufferedImage.TYPE_3BYTE_BGR);
			for(int x=0;x<s.width;x++){
				for(int y=0;y<s.height;y++){
					if(x<offset || y<offset || x>s.width-offset || y > s.height -offset){
						buffer.setRGB(x,y , Color.black.getRGB());
					}else{
						buffer.setRGB(x,y , image.getRGB(x+s.minbounds.x, y+s.minbounds.y));
					}
				}
			}
			return buffer;
	}
	public static BufferedImage CropImagePolygon(BufferedImage image,Point[] points,Point center){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		Point[] mask = GetPolygonMask(points,image,center);
		for(int i=0;i<mask.length;i++){
			//buffer.setRGB(mask[i].x, mask[i].y, image.getRGB(mask[i].x, mask[i].y));
			buffer.setRGB(mask[i].x, mask[i].y, Color.white.getRGB());
		}
		return buffer;
	}
	public static Point[] GetPolygonMask(Point[] polygon,BufferedImage image,Point center){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int i=0;i<polygon.length;i++){
			Point p1 = polygon[i];
			Point p2 = null;
			if(i == 0){
				p2 = polygon[polygon.length-1];
			}else{
				p2 = polygon[i-1];
			}
			float m=0,b=0,no_pixel;
			if(p2.x-p1.x != 0){
				float ix= (p2.x-p1.x);
				m = (float)(p2.y-p1.y)/(p2.x-p1.x);
				b = p2.y-(m*p2.x);
				no_pixel = (int)Math.ceil(GJMATHUTIL.GetDistance(p1.x, p1.y, p2.x, p2.y));
				float dx = ix/(float)no_pixel;
					for(int x=0;x<=no_pixel;x++){
						int y = Math.round(m*(p1.x+(x*dx))+b);
						buffer.setRGB(Math.round(p1.x+(x*dx)), y, Color.white.getRGB());
					}
				
			}else{
				no_pixel = p2.y-p1.y;
				if(p2.y >= p1.y){
					for(int y=0;y<=Math.abs(no_pixel);y++){
						buffer.setRGB(p1.x, p1.y+y, Color.white.getRGB());
					}
				}else{
					for(int y=0;y<=Math.abs(no_pixel);y++){
						buffer.setRGB(p1.x, p2.y+y, Color.white.getRGB());
					}
				}
				
			}
		}
		Point[] mask = FloodFill(buffer,center.x,center.y);
		Point[] buffereturn = new Point[mask.length+polygon.length];
		for(int i=0;i<mask.length;i++){
			buffereturn[i] = mask[i];
		}
		for(int i=0;i<polygon.length;i++){
			buffereturn[i+mask.length] = polygon[i];
		}
		return buffereturn;
	}
	
	public static BufferedImage CropImageCircle(BufferedImage image,Point center,float radius,float margin,boolean inverse){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				if(c.getRed() > 200){
					float distance = GJMATHUTIL.GetDistance(x, y, center.x, center.y);
					if(!inverse){
						if(distance <= radius*(1+margin)){
							buffer.setRGB(x, y, c.getRGB());
						}
					}else{
						if(distance >= radius*(1+margin)){
							buffer.setRGB(x, y, c.getRGB());
						}
					}
					
				}
			}
		}
		return buffer;
	}
	public static Symbol[] GetSymbols(BufferedImage image){
		ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		//image must be processed
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				buffer.setRGB(x, y, image.getRGB(x, y));
			}
		}
		outerloop:
		for(int x=0;x<buffer.getWidth();x++){
			for(int y=0;y<buffer.getHeight();y++){
				Color c = new Color(buffer.getRGB(x, y));
				int value = c.getRed();
				//CHECK IF ACTIVATED
				if(value > 200){
					Symbol buffersymbol = GetSymbol(buffer,x,y);
					for(int v=0;v<buffersymbol.points.length;v++){
						buffer.setRGB(buffersymbol.points[v].x, buffersymbol.points[v].y, Color.BLACK.getRGB());
					}
					symbols.add(buffersymbol);
				}
				
			}
		}
		Symbol[] buffersymbols = new Symbol[symbols.size()];
		buffersymbols = symbols.toArray(buffersymbols);
		return buffersymbols;
	}
	public static Symbol GetSymbol(BufferedImage image,int x,int y){
		Point[] bufferpoint = FloodFill(image,x,y);
		return new Symbol(bufferpoint);
	}
	public static Point[] FloodFill(BufferedImage image,int tx,int ty){
		int[][] image_data = new int[image.getWidth()][image.getHeight()];
		int setval = new Color(image.getRGB(tx, ty)).getRed();
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				int val = new Color(image.getRGB(x, y)).getRed();
				if(Math.abs(setval-val) < 60){
					image_data[x][y] = 1;
				}else{
					image_data[x][y] = 0;
				}
			}
		}
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Point> queue_points = new ArrayList<Point>();
		queue_points.add(new Point(tx,ty));
		while(queue_points.size() > 0){
			Point p = queue_points.get(0);
			if(p.x+1 < image.getWidth() && image_data[p.x+1][p.y] == 1){
				queue_points.add(new Point(p.x+1,p.y));
				image_data[p.x+1][p.y] = 2;
			}
			if(p.x-1 >= 0 && image_data[p.x-1][p.y] == 1){
				queue_points.add(new Point(p.x-1,p.y));
				image_data[p.x-1][p.y] = 2;
			}
			if(p.y+1 < image.getHeight() && image_data[p.x][p.y+1] == 1){
				queue_points.add(new Point(p.x,p.y+1));
				image_data[p.x][p.y+1] = 2;
			}
			if(p.y-1 >= 0 && image_data[p.x][p.y-1] == 1){
				queue_points.add(new Point(p.x,p.y-1));
				image_data[p.x][p.y-1] = 2;
			}
			image_data[p.x][p.y] = 0;
			points.add(p);
			queue_points.remove(0);
		}
		Point[] buffer_points = new Point[points.size()];
		buffer_points = points.toArray(buffer_points);
		return buffer_points;
	}
	public static BufferedImage ReDrawImage(BufferedImage image,Symbol[] symbols,boolean drawSymbol,Point offset){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<buffer.getWidth();x++){
			for(int y=0;y<buffer.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				buffer.setRGB(x, y, c.getRGB());
			}
		}
		Random r = new Random();
		for(int v=0;v<symbols.length;v++){
			if(drawSymbol){
				Color c = new Color(r.nextInt(155)+100,r.nextInt(155)+100,r.nextInt(155)+100);
				for(int i=0;i<symbols[v].points.length;i++){
					buffer.setRGB(symbols[v].points[i].x, symbols[v].points[i].y, c.getRGB());
				}
			}
			//DrawBoundaries(buffer,symbols[v].maxbounds,symbols[v].minbounds,offset);
		}
		return buffer;
	}
	public static BufferedImage ReDrawImage(BufferedImage image,Symbol symbols,boolean drawSymbol,Point offset){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<buffer.getWidth();x++){
			for(int y=0;y<buffer.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				buffer.setRGB(x, y, c.getRGB());
			}
		}
		Random r = new Random();
			if(drawSymbol){
				Color c = new Color(r.nextInt(155)+100,r.nextInt(155)+100,r.nextInt(155)+100);
				for(int i=0;i<symbols.points.length;i++){
					buffer.setRGB(symbols.points[i].x, symbols.points[i].y, c.getRGB());
				}
			}
			DrawBoundaries(buffer,symbols.maxbounds,symbols.minbounds,offset);
		return buffer;
	}
	public static void DrawBoundaries(BufferedImage image,Point maxbound,Point minbound,Point offset){
		Point minbound1 = new Point(minbound.x + offset.x,minbound.y + offset.y);
		Point maxbound1 = new Point(maxbound.x + offset.x,maxbound.y + offset.y);
		for(int x=minbound1.x;x<=maxbound1.x;x++){
			image.setRGB(x, minbound1.y, Color.red.getRGB());
			image.setRGB(x, maxbound1.y, Color.red.getRGB());
			if(maxbound1.y+1 < image.getHeight()){
				image.setRGB(x, maxbound1.y+1, Color.red.getRGB());
			}
			if(minbound1.y+1 < image.getHeight()){
				image.setRGB(x, minbound1.y+1, Color.red.getRGB());
			}
		}
		for(int y=minbound1.y;y<=maxbound1.y;y++){
			image.setRGB(minbound1.x,y, Color.red.getRGB());
			image.setRGB(maxbound1.x,y, Color.red.getRGB());
			
			
			if(maxbound1.x+1 < image.getWidth()){
				image.setRGB(maxbound1.x+1,y, Color.red.getRGB());
				
			}
			if(minbound1.x+1 < image.getWidth()){
				image.setRGB(minbound1.x+1,y, Color.red.getRGB());
			}
		}
	}
	public static BufferedImage DistanceTransform(BufferedImage image,int radius){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				float value=0;
				for(int xi=-radius;xi<radius;xi++){
					for(int yi=-radius;yi<radius;yi++){
						if(x+xi < image.getWidth() && x+xi >= 0 &&
							y+yi < image.getHeight() && y+yi >= 0){
							Color c = new Color(image.getRGB(x+xi, y+yi));
							value+=c.getRed();
						}
						
					}
				}
				value = value/(4*radius*radius);
				value = (float) Math.exp(5*(value/255)-5);
				int newval = (int)(value*255);
				buffer.setRGB(x, y, new Color(newval,newval,newval).getRGB());
			}
		}
		return buffer;
	}

	public static BufferedImage SetBrightness(BufferedImage image,float factor){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				buffer.setRGB(x, y, new Color(MinFactor(c.getRed(),factor),MinFactor(c.getGreen(),factor),MinFactor(c.getBlue(),factor)).getRGB());
			}
		}
		return buffer;
	}
	public static BufferedImage SetContrast(BufferedImage image,float factor,float factor1){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		int MaximumValue = 0;
		int MinimumValue = 255;
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int value = MinFactor((c.getRed()+c.getBlue()+c.getGreen())/3,1);
				if(value > MaximumValue){
					MaximumValue = value;
				}
				if(value < MinimumValue){
					MinimumValue = value;
				}
			}
		}
		float conval = (factor*((float)1/(MaximumValue+MinimumValue)/(2*factor1)));
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int value = MinFactor((c.getRed()+c.getBlue()+c.getGreen())/3,1);
				value = MinFactor(value,value*conval);
				buffer.setRGB(x, y, new Color(value,value,value).getRGB());
			}
		}
		return buffer;
	}
	public static BufferedImage BlackAndWhite(BufferedImage image){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int value = MinFactor((c.getRed()+c.getBlue()+c.getGreen())/3,1);
				buffer.setRGB(x, y, new Color(value,value,value).getRGB());
			}
		}
		return buffer;
	}
	public static BufferedImage ErodeImage(BufferedImage image,float iteration,int radius,float threshold){
		BufferedImage buffer_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int v=0;v<iteration;v++){
			BufferedImage buffer = buffer_image;
			if(v==0){
				buffer = image;
			}
			
			for(int x=0;x<image.getWidth();x++){
				for(int y=0;y<image.getHeight();y++){
					int value = 0;
					int i=0;
					for(int ix=-radius;ix<1+radius;ix++){
						for(int iy=-radius;iy<1+radius;iy++){
							int xval = x+ix;
							int yval = y+iy;
							if(xval >= 0 && yval >=0 && xval < image.getWidth() && yval < image.getHeight()){
								Color c = new Color(buffer.getRGB(xval, yval));
								value += c.getRed();
								i++;
							}
						}
					}
					value = (int)Math.floor((float)value/i);
					if(value < threshold){
						value = 0;
					}
					buffer_image.setRGB(x, y, new Color(value,value,value).getRGB());
				}
			}
		}
		return buffer_image;
	}
	public static BufferedImage InverseImage(BufferedImage image){
		BufferedImage buffer_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				buffer_image.setRGB(x, y, new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue()).getRGB());
			}
		}
		return buffer_image;
	}
	public static BufferedImage DilateImage(BufferedImage image,float iteration,int radius,float threshold){
		BufferedImage buffer_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int v=0;v<iteration;v++){
			BufferedImage buffer = buffer_image;
			if(v==0){
				buffer = InverseImage(image);
			}
			for(int x=0;x<image.getWidth();x++){
				for(int y=0;y<image.getHeight();y++){
					int value = 0;
					int i=0;
					for(int ix=-radius;ix<1+radius;ix++){
						for(int iy=-radius;iy<1+radius;iy++){
							int xval = x+ix;
							int yval = y+iy;
							if(xval >= 0 && yval >=0 && xval < image.getWidth() && yval < image.getHeight()){
								Color c = new Color(buffer.getRGB(xval, yval));
								value += c.getRed();
								i++;
							}
						}
					}
					value = (int)Math.floor((float)value/i);
					if(value < threshold){
						value = 0;
					}
					buffer_image.setRGB(x, y, new Color(value,value,value).getRGB());
				}
			}
		}
		return  InverseImage(buffer_image);
	}
	public static BufferedImage BinarizeImage(BufferedImage image,float factor,int width,int height){
		BufferedImage buffer_image = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		int MaximumValue = 0;
		int MinimumValue = 255;
		int startposw = (image.getWidth()-width)/2;
		int startposh = (image.getHeight()-height)/2;
		for(int x=startposw;x<startposw+width;x++){
			for(int y=startposh;y<startposh+height;y++){
				Color c = new Color(image.getRGB(x, y));
				int value = MinFactor((c.getRed()+c.getBlue()+c.getGreen())/3,1);
				if(value > MaximumValue){
					MaximumValue = value;
				}
				if(value < MinimumValue){
					MinimumValue = value;
				}
			}
		}
		
		int threshold = Math.round((float)(MinimumValue+1)/(factor));
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int value = MinFactor((c.getRed()+c.getBlue()+c.getGreen())/3,1);
				if(value <= threshold){
					buffer_image.setRGB(x, y, new Color(255,255,255).getRGB());
				}else{
					buffer_image.setRGB(x, y, new Color(0,0,0).getRGB());
				}
				
			}
		}
		return buffer_image;
	}
	public static BufferedImage FilterImage(Color[] colors,BufferedImage image,float rangeh,float ranges,float rangev){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
				float hue = hsb[0];
				float sat = hsb[1];
				float val = hsb[2];
				boolean passed = false;
				for(int i=0;i<colors.length;i++){;
					float[] hsb1 = Color.RGBtoHSB(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), null);
					float hue1 = hsb1[0];
					float sat1 = hsb1[1];
					float val1 = hsb1[2];
					if(Math.abs(hue-hue1) < rangeh && Math.abs(sat-sat1) < ranges && Math.abs(val-val1) < rangev){
						passed = true;
						break;
					}
				}
				if(passed){
					buffer.setRGB(x, y, c.getRGB());
				}else{
					buffer.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		return buffer;
	}
	public static BufferedImage GetCoutour(BufferedImage image){
		BufferedImage buffer = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				Color c = new Color(image.getRGB(x, y));
				int val = c.getRed();
				if(val > 200){
					if(x-1 >= 0){
						int val1 = new Color(image.getRGB(x-1, y)).getRed();
						if(val1 < 200){
							buffer.setRGB(x, y, Color.white.getRGB());
						}
					}else{
						buffer.setRGB(x, y, Color.white.getRGB());
					}
					if(y-1 >= 0){
						int val2 = new Color(image.getRGB(x, y-1)).getRed();
						if(val2 < 200){
							buffer.setRGB(x, y, Color.white.getRGB());
						}
					}else{
						buffer.setRGB(x, y, Color.white.getRGB());
					}
					if(y+1 < image.getHeight()){
						int val3 = new Color(image.getRGB(x, y+1)).getRed();
						if(val3 < 200){
							buffer.setRGB(x, y, Color.white.getRGB());
						}
					}else{
						buffer.setRGB(x, y, Color.white.getRGB());
					}
					if(x+1 < image.getWidth()){
						int val4 = new Color(image.getRGB(x+1, y)).getRed();
						if(val4 < 200){
							buffer.setRGB(x, y, Color.white.getRGB());
						}
					}else{
						buffer.setRGB(x, y, Color.white.getRGB());
					}
				}
			}
		}
		return buffer;
	}
	public static int MinFactor(float c,float factor){
		float buffer = c*factor;
		if(buffer > 255){
			buffer = 255;
		}else if(buffer < 0){
			buffer = 0;
		}
		return (int)Math.round(buffer);
	}
}
