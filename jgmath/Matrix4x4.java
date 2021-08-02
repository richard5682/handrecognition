package jgmath;

public class Matrix4x4 {
	float   a,b,c,d,
			e,f,g,h,
			i,j,k,l,
			m,n,o,p;
	
	public Matrix4x4(
			float a,float b,float c,float d,
			float e,float f,float g,float h,
			float i,float j,float k,float l,
			float m,float n,float o,float p){
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
		this.e=e;
		this.f=f;
		this.g=g;
		this.h=h;
		this.i=i;
		this.j=j;
		this.k=k;
		this.l=l;
		this.m=m;
		this.n=n;
		this.o=o;
		this.p=p;
	}
	public String toString(){
		return String.format(" [%f,%f,%f,%f]\n [%f,%f,%f,%f]\n [%f,%f,%f,%f]\n [%f,%f,%f,%f]", a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p);
	}
}
