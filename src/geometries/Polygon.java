package geometries;

import java.util.List;
import static primitives.Util.*;
import primitives.*;

/**
 * Polygon class represents a two-dimensional convex polygon in 3D Cartesian coordinate system.
 * The polygon must be defined by at least three ordered, coplanar, and non-collinear vertices.
 * The polygon is assumed to be convex.
 */
public class Polygon implements Geometry {
    /** List of polygon's vertices */
    protected final List<Point> vertices;

    /** Associated plane in which the polygon lies */
    protected final Plane plane;

    /** The size of the polygon - the number of vertices */
    private final int size;

    /**
     * Polygon constructor based on a list of vertices.
     * The vertices must be ordered and form a convex polygon.
     *
     * @param vertices list of vertices according to their order by edge path
     * @throws IllegalArgumentException if:
     * - there are less than 3 vertices
     * - two consecutive vertices are the same point
     * - vertices are not in the same plane
     * - vertices are not ordered correctly
     * - three consecutive vertices lie on the same line
     * - the polygon is concave
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");

        this.vertices = List.of(vertices);  // Create an immutable list of vertices
        size = vertices.length;

        // Check if there are duplicate points (consecutive vertices being the same)
        for (int i = 0; i < size; ++i) {
            for (int j = i + 1; j < size; ++j) {
                if (vertices[i].equals(vertices[j])) {
                    throw new IllegalArgumentException("Two consecutive vertices are the same point");
                }
            }
        }

        // Construct the plane from the first three vertices
        plane = new Plane(vertices[0], vertices[1], vertices[2]);

        // If the polygon is a triangle, no further checks are necessary
        if (size == 3) return;

        // Get the normal vector of the plane
        Vector n = plane.getNormal(vertices[0]);

        // Create two edges from the last three points of the polygon
        Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[size - 1]);

        // Determine the orientation of the polygon based on the cross product sign
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;

        // Iterate over the vertices to check if they are ordered and lie in the same plane
        for (int i = 1; i < size; ++i) {
            // Check that the vertex lies in the same plane
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");

            // Check orientation of each edge to ensure convexity (all edges must follow the same orientation)
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return plane.getNormal(point);  // Return the normal of the plane the polygon lies on
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        // First, check for intersection with the plane of the polygon
        List<Point> planeIntersections = plane.findIntersections(ray);
        if (planeIntersections == null) {
            return null;  // No intersection with the polygon's plane
        }

        Point p0 = ray.getHead();  // Ray's starting point
        Vector v = ray.getDirection();  // Ray's direction
        Point p = planeIntersections.get(0);  // The intersection point with the plane

        // Get vectors from the ray's start point to two vertices of the polygon
        Vector v1 = vertices.get(0).subtract(p0);
        Vector v2 = vertices.get(1).subtract(p0);

        // Get the normal to the first edge of the polygon
        Vector n = v1.crossProduct(v2).normalize();

        // Determine the sign of the dot product with the ray's direction
        double sign = alignZero(v.dotProduct(n));
        if (sign == 0) return null;  // The ray lies on the edge, no intersection

        boolean positive = sign > 0;

        // Check that all cross products with the ray's direction have the same sign
        for (int i = 1; i < size; ++i) {
            v1 = vertices.get(i).subtract(p0);
            v2 = vertices.get((i + 1) % size).subtract(p0);
            n = v1.crossProduct(v2).normalize();
            sign = alignZero(v.dotProduct(n));

            if (sign == 0 || (sign > 0) != positive) {
                return null;  // The ray intersects the plane but outside the polygon
            }
        }

        return List.of(p);  // The ray intersects inside the polygon
    }
}
