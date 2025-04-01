// Cylinder.java
package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Cylinder extends Tube {
    private static final double DELTA = 0.000001;

    private double height;

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

        // Calculate the projection of the point on the axis
        double t = point.subtract(p0).dotProduct(dir);

        // Check if the point is on the top base
        if (Math.abs(t - height) < DELTA) {
            return dir;
        }

        // Check if the point is on the bottom base
        if (Math.abs(t) < DELTA) {
            return dir.scale(-1);
        }

        // Otherwise, the point is on the side surface
        Point o = p0.add(dir.scale(t));
        return point.subtract(o).normalize();
    }
    }