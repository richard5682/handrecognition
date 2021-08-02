package jgmath;

public class Vector4 {
	public float x,y,z,w;
	
	public Vector4(float x,float y,float z,float w){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}
	public Vector3 getVector3(){
		return new Vector3(this.x,this.y,this.z);
	}
	public String toString(){
		return String.format("x : %f , y : %f , z : %f , w : %f", x,y,z,w);
	}
}
