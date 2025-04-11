package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for Triangle class.
 */
class TriangleTest {
    /**
     * Tolerance for floating-point comparisons.
     */
    private static final double DELTA = 0.000001;

    Triangle triangle = new Triangle(new Point(1, 0, 0), new Point(0, 2, 0), new Point(0, 0, 3));
    Point p1 = new Point(0, 1, 1);

    /**
     * Test method for {@link geometries.Triangle#getNormal(Point)}.
     *
     * Test that the normal to the triangle is calculated correctly.
     * - Checks that the normal vector is orthogonal.
     * - Checks that the normal vector is normalized.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Check that the normal is orthogonal
        Vector expectedNormal = new Vector(6, 3, 2);
        assertThrows(IllegalArgumentException.class, () ->
                        triangle.getNormal(p1).crossProduct(expectedNormal),
                "The normal vector is not in the right direction");

        // TC02: Check that the normal is normalized
        assertEquals(1, triangle.getNormal(p1).length(), DELTA,
                "The normal vector isn't normalized");
    }

    /**
     * Test method for {@link geometries.Triangle#findIntersections(Ray)}.
     *
     * Test ray-triangle intersections:
     * - Ray intersects inside the triangle.
     * - Ray outside the triangle (against edge or vertex).
     * - Ray hits edge or vertex exactly.
     * - Ray continues beyond the triangle.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(
                new Point(0, 1, 0),
                new Point(1, -1, 0),
                new Point(-1, -1, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the triangle (1 point)
        Ray ray1 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        assertEquals(1, triangle.findIntersections(ray1).size(), DELTA,
                "There should be one intersection point between the ray and the triangle");

        // TC02: Ray is outside against edge (0 points)
        Ray ray2 = new Ray(new Point(2, 0, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray2), "Ray should not intersect the triangle");

        // TC03: Ray is outside against vertex (0 points)
        Ray ray3 = new Ray(new Point(0, 3, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray3), "Ray should not intersect the triangle");

        // =============== Boundary Values Tests ==================

        // TC04: Ray hits exactly on the edge of the triangle (0 points)
        Ray ray4 = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray4), "Ray should not intersect the triangle");

        // TC05: Ray hits exactly on a vertex of the triangle (0 points)
        Ray ray5 = new Ray(new Point(0, 1, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray5), "Ray should not intersect the triangle");

        // TC06: Ray continues the edge beyond its endpoint (0 points)
        Ray ray6 = new Ray(new Point(2, -3, -1), new Vector(0, 0, 1));
        assertNull(triangle.findIntersections(ray6), "Ray should not intersect the triangle");
    }
}
