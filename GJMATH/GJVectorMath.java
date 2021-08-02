package GJMATH;

import GJMATH.GJDataUtil.Matrix3x3;
import GJMATH.GJDataUtil.Vector3d;

public class GJVectorMath {
	public static class Operation3x3{
		public float dotproduct(Vector3d vec1,Vector3d vec2){
			return vec1.a*vec2.a+vec1.b*vec2.b+vec1.c*vec2.c;
		}
		public Matrix3x3 Multiply(Matrix3x3 mat1,Matrix3x3 mat2){
			float a = mat1.a*mat2.a + mat1.b*mat2.a1 + mat1.c*mat2.a2;
			float b = mat1.a*mat2.b + mat1.b*mat2.b1 + mat1.c*mat2.b2;
			float c = mat1.a*mat2.c + mat1.b*mat2.c1 + mat1.c*mat2.c2;
			
			float a1 = mat1.a1*mat2.a + mat1.b1*mat2.a1 + mat1.c1*mat2.a2;
			float b1 = mat1.a1*mat2.b + mat1.b1*mat2.b1 + mat1.c1*mat2.b2;
			float c1 = mat1.a1*mat2.c + mat1.b1*mat2.c1 + mat1.c1*mat2.c2;
			
			float a2 = mat1.a2*mat2.a + mat1.b2*mat2.a1 + mat1.c2*mat2.a2;
			float b2 = mat1.a2*mat2.b + mat1.b2*mat2.b1 + mat1.c2*mat2.b2;
			float c2 = mat1.a2*mat2.c + mat1.b2*mat2.c1 + mat1.c2*mat2.c2;
			return new GJDataUtil.Matrix3x3(a,b,c,a1,b1,c1,a2,b2,c2);
		}
	}
}
