package interaction;

import org.joml.Vector3f;

public class Ray {

    private Vector3f origin;
    private Vector3f direction;
    public float t;
    
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        if (direction.length() != 1) {
            direction = direction.normalize();
        }
        this.direction = direction;
    }

    public Vector3f getOrigin() {
        return origin;
    }
    
    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    public Vector3f getDirection() {
        return direction;
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
    
}