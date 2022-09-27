package toolbox;

import org.joml.*;
import org.joml.Math;

import entities.Camera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation);
		matrix.rotate(Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate(Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate(Math.toRadians(rz), new Vector3f(0, 0, 1));
		matrix.scale(scale);
		return matrix;
	}
	
	// GUIs
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation.x, translation.y, 0.0f);
		matrix.scale(new Vector3f(scale.x, scale.y, 1f));
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}
	
	// Player movement (determine rotation for terrain)
	public static Vector2f determineVectorRotations(Vector3f normal) {
		Vector3f UP = new Vector3f(0, 1f, 0);
		Vector3f bX = new Vector3f(0, normal.y, normal.z);
		Vector3f bZ = new Vector3f(normal.x, normal.y, 0);
		bX.normalize();
		bZ.normalize();
		float thetaX = (float)Math.toDegrees(Math.acos(UP.dot(bX)));
		float thetaZ = (float)Math.toDegrees(Math.acos(UP.dot(bZ)));
//		if (thetaX > 180) {
//			thetaX -= 180;
//		}
//		if (thetaZ > 180) {
//			thetaZ -= 180;
//		}
		
		return new Vector2f(thetaX, thetaZ);
		
	}
 
	
}
