package jgmath;

public class Vector3Math {
	
	public static final Matrix3x3 IDENTITY_MATRIX = new Matrix3x3(1,0,0,0,1,0,0,0,1);
	
	public static Vector3 VectorAdd(Vector3 v1,Vector3 v2){
		return new Vector3(v1.x+v2.x,v1.y+v2.y,v1.z+v2.z);
	}
	public static Vector3 VectorScale(Vector3 v,float s){
		return new Vector3(v.x*s,v.y*s,v.z*s);
	}
	//ifbugging check this
	public static Vector3 VectorMinus(Vector3 v1,Vector3 v2){
		return new Vector3(v1.x-v2.x,v1.y-v2.y,v1.z-v2.z);
	}
	public static float VectorMagnitude(Vector3 v){
		return (float) Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
	}
	public static float VectorMagnitude(Vector3 v1,Vector3 v2){
		return VectorMagnitude(VectorMinus(v1,v2));
	}
	public static Matrix3x3 Transform(Matrix3x3 m1,Matrix3x3 m2){
		return new Matrix3x3(m1.a*m2.a+m1.b*m2.d+m1.c*m2.g,
							m1.a*m2.b+m1.b*m2.e+m1.c*m2.h,
							m1.a*m2.c+m1.b*m2.f+m1.c*m2.i,
							
							m1.d*m2.a+m1.e*m2.d+m1.f*m2.g,
							m1.d*m2.b+m1.e*m2.e+m1.f*m2.h,
							m1.d*m2.c+m1.e*m2.f+m1.f*m2.i,
							
							m1.g*m2.a+m1.h*m2.d+m1.i*m2.g,
							m1.g*m2.b+m1.h*m2.e+m1.i*m2.h,
							m1.g*m2.c+m1.h*m2.f+m1.i*m2.i);
	}
	public static Vector3 ProjectVector(Vector3 a,Vector3 n){
		float dot = Vector3Math.DotProduct(a, n);
		return Vector3Math.VectorScale(n, dot);
	}
	public static Vector3 Transform(Matrix3x3 m,Vector3 v){
		return new Vector3(
				m.a*v.x+m.b*v.y+m.c*v.z,
				m.d*v.x+m.e*v.y+m.f*v.z,
				m.g*v.x+m.h*v.y+m.i*v.z);
	}
	public static Vector3 Vectorto3(Vector4 v){
		return new Vector3(v.x,v.y,v.z);
	}
	public static Vector3 CrossProduct(Vector3 v1,Vector3 v2){
		return new Vector3(
				(v1.y*v2.z)-(v1.z*v2.y),
				(v1.z*v2.x)-(v1.x*v2.z),
				(v1.x*v2.y)-(v1.y*v2.x));
	}
	public static Vector3 Normalize(Vector3 v1){
		return VectorScale(v1,1/VectorMagnitude(v1));
	}
	public static float DotProduct(Vector3 v1,Vector3 v2){
		return v1.x*v2.x+v1.y*v2.y+v1.z*v2.z;
	}
}
