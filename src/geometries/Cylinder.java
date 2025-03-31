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

        // Avoid zero vector case
        Vector normal = point.subtract(o);
        if (normal.lengthSquared() < DELTA) {
            throw new IllegalArgumentException("Normal calculation resulted in zero vector");
        }

        return normal.normalize();
    }
}