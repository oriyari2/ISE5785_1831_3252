package geometries;

import primitives.*;
import java.util.List;

/**
 * Represents a triangle geometry, which is a type of polygon
 * defined by three points.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a Triangle from three given points.
     *
     * @param q1 The first point of the triangle.
     * @param q2 The second point of the triangle.
     * @param q3 The third point of the triangle.
     */
    public Triangle(Point q1, Point q2, Point q3) {
        super(q1, q2, q3); // Initializes the triangle using the Polygon constructor
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p = planeIntersections.get(0);

        Point p0 = ray.getHead();
        Vector dir = ray.getDirection();

        Point v1 = vertices.get(0);
        Point v2 = vertices.get(1);
        Point v3 = vertices.get(2);

        // Create vectors
        Vector v1p = v1.subtract(p0);
        Vector v2p = v2.subtract(p0);
        Vector v3p = v3.subtract(p0);

        // Normals to triangle's edges
        Vector n1 = v1p.crossProduct(v2p).normalize();
        Vector n2 = v2p.crossProduct(v3p).normalize();
        Vector n3 = v3p.crossProduct(v1p).normalize();

        // Check if the intersection point is on the same side of all edges
        double s1 = Util.alignZero(dir.dotProduct(n1));
        double s2 = Util.alignZero(dir.dotProduct(n2));
        double s3 = Util.alignZero(dir.dotProduct(n3));

        // If all have the same sign (all positive or all negative), the point is inside
        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            return List.of(p);
        }

        return null;
    }

}
