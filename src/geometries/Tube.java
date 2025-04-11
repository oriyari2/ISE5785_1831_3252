package geometries;
import primitives.Util;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents a tube geometry, which is a type of radial geometry
 * defined by an axis (a Ray) and a radius.
 */
public class Tube extends RadialGeometry {

    /**
     * The axis of the tube, represented by a Ray.
     */
    protected final Ray axis;

    /**
     * Constructs a Tube with the given axis and radius.
     *
     * @param axis The axis of the tube, represented by a Ray.
     * @param radius The radius of the tube.
     */
    public Tube(Ray axis, double radius) {
        super(radius);  // Calls the constructor of RadialGeometry to initialize the tube's radius
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        // Get the direction vector of the axis of the tube
        Vector v = axis.getDirection();

        // Get the starting point of the axis (the ray's origin)
        Point p0 = axis.getHead();

        // Create a vector from the axis origin to the given point
        Vector v2 = point.subtract(p0);

        // Calculate the projection of the point onto the axis
        double t = Util.alignZero(v.dotProduct(v2));

        // If the projection is zero, the orthogonal point is simply p0 (the axis origin)
        Point o = Util.isZero(t) ? p0 : axis.getPoint(t);

        // Return the normal vector, which is the normalized vector from the point to the closest point on the axis
        return point.subtract(o).normalize();
    }



    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
