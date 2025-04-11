package geometries;

import primitives.*;
import java.util.List;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * A plane can be defined either by a point and a normal vector, or by three points
 * on the plane. The normal vector is always normalized upon creation.
 */
public class Plane implements Geometry {
    /**
     * A reference point on the plane.
     */
    private final Point q;

    /**
     * The normal vector to the plane.
     */
    private final Vector normal;

    /**
     * Constructs a plane given a point on the plane and the normal vector.
     *
     * @param q      the point on the plane
     * @param normal the normal vector to the plane (will be normalized)
     */
    public Plane(Point q, Vector normal) {
        this.q = q;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane using three non-collinear points on the plane.
     * The normal vector is calculated as the cross product of two vectors formed by the points.
     *
     * @param q1 the first point on the plane
     * @param q2 the second point on the plane
     * @param q3 the third point on the plane
     * @throws IllegalArgumentException if the points are collinear or identical,
     *                                  and thus cannot define a valid plane
     */
    public Plane(Point q1, Point q2, Point q3) {
        Vector vec1 = q2.subtract(q1);
        Vector vec2 = q3.subtract(q1);

        // Calculate the cross product to get the normal vector
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
     * @param point a point on the plane (not used in the calculation)
     * @return the normal vector of the plane
     */
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * Returns a point on the plane.
     *
     * @return the reference point on the plane
     */
    public Point getPoint() {
        return q;
    }

    /**
     * Finds the intersection points of the ray with the plane.
     *
     * @param ray the ray to check for intersections with the plane
     * @return a list of intersection points between the ray and the plane, or null if there is no intersection
     */

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.getHead();
        Vector v = ray.getDirection();

        double nv = normal.dotProduct(v);

        // If the ray is parallel to the plane - no intersection
        if (Util.isZero(nv)) return null;

        // If the ray starts exactly at the reference point on the plane - considered no intersection
        if (q.equals(p0)) return null;

        Vector qMinusP0 = q.subtract(p0);
        double nQMinusP0 = normal.dotProduct(qMinusP0);

        double t = Util.alignZero(nQMinusP0 / nv);

        // If the intersection is behind the ray's head - no intersection
        if (t <= 0) return null;

        // Return the intersection point
        return List.of(ray.getPoint(t));
    }
}
