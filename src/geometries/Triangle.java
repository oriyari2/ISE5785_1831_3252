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
        // Step 1: Find intersection point with the triangle's plane
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) {
            return null; // No intersection with the plane
        }

        // Take the single intersection point with the plane
        Point P = planeIntersections.get(0);

        // Step 2: Check if the point lies inside the triangle
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);

        // Check if the intersection point coincides with any of the triangle's vertices
        if (P.equals(p1) || P.equals(p2) || P.equals(p3)) {
            return null; // Point is exactly on a vertex - not considered inside
        }

        // Create vectors from each vertex to the intersection point
        Vector v1, v2, v3;
        try {
            v1 = P.subtract(p1); // Vector from p1 to P
            v2 = P.subtract(p2);
            v3 = P.subtract(p3);
        } catch (IllegalArgumentException e) {
            // One of the vectors is zero (P is equal to one of the vertices)
            return null;
        }

        // Edge vectors of the triangle
        Vector e1 = p2.subtract(p1);
        Vector e2 = p3.subtract(p2);
        Vector e3 = p1.subtract(p3);

        // Get the normal vector of the triangle's plane
        Vector n = plane.getNormal(P);

        try {
            // Compute cross products of edge vectors with vectors to point P
            Vector n1 = e1.crossProduct(v1);
            Vector n2 = e2.crossProduct(v2);
            Vector n3 = e3.crossProduct(v3);

            // Compute dot products with the normal vector
            double s1 = Util.alignZero(n1.dotProduct(n));
            double s2 = Util.alignZero(n2.dotProduct(n));
            double s3 = Util.alignZero(n3.dotProduct(n));

            // Check if the point lies exactly on an edge or vertex
            if (Util.isZero(s1) || Util.isZero(s2) || Util.isZero(s3)) {
                return null;
            }

            // If all dot products have the same sign, the point is inside the triangle
            if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
                return List.of(P);
            }
        } catch (IllegalArgumentException e) {
            // Cross product failed (zero vector) - point lies on edge or is invalid
            return null;
        }

        // Point is outside the triangle
        return null;
    }
}
