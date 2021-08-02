package GJ3DUTIL;
import java.awt.Graphics2D;

import GJMATH.GJDataUtil.Matrix3x3;
import GJMATH.GJDataUtil.Vector3d;
import GJSWING.GJPanel;;

public class GJObject {
	public class triangle{
		Vector3d v1,v2,v3;
		public triangle(Vector3d v1,Vector3d v2,Vector3d v3){
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
		public void render(Graphics2D g2d,Matrix3x3 mat){
			
		}
	}
	public class Object3D{
		triangle[] triangles;
		public Object3D(triangle[] triangles){
			this.triangles = triangles;
		}
		public void render(Graphics2D g2d,Matrix3x3 mat){
			
		}
	}
	public class camera{
		Vector3d pos;
		Object3D[] objects;
		GJPanel panel;
		public camera(Vector3d pos,Object3D[] objects,GJPanel panel){
			this.pos = pos;
			this.objects = objects;
			this.panel = panel;
		}
		
	}
}
