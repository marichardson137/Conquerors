package interaction;

import org.joml.Vector3f;

import entities.Entity;

public class BoundingBox extends Bounds {
	
	private Vector3f dimensions;

	private Vector3f min = new Vector3f();
	private Vector3f max = new Vector3f();

	public BoundingBox(Entity entity, Vector3f dimensions) {
		this.entity = entity;
		this.dimensions = dimensions;
	}
	
	public Vector3f getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(Vector3f dimensions) {
		this.dimensions = dimensions;
	}
	
    private void calculateMin() {
        this.min.x = this.position.x - this.dimensions.x / 2;
        this.min.y = this.position.y - this.dimensions.y / 2;
        this.min.z = this.position.z - this.dimensions.z / 2;
    }
    
    private void calculateMax() {
        this.max.x = this.position.x + this.dimensions.x / 2;
        this.max.y = this.position.y + this.dimensions.y / 2;
        this.max.z = this.position.z + this.dimensions.z / 2;
    }
    
    public Vector3f getMin() {
    	return this.min;
    }
    
    public Vector3f getMax() {
    	return this.max;
    }
	
	public void update() {
		super.update();
		calculateMin();
		calculateMax();
	}
	
	

}
