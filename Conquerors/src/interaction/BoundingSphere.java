package interaction;

import org.joml.Vector3f;

import entities.Entity;

public class BoundingSphere extends Bounds {
	
	private float radius;
	
	public BoundingSphere(Entity entity, float radius) {
		this.entity = entity;
		this.radius = radius;
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public void update() {
		super.update();
	}
	
	
}
