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
    private static final double delta = 1e-6;

    /**
     * Creating shared vectors for tests
     */
    private final Point p1 = new Point(1, 0, 0);
    private final Point p2 = new Point(0, 1, 0);
    private final Point p3 = new Point(0, 0, 1);

    private final Vector vec1 = new Vector(1, 0, 0); // Vector from p1 to p2
    private final Vector vec2 = new Vector(0, 1, 0); // Vector from p1 to p3

    private final Plane plane = new Plane(p1, p2, p3); // Creating the plane using points

    /**
     * Test method for {@link Plane#getNormal()}.
     */
    @Test
    void testGetNormal() {
        Vector normal = plane.getNormal();

        // TC01: Ensure normal is orthogonal to the plane's vectors
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");

        // TC02: Ensure normal is a unit vector
        assertEquals(1, normal.length(), delta, "Normal vector is not a unit vector");

        // TC03: Ensure normal is the same as the cross product of the plane's vectors

        // Calculate the cross product of the plane's vectors
        Vector crossProduct = vec1.crossProduct(vec2);

     }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormalWithPoint() {
        Vector normalFromPoint = plane.getNormal(p1);

        // TC01: Ensure normal from Point is same as normal from Plane
        assertEquals(plane.getNormal(), normalFromPoint, "Normal vector from point is not the same as normal from plane");

        // TC02: Ensure normal from point is a unit vector
        assertEquals(1, normalFromPoint.length(), delta, "Normal vector from point is not of unit length");

        // TC03: Ensure normal from point is orthogonal to the plane's vectors
        assertEquals(0, normalFromPoint.dotProduct(vec1), "Normal from point is not orthogonal to first vector");
        assertEquals(0, normalFromPoint.dotProduct(vec2), "Normal from point is not orthogonal to second vector");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)} constructor.
     */
    @Test
    void testCtorThreePoints() {
        // =============== Boundary Values Tests ==================

        // TC01: Ensure normal is orthogonal to the plane's vectors and unit length
        Plane plane = new Plane(p1, p2, p3);
        Vector normal = plane.getNormal();

        // Check orthogonality and length
        assertEquals(1, normal.length(), delta, "Normal vector is not a unit vector");
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");

        // TC02: Check the direction of the normal (test for reverse direction)
        Vector normalReverse = plane.getNormal();
        assertNotEquals(normal, normalReverse, "Normal vector direction is not correct (should be reverse possible)");
    }
}
