package GJNEURAL;

import java.awt.Color;
import java.awt.image.BufferedImage;

import GJMATH.GJMATHUTIL;

public class GJTrainerUtil {
	public static class Filter{
		float[][] weights;
		float bias;
		public Filter(float[][] weights,float bias){
			this.weights = weights;
			this.bias = bias;
		}
		public float DotFilter(float[][] image){
			float sum=0;
			for(int x=0;x<weights.length;x++){
				for(int y=0;y<weights[0].length;y++){
					sum += weights[y][x] * image[x][y];
				}
			}
			return GJMATHUTIL.sigmoid(sum+bias);
		}
		public float DotFilterU(float[][] image){
			float sum=0;
			for(int x=0;x<weights.length;x++){
				for(int y=0;y<weights[0].length;y++){
					sum += weights[y][x] * image[x][y];
				}
			}
			return GJMATHUTIL.RelU(sum+bias);
		}
	}
	public static float[] ProcessImage(BufferedImage raw_image){
		float[][] image0,image,image1;
		image0 = GJTrainerUtil.CenterImage(raw_image);
		image = GJTrainerUtil.MaxPooling(GJTrainerUtil.FilterImage(GJTrainer.VERTICALFILTER, image0));
		image1 = GJTrainerUtil.MaxPooling(GJTrainerUtil.FilterImage(GJTrainer.HORIZONTALFILTER, image0));
		float[][][] images = {image,image1};
		return GJTrainer.ConvertImage(images);
	}
	public static float[][] CenterImage(BufferedImage image){
		int minx=0;
		int maxx=image.getWidth();
		int miny=0;
		int maxy=image.getHeight();
		outerloop:
		for(int x=0;x<image.getWidth();x++){
			for(int y=0;y<image.getHeight();y++){
				if(255-new Color(image.getRGB(x, y)).getRed() > 100){
					minx = x;
					break outerloop;
				}
			}
		}
		outerloop:
		for(int x=image.getWidth()-1;x>=0;x--){
			for(int y=0;y<image.getHeight();y++){
				
				if(255-new Color(image.getRGB(x, y)).getRed() > 100){
					maxx = x;
					break outerloop;
				}
			}
		}
		outerloop:
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				if(255-new Color(image.getRGB(x, y)).getRed() > 100){
					miny = y;
					break outerloop;
				}
			}
		}
		outerloop:
		for(int y=image.getHeight()-1;y>=0;y--){
			for(int x=0;x<image.getWidth();x++){
				if(255-new Color(image.getRGB(x, y)).getRed() > 100){
					maxy = y;
					break outerloop;
				}
			}
		}
		float[][] imagedata = new float[image.getWidth()][image.getHeight()];
		int startx = ((image.getWidth()-maxx + minx)/2);
		int starty = ((image.getHeight()-maxy + miny)/2);
		int endx = image.getWidth()-startx;
		int endy = image.getHeight()-starty;
		for(int x=startx;x<endx;x++){
			int rx = minx+x-startx;
			for(int y=starty;y<endy;y++){
				int ry = miny+y-starty;
				imagedata[x][y] = (255-new Color(image.getRGB(rx, ry)).getRed())/255f;
			}
		}
		return imagedata;
	}
	public static BufferedImage ToBufferImage(float[][] data){
		BufferedImage buffer = new BufferedImage(data.length,data[0].length,BufferedImage.TYPE_3BYTE_BGR);
		for(int x=0;x<data.length;x++){
			for(int y=0;y<data[0].length;y++){
				int value = (int) (data[x][y]*255);
				Color c = new Color(value,value,value);
				buffer.setRGB(x, y, c.getRGB());
			}
		}
		return buffer;
	}
	public static float[][] FilterImageU(Filter filter,BufferedImage image){
		float[][] result = new float[image.getWidth()-2][image.getHeight()-2];
		
		for(int x=0;x<image.getWidth()-2;x++){
			for(int y=0;y<image.getHeight()-2;y++){
				float[][] scope = new float[3][3];
				for(int ix=0;ix<3;ix++){
					for(int iy=0;iy<3;iy++){
						scope[ix][iy] = (255-new Color(image.getRGB(x+ix, y+iy)).getRed())/255f;
					}
				}
				result[x][y] = filter.DotFilterU(scope);
			}
		}
		return result;
	}
	public static float[][] FilterImage(Filter filter,BufferedImage image){
		float[][] result = new float[image.getWidth()-2][image.getHeight()-2];
		
		for(int x=0;x<image.getWidth()-2;x++){
			for(int y=0;y<image.getHeight()-2;y++){
				float[][] scope = new float[3][3];
				for(int ix=0;ix<3;ix++){
					for(int iy=0;iy<3;iy++){
						scope[ix][iy] = (255-new Color(image.getRGB(x+ix, y+iy)).getRed())/255f;
					}
				}
				result[x][y] = filter.DotFilter(scope);
			}
		}
		return result;
	}
	public static float[][] FilterImage(Filter filter,float[][] image){
		float[][] result = new float[image.length-2][image[0].length-2];
		
		for(int x=0;x<image.length-2;x++){
			for(int y=0;y<image[0].length-2;y++){
				float[][] scope = new float[3][3];
				for(int ix=0;ix<3;ix++){
					for(int iy=0;iy<3;iy++){
						scope[ix][iy] = image[x+ix][y+iy];
					}
				}
				result[x][y] = filter.DotFilter(scope);
			}
		}
		return result;
	}
	public static float[][] MaxPooling(float[][] filt_img){
		float[][] result = new float[filt_img.length/2][filt_img[0].length/2];
		
		for(int x=0;x<filt_img.length/2;x++){
			for(int y=0;y<filt_img[0].length/2;y++){
				float highest = 0;
				highest = filt_img[2*x][2*y];
				if(highest < filt_img[2*x+1][2*y]){
					highest = filt_img[2*x+1][2*y];
				}else if(highest < filt_img[2*x][2*y+1]){
					highest = filt_img[2*x][2*y+1];
				}else if(highest < filt_img[2*x+1][2*y+1]){
					highest = filt_img[2*x+1][2*y+1];
				}
				result[x][y] = highest;
			}
		}
		return result;
	}
	public static float[][] MeanPooling(float[][] filt_img){
		float[][] result = new float[filt_img.length/2][filt_img[0].length/2];
		for(int x=0;x<filt_img.length/2;x++){
			for(int y=0;y<filt_img[0].length/2;y++){
				float highest = 0;
				highest = filt_img[2*x][2*y];
				highest += filt_img[2*x+1][2*y];
				highest += filt_img[2*x][2*y+1];
				highest += filt_img[2*x+1][2*y+1];
				result[x][y] = highest/4;
			}
		}
		return result;
	}
}
