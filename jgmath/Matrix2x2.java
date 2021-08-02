 package jgmath;

public class Matrix2x2 {
	public float a,b,c,d;
	public Matrix2x2(float a,float b,float c,float d){
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
	}
	public String toString(){
		return String.format("[ %f    %f  ]\n[ %f    %f  ]", a,b,c,d);
	}
}
