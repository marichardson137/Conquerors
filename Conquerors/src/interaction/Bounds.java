package interaction;

import org.joml.Vector3f;

import entities.Entity;

public abstract class Bounds {
	
	protected Vector3f position;
	protected Entity entity; 
	
	protected Vector3f getPosition() {
		return position;
	}

	protected void setPosition(Vector3f position) {
		this.position = position;
	}

	protected Entity getEntity() {
		return entity;
	}
	
	public void update() {
		this.position = entity.getPosition();
	}

}
