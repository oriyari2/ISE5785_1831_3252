package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ray class.
 */
class RayTest {

    /**
     * Test method for {@link Ray#getPoint(double)}.
     * This method tests the functionality of retrieving a point on the ray
     * at a given distance from the ray's head.
     */
    @Test
    void testGetPoint() {

        // ============ Equivalence Partitions Tests ==============
        // TC01: Positive distance
        Ray ray1 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(new Point(1, 2, 6), ray1.getPoint(3), "getPoint() failed for positive t");

        // TC02: Negative distance
        Ray ray2 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(new Point(1, 2, 1), ray2.getPoint(-2), "getPoint() failed for negative t");

        // =============== Boundary Values Tests ==================
        // TC11: Zero distance
        Ray ray3 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(ray3.getHead(), ray3.getPoint(0), "getPoint() failed for t = 0 (should return head)");
    }

    @Test
    void testFindClosestPoint() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Closest point is in the middle
        Ray ray3 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        List<Point> points3 = List.of(new Point(1, 2, 6), new Point(1, 2, 4), new Point(1, 2, 5));
        assertEquals(new Point(1, 2, 4), ray3.findClosestPoint(points3), "findClosestPoint() failed for closest point in the middle");

        // =============== Boundary Values Tests ==================

        // TC11: Closest point is the last point
        Ray ray2 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        List<Point> points2 = List.of(new Point(1, 2, 6), new Point(1, 2, 5), new Point(1, 2, 4));
        assertEquals(new Point(1, 2, 4), ray2.findClosestPoint(points2), "findClosestPoint() failed for closest point at index last");

        // TC12: Closest point is the first point
        Ray ray1 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        List<Point> points1 = List.of(new Point(1, 2, 4), new Point(1, 2, 5), new Point(1, 2, 6));
        assertEquals(new Point(1, 2, 4), ray1.findClosestPoint(points1), "findClosestPoint() failed for closest point at index 0");

        // TC13: Empty list
        Ray ray4 = new Ray(new Point(1, 2, 3), new Vector(0, 0, -1));
        List<Point> points4 = List.of();
        assertNull(ray4.findClosestPoint(points4), "findClosestPoint() failed for empty list");
    }
}