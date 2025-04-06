package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
/**
 * Unit tests for {@link geometries.Triangle} class.
 */
class TriangleTest {
    private static final double DELTA = 0.000001;
    Triangle triangle = new Triangle(new Point(1, 0, 0), new Point(0, 2, 0), new Point(0, 0, 3));
    Point p1 = new Point(0, 1, 1);

    /**
     * Test method for {@link geometries.Triangle#getNormal(Point)}.
     *
     * This test verifies that the normal calculation of a Triangle is correct
     * and properly normalized.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: test to see that the getNormal function works correctly
        Vector expectedNormal = new Vector(6, 3, 2);
        assertThrows(IllegalArgumentException.class,()->triangle.getNormal(p1).crossProduct(expectedNormal),
                "The normal vector is not in the right direction");

        assertEquals(1, triangle.getNormal(p1).length(), DELTA,
                "The normal vector isn't normalized");
    }


    @Test
    void testFindIntersections() {
        Polygon polygon = new Polygon(
                new Point(1, 0, 0),
                new Point(0, 2, 0),
                new Point(-1, 1, 0),
                new Point(-1, -1, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the polygon (1 point)
        Ray ray1 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        assertEquals(1, polygon.findIntersections(ray1).size(), DELTA,
                "There should be one intersection point between the ray and the polygon");

        // TC02: Ray is outside against edge (0 points)
        Ray ray2 = new Ray(new Point(2, 2, -1), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray2), "Ray should not intersect the polygon");

        // TC03: Ray is parallel to the polygon's plane and doesn't intersect (0 points)
        Ray ray3 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        assertNull(polygon.findIntersections(ray3), "Ray should not intersect the polygon");

        // =============== Boundary Values Tests ==================

        // TC04: Ray hits exactly on the edge of the polygon (0 points)
        Ray ray4 = new Ray(new Point(0.5, 1, -1), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray4), "Ray should not intersect the polygon");

        // TC05: Ray hits exactly on a vertex of the polygon (0 points)
        Ray ray5 = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray5), "Ray should not intersect the polygon");

        // TC06: Ray continues the edge beyond its endpoint (0 points)
        Ray ray6 = new Ray(new Point(2, -2, -1), new Vector(0, 0, 1));
        assertNull(polygon.findIntersections(ray6), "Ray should not intersect the polygon");
    }

}