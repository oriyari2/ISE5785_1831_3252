package geometries;

import primitives.*;
import java.util.List;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * <p>
 * A plane can be defined either by a point and a normal vector, or by three points
 * on the plane. The normal vector is always normalized upon creation.
 */
public class Plane implements Geometry {
    /**
     * The point Q that defines the plane.
     */
    private final Point q;
    /**
     * The normal vector to the plane.
     */
    private final Vector normal;

    /**
     * Constructs a plane given a point on the plane and the normal vector.
     *
     * @param q      The point on the plane
     * @param normal The normal vector to the plane, which will be normalized
     */
    public Plane(Point q, Vector normal) {
        this.q = q;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane using three points on the plane.
     * The normal vector is calculated as the cross product of two vectors formed by the points.
     *
     * @param q1 The first point on the plane
     * @param q2 The second point on the plane
     * @param q3 The third point on the plane
     * @throws IllegalArgumentException if the points are not suitable to define a plane
     */
    public Plane(Point q1, Point q2, Point q3) {
        Vector vec1 = q2.subtract(q1);
        Vector vec2 = q3.subtract(q1);

        // Check if the points are not collinear (cross product must not be zero)
        Vector cross = vec1.crossProduct(vec2);
        if (cross.lengthSquared() == 0) {
            throw new IllegalArgumentException("The given points do not define a valid plane (they may be identical or collinear).");
        }

        this.q = q1;
        this.normal = cross.normalize();
    }

    /**
     * Returns the normal vector of the plane at a given point.
     * Since the normal is constant for a plane, it will always return the same vector.
     *
     * @param point A point on the plane (not used in the calculation as the normal is constant)
     * @return The normal vector of the plane
     */
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * Returns a point on the plane.
     *
     * @return A reference point on the plane
     */
    public Point getPoint() {
        return q;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getHead();
        Vector v = ray.getDirection();

        // If the ray starts exactly at the point on the plane → no intersection
        if (q.equals(p0)) {
            return null;
        }

        double nv = normal.dotProduct(v);

        // Ray is parallel to the plane → no intersection
        if (Util.isZero(nv)) {
            return null;
        }

        Vector qMinusP0 = q.subtract(p0);
        double nQMinusP0 = normal.dotProduct(qMinusP0);

        // Ray starts on the plane → no intersection
        if (Util.isZero(nQMinusP0)) {
            return null;
        }

        double t = nQMinusP0 / nv;

        if (t <= 0) {
            return null;
        }

        return List.of(ray.getPoint(t));
    }

}
