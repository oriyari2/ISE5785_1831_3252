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
     * @param q The point on the plane
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
     */
    public Plane(Point q1, Point q2, Point q3) {
        this.q = q1;
        Vector vec1 = q2.subtract(q1);
        Vector vec2 = q3.subtract(q1);
        Vector cross = vec1.crossProduct(vec2);
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

    public Point getPoint() {
        return q;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getHead();
        Vector v = ray.getDirection();

        // Check if ray direction is parallel to the plane (perpendicular to normal)
        double nv = normal.dotProduct(v);

        // If the ray is parallel to the plane (nv == 0)
        if (Util.isZero(nv)) {
            return null; // No intersection
        }

        // Check if the ray starts on the plane or the ray's head is on the plane
        Vector qMinusP0 = q.subtract(p0);
        double nQMinusP0 = normal.dotProduct(qMinusP0);

        // If the ray starts on the plane
        if (Util.isZero(nQMinusP0)) {
            return null; // The ray is in the plane or starts from the plane
        }

        // Calculate the intersection parameter t
        double t = nQMinusP0 / nv;

        // If t <= 0, the intersection is behind the ray's head
        if (t <= 0) {
            return null;
        }

        // Calculate the intersection point
        Point intersectionPoint = ray.getPoint(t);

        return List.of(intersectionPoint);
    }
}
