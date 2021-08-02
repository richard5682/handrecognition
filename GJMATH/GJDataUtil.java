package GJMATH;

public class GJDataUtil {
	public static class Vector3d{
		public float a,b,c;
		public Vector3d(float a,float b,float c){
			this.a = a;
			this.b = b;
			this.c = c;
		}
		public float dot(Vector3d vec){
			return vec.a*a+vec.b*b+vec.c*c;
		}
	}
	
	public static class Vector4d{
		public float a,b,c;
		public Vector4d(float a,float b,float c,float d){
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}
	
	public static class Matrix3x3{
		public float a,b,c,a1,b1,c1,a2,b2,c2;
		public Matrix3x3(float a,float b,float c
						,float a1,float b1,float c1
						,float a2,float b2,float c2){
			this.a = a;
			this.b = b;
			this.c = c;
			this.a1 = a1;
			this.b1 = b1;
			this.c1 = c1;
			this.a2 = a2;
			this.b2 = b2;
			this.c2 = c2;
		}
	}
}
