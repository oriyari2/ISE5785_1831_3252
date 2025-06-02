package geometries;

import java.util.List;
import static primitives.Util.*;
import primitives.*;

/**
 * Polygon class represents a two-dimensional convex polygon in 3D Cartesian coordinate system.
 * The polygon must be defined by at least three ordered, coplanar, and non-collinear vertices.
 * The polygon is assumed to be convex.
 */
public class Polygon extends Geometry {
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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        // Intersect the ray with the polygon's plane
        List<Intersection> planeIntersections = plane.calculateIntersectionsHelper(ray);
        if (planeIntersections == null) return null;

        Point p = planeIntersections.get(0).point; // intersection point with the plane
        Vector n = plane.getNormal(null);           // polygon's normal
        int size = vertices.size();

        // If the ray starts exactly on the polygon — no intersection
        if (ray.getHead().equals(p)) {
            return null;
        }

        Boolean positive = null; // to track orientation consistency

        for (int i = 0; i < size; i++) {
            Point vCurrent = vertices.get(i);
            Point vNext = vertices.get((i + 1) % size);

            // Check if p equals vCurrent to avoid zero vector creation
            if (p.equals(vCurrent)) {
                // Intersection is exactly on a vertex — no intersection by requirement
                return null;
            }

            Vector edge = vNext.subtract(vCurrent);

            // Now safe to subtract because p != vCurrent
            Vector edgeToP;
            try {
                edgeToP = p.subtract(vCurrent);
            } catch (IllegalArgumentException e) {
                // subtract resulted in zero vector — no intersection
                return null;
            }

            // Check for zero edge vector (should never be zero in a valid polygon)
            if (Util.isZero(edge.length())) {
                return null; // invalid polygon edge
            }

            Vector cross;
            try {
                cross = edge.crossProduct(edgeToP);
            } catch (IllegalArgumentException e) {
                // crossProduct resulted in zero vector - no intersection
                return null;
            }

            // If cross is zero vector, point lies on the edge line
            if (Util.isZero(cross.length())) {
                double t = edgeToP.dotProduct(edge) / edge.lengthSquared();
                if (t >= 0 && t <= 1) {
                    // point is exactly on an edge — no intersection
                    return null;
                }
            }

            double sign = cross.dotProduct(n);

            if (positive == null) {
                positive = (sign > 0) || Util.isZero(sign);
            } else if (((sign > 0) || Util.isZero(sign)) != positive) {
                // point is outside polygon
                return null;
            }
        }

        // If all tests passed — point is inside polygon, return intersection object
        return List.of(new Intersection(this, p));
    }

}
