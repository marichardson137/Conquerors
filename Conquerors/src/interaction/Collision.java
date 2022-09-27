package interaction;

import org.joml.Vector3f;

import entities.Entity;

//AABB Collision Class
public class Collision {
 
 // Box-Box
 public static boolean BoxBox(Entity one, Entity two) {
     BoundingBox a = (BoundingBox) one.getBounds();
     BoundingBox b = (BoundingBox) two.getBounds();
     return ( 
             (a.getMin().x <= b.getMax().x && a.getMax().x >= b.getMin().x) &&
             (a.getMin().y <= b.getMax().y && a.getMax().y >= b.getMin().y) &&
             (a.getMin().z <= b.getMax().z && a.getMax().z >= b.getMin().z) 
            );
 }

 // Point-Box
 public static boolean PointBox(Vector3f point, Entity one) {
     BoundingBox a = (BoundingBox) one.getBounds();
     return (
             (point.x >= a.getMin().x && point.x <= a.getMax().x) &&
             (point.y >= a.getMin().y && point.y <= a.getMax().y) &&
             (point.z >= a.getMin().z && point.z <= a.getMax().z)
            );
 }
 
 // Sphere-sphere
 public static boolean SphereSphere(Entity one, Entity two) {
	 BoundingSphere sphereOne = (BoundingSphere) one.getBounds();
	 BoundingSphere sphereTwo = (BoundingSphere) two.getBounds();
     Vector3f centerOne = sphereOne.getPosition();
     Vector3f centerTwo = sphereTwo.getPosition();
     float distance = (float) Math.sqrt((centerOne.x - centerTwo.x) * (centerOne.x - centerTwo.x) +
                        		(centerOne.y - centerTwo.y) * (centerOne.y - centerTwo.y) +
                        		(centerOne.z - centerTwo.z) * (centerOne.z - centerTwo.z));
     return distance < (sphereOne.getRadius() + sphereTwo.getRadius());
 }

 // Point-Sphere
 public static boolean PointSphere(Vector3f point, Entity one) {
	 BoundingSphere sphere = (BoundingSphere) one.getBounds();
	 Vector3f center = sphere.getPosition();
     float distance = (float) Math.sqrt((point.x - center.x) * (point.x - center.x) +
                                		(point.y - center.y) * (point.y - center.y) +
                                		(point.z - center.z) * (point.z - center.z));
     return distance < sphere.getRadius();
 }

 // Box-Sphere
 public static boolean BoxSphere(Entity one, Entity two) {
     BoundingBox box = (BoundingBox) one.getBounds();
     BoundingSphere sphere = (BoundingSphere) two.getBounds();
     Vector3f center = sphere.getPosition();
     float x = Math.max(box.getMin().x, Math.min(center.x, box.getMax().x));
     float y = Math.max(box.getMin().y, Math.min(center.y, box.getMax().y));
     float z = Math.max(box.getMin().z, Math.min(center.z, box.getMax().z));
     float distance = (float) Math.sqrt((x - center.x) * (x - center.x) +
                                		(y - center.y) * (y - center.y) +
                                		(z - center.z) * (z - center.z));
     return distance < sphere.getRadius();
 }

 // Ray-Box
 public static boolean RayBox(Entity one, Ray ray) {
	 BoundingBox box = (BoundingBox) one.getBounds();
     Vector3f lb = box.getMin();
     Vector3f rt = box.getMax();
     Vector3f org = ray.getOrigin();
     Vector3f dir = ray.getDirection();

     // r.dir is unit direction vector of ray
     float dirfracX = 1.0f / dir.x;
     float dirfracY = 1.0f / dir.y;
     float dirfracZ = 1.0f / dir.z;
     // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
     // r.org is origin of ray
     float t1 = (lb.x - org.x)*dirfracX;
     float t2 = (rt.x - org.x)*dirfracX;
     float t3 = (lb.y - org.y)*dirfracY;
     float t4 = (rt.y - org.y)*dirfracY;
     float t5 = (lb.z - org.z)*dirfracZ;
     float t6 = (rt.z - org.z)*dirfracZ;

     float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
     float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

     // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
     if (tmax < 0)
     {
         ray.t = tmax;
         return false;
     }

     // if tmin > tmax, ray doesn't intersect AABB
     if (tmin > tmax)
     {
         ray.t = tmax;
         return false;
     }

     ray.t = tmin;
     return true;
 }

 // Ray-Sphere
 public static boolean RaySphere(Entity one, Ray ray) {

     float t0, t1; // solutions for t if the ray intersects 

     BoundingSphere sphere = (BoundingSphere) one.getBounds();
     
     float radius2 = sphere.getRadius() * sphere.getRadius();
     
     // geometric solution
     Vector3f L = sphere.getPosition().sub(ray.getOrigin(), new Vector3f()); 
     float tca = L.dot(ray.getDirection()); 
     
     // if (tca < 0) return false;
     float d2 = L.dot(L) - tca * tca; 
     if (d2 > radius2) return false; 
     float thc = (float) Math.sqrt(radius2 - d2); 
     t0 = tca - thc; 
     t1 = tca + thc; 

     ray.t = t0; // closer of the two
     return true;

 }

}