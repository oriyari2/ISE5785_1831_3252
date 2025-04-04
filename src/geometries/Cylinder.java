// Cylinder.java
package geometries;
import primitives.Util;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Cylinder class represents a cylinder in 3D space.
 */
public class Cylinder extends Tube {
    /** Small value for precision comparisons */
    private static final double DELTA = 0.000001;
    /** Height of the cylinder */
    private double height;


    /**
     * Constructor to initialize Cylinder with axis ray, radius, and height.
     * @param axisRay the axis ray of the cylinder
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     */

    public Cylinder(Ray axisRay, double radius, double height) {
        super(axisRay, radius);
        this.height = height;
    }

    @Override
    /**
     * Calculate the normal vector to the cylinder at a given point.
     * The point is assumed to be on the cylinder surface.
     * The method handles three cases:
     * 1. Point on the curved surface (side)
     * 2. Point on the first base
     * 3. Point on the second base
     * And handles boundary cases where points are at centers or edges.
     *
     * @param point A point on the cylinder surface
     * @return The normalized normal vector at the given point
     */
    public Vector getNormal(Point point) {
        Point p0 = axis.getHead();
        Vector dir = axis.getDirection();

        // Vector from base to point
        Vector fromBase = point.subtract(p0);
        double t = Util.alignZero(fromBase.dotProduct(dir));

        // On bottom base
        if (Util.alignZero(t) == 0)
            return dir.scale(-1);

        // On top base
        if (Util.alignZero(t - height) == 0)
            return dir;

        // On side surface (delegate to Tube)
        return super.getNormal(point);
    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
    }