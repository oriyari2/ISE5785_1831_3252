package geometries;
import primitives.Vector;
import primitives.Point;

/**
 * An abstract class representing a geometric object in 3D space.
 * <p>
 * This class serves as a base for all geometric shapes and ensures that
 * every shape has a method to compute the normal vector at a given point.
 */
public interface Geometry extends Intersectable {
   /**
    * Computes the normal vector to the surface of the geometric shape at a given point.
    *
    * @param point The point on the surface of the geometric object
    * @return The normal vector at the given point
    */
   public abstract Vector getNormal(Point point);
}

