package GJMATH;

import GJIMAGE.GJImageTune.Point;

public class GJMATHUTIL {
	public static float sigmoid(float a){
		if(Float.isInfinite(a)){
			if(a>0){
				return 1;
			}else{
				return 0;
			}
		}
		//return ((a/(1+Math.abs(a))+1)/2);
		return (float) (1/(1+Math.exp(-a)));
	}
	public static float RelU(float a){
		if(a < 0){
			return 0;
		}else if(a > 1){
			return 1;
		}
		return a;
	}
	public static float[] SoftMax(float[] values){
		float buffer=0;
		float[] return_buffer = new float[values.length];
		for(int i=0;i<values.length;i++){
			buffer += Math.exp(values[i]);
		}
		for(int i=0;i<values.length;i++){
			return_buffer[i] = (float) (Math.exp(values[i])/buffer);
		}
		return return_buffer;
	}
	public static float GetSoftmaxAdjustment(float zn,float an,float dn,float sum){
		float a = (float) ((sum*Math.exp(an))/Math.pow((sum+Math.exp(an)),2));
		float b = (float) (-(dn-zn)/(Math.log(10)*Math.abs(dn-zn)*(1-Math.abs(dn-zn))));
		float c = (dn-zn)/Math.abs(dn-zn);
		return a*b*c;
	}
	public static float GetDistance(float x,float y,float x1,float y1){
		float iy = y1-y;
		float ix = x1-x;
		return (float)Math.sqrt((iy*iy)+(ix*ix));
	}
	public static Point GetCenterPoint(Point p1,Point p2){
		return new Point((p1.xf+p2.xf)/2,(p1.yf+p2.yf)/2);
	}
	public static Point ScalePoint(Point p1,float scale){
		return new Point(p1.xf*scale,p1.yf*scale);
	}
	public static Point SubtractPoint(Point p1,Point p2){
		return new Point(p1.xf-p2.xf,p1.yf-p2.yf);
	}
	public static Point AddPoint(Point p1,Point p2){
		return new Point(p1.xf+p2.xf,p1.yf+p2.yf);
	}
	public static Point NormalizePoint(Point p1){
		float dist = GetDistance(p1.xf,p1.yf,0,0);
		return new Point(p1.xf/dist,p1.yf/dist);
	}
	public static float DotProduct(Point p1,Point p2){
		return p1.xf*p2.xf + p1.yf*p2.yf;
	}
	public static float GetAnglePoint(Point p1){
		float deg1 = (float)Math.atan(p1.yf/p1.xf);
		if(p1.yf > 0 && p1.xf < 0){
			deg1 += Math.PI;
		}
		if(p1.yf < 0 && p1.xf < 0){
			deg1 += Math.PI;
		}
		if(p1.yf < 0 && p1.xf > 0){
			deg1 += 2*Math.PI;
		}
		return deg1;
	}
	public static float GetAngleRad(Point p1,Point p2){
		float deg1 = GetAnglePoint(p1);
		float deg2 = GetAnglePoint(p2);
		return (deg1-deg2);
	}
	public static float GetAngleDeg(Point p1,Point p2){
		return (float)((GetAngleRad(p1,p2))*180/Math.PI);
	}
}
