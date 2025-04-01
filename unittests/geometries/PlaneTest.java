package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

/**
 * Testing Plane
 */
public class PlaneTest {

    /**
     * DELTA for test precision
     */
    private static final double DELTA = 0.000001;

    /**
     * Creating shared vectors for tests
     */
    private final Point p1 = new Point(1, 2, 3);
    private final Point p2 = new Point(4, 5, 6);
    private final Point p3 = new Point(7, 8, 10);

    private final Vector vec1 = p2.subtract(p1); // Vector from p1 to p2
    private final Vector vec2 = p3.subtract(p1); // Vector from p1 to p3

    private final Plane plane = new Plane(p1, p2, p3); // Creating the plane using points

    /**
     * Test method for {@link Plane#getNormal()}.
     */
    @Test
    void testGetNormal() {
        Vector normal = plane.getNormal();
        // ============ Equivalence Partitions Tests ==============
        validateNormal(normal);
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormalWithPoint() {
        Vector normal = plane.getNormal(p1);
        // ============ Equivalence Partitions Tests ==============
        validateNormal(normal);
    }

    /**
     * Helper method to test normal vector validity
     *============ Equivalence Partitions Tests ==============
     */    private void validateNormal(Vector normal) {
        // Ensure normal is orthogonal to the plane's vectors
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");

        // Ensure normal is a unit vector
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");
    }

    /**
     * Helper method to test plane constructor validity
     *============ Equivalence Partitions Tests ==============
     */
    private void validatePlaneConstructor(Plane plane, Vector vec1, Vector vec2) {
        Vector normal = plane.getNormal();
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)} constructor.
     */
    @Test
    void testCtorThreePoints() {
        Plane plane = new Plane(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============
        validatePlaneConstructor(plane, vec1, vec2);

        // Ensure cross product length is different from 1
        Vector crossProduct = vec1.crossProduct(vec2);
        assertNotEquals(1, crossProduct.length(), "Cross product length should not be 1");

        // =============== Boundary Values Tests ==================

        // TC01: Two identical points (p1 and p2)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p3),
                "Constructor should throw an exception for two identical points (p1, p1, p3)");

        // TC02: Two identical points (p1 and p3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p2),
                "Constructor should throw an exception for two identical points (p1, p2, p2)");

        // TC03: Two identical points (p2 and p3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p3, p3),
                "Constructor should throw an exception for two identical points (p1, p3, p3)");

        // TC04: All three points identical
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p1),
                "Constructor should throw an exception for three identical points");

        // TC05: All points on the same line
        Point p4 = new Point(10, 11, 12);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p4),
                "Constructor should throw an exception when all points are collinear");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)} constructor.
     */
    @Test
    void testCtorPointNormal() {
        Vector normal = vec1.crossProduct(vec2).normalize();
        Plane plane = new Plane(p1, normal);
        validatePlaneConstructor(plane, vec1, vec2);
    }
}
