package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
/**
 * Unit tests for {@link geometries.Triangle} class.
 */
class TriangleTest {
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
        Triangle triangle = new Triangle(new Point(1, 0, 0), new Point(0, 2, 0), new Point(0, 0, 3));
        Vector expectedNormal = new Vector(6, 3, 2);
        assertThrows(IllegalArgumentException.class,()->triangle.getNormal(new Point(0, 1, 1)).crossProduct(expectedNormal),
                "The normal vector is not in the right direction");

        assertEquals(1, triangle.getNormal(new Point(0, 1, 1)).length(), 1e-10,
                "The normal vector isn't normalized");
    }
    @Test
    void testFindIntersections() {
    }
}