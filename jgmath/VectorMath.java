package jgmath;

public class VectorMath {
	public static final Matrix2x2 CORRECTION_MATRIX = new Matrix2x2(1,0,0,-1);
	public static final Matrix2x2 IDENTITY_MATRIX = new Matrix2x2(1,0,0,1);
	
	public static Vector2 Transform(Matrix2x2 mat,Vector2 vec){
		return new Vector2((mat.a*vec.x)+(mat.b*vec.y) , (mat.c*vec.x)+(mat.d*vec.y));
	}
	public static Matrix2x2 Transform(Matrix2x2 mat1,Matrix2x2 mat2){
		return new Matrix2x2(
				(mat2.a*mat1.a)+(mat2.c*mat1.b),
				(mat2.b*mat1.a)+(mat2.d*mat1.b),
				(mat2.a*mat1.c)+(mat2.c*mat1.d),
				(mat2.b*mat1.c)+(mat2.d*mat1.d));
	}
	public static float GetDeterminant(Matrix2x2 mat){
		return (mat.a*mat.d) - (mat.b*mat.c);
	}
	public static Matrix2x2 TranlateMatrix(Matrix2x2 mat,Vector2 vec){
		return new Matrix2x2(mat.a+vec.x,mat.b+vec.y,mat.c+vec.x,mat.d+vec.y);
	}
	public static Matrix2x2 CreateMatrix(Vector2 v1,Vector2 v2){
		return new Matrix2x2(v1.x,v1.y,v2.x,v2.y);
	}
	public static Matrix2x2 ScaleMatrix(Matrix2x2 mat,float scale){
		return new Matrix2x2(mat.a*scale,mat.b*scale,mat.c*scale,mat.d*scale);
	}
	public static Vector2 VectorMinus(Vector2 v1,Vector2 v2){
		return new Vector2(v1.x-v2.x,v1.y-v2.y);
	}
	public static Vector2 VectorAdd(Vector2 v1,Vector2 v2){
		return new Vector2(v1.x+v2.x,v1.y+v2.y);
	}
	public static Vector2 VectorAdd(Vector2 v1,Vector2 v2,Vector2 v3){
		return new Vector2(v1.x+v2.x+v3.x,v1.y+v2.y+v3.y);
	}
	public static Vector2 ScaleVector(Vector2 v1,float scalar){
		return new Vector2(scalar*v1.x,scalar*v1.y);
	}
	public static double VectorMagnitude(Vector2 v1){
		return Math.sqrt(v1.x*v1.x + v1.y*v1.y);
	}
	public static Vector2 VectorInterpolate(Vector2 v1,Vector2 v2,float s){
		Vector2 v3 = VectorMinus(v2,v1);
		return VectorAdd(v1,ScaleVector(v3,s));
	}
	public static Matrix2x2 MatrixInterpolate(Matrix2x2 mat1,Matrix2x2 mat2,float s){
		Vector2 v1 = new Vector2(mat1.a,mat1.b);
		Vector2 v2 = new Vector2(mat1.c,mat1.d);
		Vector2 v3 = new Vector2(mat2.a,mat2.b);
		Vector2 v4 = new Vector2(mat2.c,mat2.d);
		return CreateMatrix(VectorInterpolate(v1,v3,s),VectorInterpolate(v2,v4,s));
	}
	
	public static boolean checkInsideCircle(Vector2 v1,Vector2 orig,float r){
		if(VectorMagnitude(VectorMinus(v1,orig)) < r){
			return true;
		}else{
			return false;
		}
	}

	public static Vector2 Vectorto2(Vector4 v){
		return new Vector2(v.x,v.y);
	}
	public static Vector2 Vectorto2(Vector3 v){
		return new Vector2(v.x,v.y);
	}
	public static Matrix2x2 GetInverse(Matrix2x2 mat){
		float det = GetDeterminant(mat);
		return ScaleMatrix(new Matrix2x2(mat.d,-mat.b,-mat.c,mat.a),1/det);
		
	}
}
