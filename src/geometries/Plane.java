package geometries;

import primitives.*;
import primitives.Vector;

import java.util.*;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a plane in 3D space defined by a point and a normal vector.
 * A plane can be defined either by a point and a normal vector, or by three points
 * on the plane. The normal vector is always normalized upon creation.
 */
public class Plane extends Geometry {
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


    @Override
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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Vector v = ray.getDirection();
        Point p0 = ray.getHead();

        // If the ray's origin is exactly on the plane reference point,
        // there is no unique intersection point (or the ray lies in the plane)
        if (p0.equals(q)) {
            return null;
        }

        // Compute the dot product of the plane normal and the ray direction
        double nv = alignZero(normal.dotProduct(v));

        // If the dot product is zero, the ray is parallel to the plane (no intersection)
        if (isZero(nv)) {
            return null;
        }

        // Subtract the ray origin from the plane reference point (safe since p0 ≠ q)
        Vector p0q = q.subtract(p0);

        // Compute t (scalar for ray equation) using the plane intersection formula
        double t = alignZero(normal.dotProduct(p0q) / nv);

        // If t is zero or negative, the intersection is behind the ray origin or on it
        if (t <= 0) {
            return null;
        }

        // Compute the intersection point using the ray equation: P = p0 + t * v
        Point intersectionPoint = ray.getPoint(t);

        // Return the intersection as a list with a single Intersection object
        return List.of(new Intersection(this, intersectionPoint));
    }

}
