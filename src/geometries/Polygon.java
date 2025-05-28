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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray)
    {
        List<Intersection> planeIntersections = plane.calculateIntersections(ray);
        if (planeIntersections == null) return null;  // No intersection with the plane

        Intersection p = planeIntersections.get(0);  // Get the intersection point with the plane

        Point p0 = ray.getHead();
        Vector dir = ray.getDirection();

        // Create vectors from the first vertex to the intersection point
        Vector v1p = vertices.get(0).subtract(p0);
        Vector v2p = vertices.get(1).subtract(p0);
        Vector v3p = vertices.get(2).subtract(p0);

        // Normals to triangle's edges
        Vector n1 = v1p.crossProduct(v2p).normalize();
        Vector n2 = v2p.crossProduct(v3p).normalize();
        Vector n3 = v3p.crossProduct(v1p).normalize();

        // Check if the intersection point is on the same side of all edges
        double s1 = alignZero(dir.dotProduct(n1));
        double s2 = alignZero(dir.dotProduct(n2));
        double s3 = alignZero(dir.dotProduct(n3));

        // If all have the same sign (all positive or all negative), the point is inside
        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            return List.of(new Intersection(this, p));  // Return the intersection point wrapped in an Intersection object
        }

        return null;  // No valid intersection within the polygon
    }
}
