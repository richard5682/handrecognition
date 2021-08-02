package jgmath;

public class Vector4Math {
	public static final Matrix4x4 IDENTITY_MATRIX = new Matrix4x4(	 1,0,0,0,
															 0,1,0,0,
															 0,0,1,0,
															 0,0,0,1);
	public static Matrix4x4 Transform(Matrix4x4 m1,Matrix4x4 m2){
		return new Matrix4x4(
				m1.a*m2.a+m1.b*m2.e+m1.c*m2.i+m1.d*m2.m  ,
				m1.a*m2.b+m1.b*m2.f+m1.c*m2.j+m1.d*m2.n  ,
				m1.a*m2.c+m1.b*m2.g+m1.c*m2.k+m1.d*m2.o  ,
				m1.a*m2.d+m1.b*m2.h+m1.c*m2.l+m1.d*m2.p  ,
				
				m1.e*m2.a+m1.f*m2.e+m1.g*m2.i+m1.h*m2.m  ,
				m1.e*m2.b+m1.f*m2.f+m1.g*m2.j+m1.h*m2.n  ,
				m1.e*m2.c+m1.f*m2.g+m1.g*m2.k+m1.h*m2.o  ,
				m1.e*m2.d+m1.f*m2.h+m1.g*m2.l+m1.h*m2.p  ,
				
				m1.i*m2.a+m1.j*m2.e+m1.k*m2.i+m1.l*m2.m  ,
				m1.i*m2.b+m1.j*m2.f+m1.k*m2.j+m1.l*m2.n  ,
				m1.i*m2.c+m1.j*m2.g+m1.k*m2.k+m1.l*m2.o  ,
				m1.i*m2.d+m1.j*m2.h+m1.k*m2.l+m1.l*m2.p  ,
				
				m1.m*m2.a+m1.n*m2.e+m1.o*m2.i+m1.p*m2.m  ,
				m1.m*m2.b+m1.n*m2.f+m1.o*m2.j+m1.p*m2.n  ,
				m1.m*m2.c+m1.n*m2.g+m1.o*m2.k+m1.p*m2.o  ,
				m1.m*m2.d+m1.n*m2.h+m1.o*m2.l+m1.p*m2.p );
	}
	
	public static Vector4 Transform(Matrix4x4 m,Vector4 v){
		return new Vector4(
				m.a*v.x+m.b*v.y+m.c*v.z+m.d*v.w ,
				m.e*v.x+m.f*v.y+m.g*v.z+m.h*v.w ,
				m.i*v.x+m.j*v.y+m.k*v.z+m.l*v.w ,
				m.m*v.x+m.n*v.y+m.o*v.z+m.p*v.w  );
	}
	public static Vector4 Vector3to4(Vector3 v){
		return new Vector4(v.x,v.y,v.z,1);
	}
	public static Matrix4x4 GetRotationMatrix(float thetax,float thetay,float thetaz){
		float sx = (float)Math.sin(thetax);
		float cx = (float)Math.cos(thetax);
		
		float sy = (float)Math.sin(thetay);
		float cy = (float)Math.cos(thetay);
		
		float sz = (float)Math.sin(thetaz);
		float cz = (float)Math.cos(thetaz);
		
		Matrix4x4[] mat = new Matrix4x4[3];
		mat[0] = new Matrix4x4(
				1,0,0,0,
				0,cx,sx,0,
				0,-sx,cx,0,
				0,0,0,1);
		mat[1] = new Matrix4x4(
				cy,0,sy,0,
				0,1,0,0,
				-sy,0,cy,0,
				0,0,0,1);
		mat[2] = new Matrix4x4(
				cz,sz,0,0,
				-sz,cz,0,0,
				0,0,1,0,
				0,0,0,1);
		Matrix4x4 mat1 = Transform(mat[0],mat[2]);
		return Transform(mat1,mat[1]);
	}
	public static Matrix4x4 GetCameraRotMatrix(float thetax,float thetay,float thetaz){
		float sx = (float)Math.sin(thetax);
		float cx = (float)Math.cos(thetax);
		
		float sy = (float)Math.sin(thetay);
		float cy = (float)Math.cos(thetay);
		
		float sz = (float)Math.sin(thetaz);
		float cz = (float)Math.cos(thetaz);
		
		Matrix4x4[] mat = new Matrix4x4[3];
		mat[0] = new Matrix4x4(
				1,0,0,0,
				0,cx,sx,0,
				0,-sx,cx,0,
				0,0,0,1);
		mat[1] = new Matrix4x4(
				cy,0,sy,0,
				0,1,0,0,
				-sy,0,cy,0,
				0,0,0,1);
		mat[2] = new Matrix4x4(
				cz,sz,0,0,
				-sz,cz,0,0,
				0,0,1,0,
				0,0,0,1);
		Matrix4x4 mat1 = Transform(mat[1],mat[2]);
		return Transform(mat1,mat[0]);
	}
	public static Matrix4x4 GetTranslationMatrix(Vector3 v){
		return new Matrix4x4(
				1,0,0,v.x,
				0,1,0,v.y,
				0,0,1,v.z,
				0,0,0,1);
	}
	public static Matrix4x4 GetOrhtoMatrix(Vector3 v1,Vector3 v2){
		float e1 = 2/(v2.x-v1.x);
		float e2 = (v1.x+v2.x)/(v1.x-v2.x);
		float e3 = 2/(v2.y-v1.y);
		float e4 = (v1.y+v2.y)/(v1.y-v2.y);
		float e5 = 1/(v2.z-v1.z);
		float e6 = v1.z/(v1.z-v2.z);
		return new Matrix4x4(
				e1,0,0,e2,
				0,e3,0,e4,
				0,0,e5,e6,
				0,0,0,1);
	}
	public static Matrix4x4 GetOrhtoMatrix(Vector4 v1,Vector4 v2){
		float e1 = 2/(v2.x-v1.x);
		float e2 = (v1.x+v2.x)/(v1.x-v2.x);
		float e3 = 2/(v2.y-v1.y);
		float e4 = (v1.y+v2.y)/(v1.y-v2.y);
		float e5 = v1.z/(v1.z-v2.z);
		float e6 = 1/(v2.z-v1.z);
		return new Matrix4x4(
				e1,0,0,e2,
				0,e3,0,e4,
				0,0,e5,e6,
				0,0,0,1);
	}
	public static Matrix4x4 GetProjectMatrix(Vector3 v1,Vector3 v2,float f){
		float e1 = (2*f)/(v2.x-v1.x);
		float e2 = (v1.x+v2.x)/(v1.x-v2.x);
		float e3 = (2*f)/(v2.y-v1.y);
		float e4 = (v1.y+v2.y)/(v1.y-v2.y);
		float e5 = v1.z/(v1.z-v2.z);
		float e6 = 1/(v2.z-v1.z);
		return new Matrix4x4(
				e1,0,e2,0,
				0,e3,e4,0,
				0,0,e5,e6,
				0,0,0,1);
	}
	public static Matrix4x4 GetScaleMatrix(float sx,float sy){
		return new Matrix4x4(
				sx,0,0,0,
				0,sy,0,0,
				0,0,1,0,
				0,0,0,1);
	}
	public static Vector4 VectorScale(Vector4 v,float s){
		return new Vector4(v.x*s,v.y*s,v.z*s,1);
	}
}
