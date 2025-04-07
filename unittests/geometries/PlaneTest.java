package geometries;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for Plane class.
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
     * Test method for {@link Plane# getNormal()}.
     */
    @Test
    void testGetNormal() {
        Vector normal = plane.getNormal(p1);
        // ============ Equivalence Partitions Tests ==============
        // Ensure normal is orthogonal to the plane's vectors
        assertTrue(normal.dotProduct(vec1) < DELTA, "Normal is not orthogonal to first vector");
        assertTrue(normal.dotProduct(vec2) < DELTA, "Normal is not orthogonal to second vector");

        // Ensure normal is a unit vector
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");
    }


    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormalWithPoint() {
        Vector normal = plane.getNormal(p1);
        // ============ Equivalence Partitions Tests ==============
        // Ensure normal is orthogonal to the plane's vectors
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");

        // Ensure normal is a unit vector
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)} constructor.
     */
    @Test
    void testCtorThreePoints() {
        Plane plane = new Plane(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============
        Vector normal = plane.getNormal(p1);
        assertTrue(normal.dotProduct(vec1) < DELTA, "Normal is not orthogonal to first vector");
        assertTrue(normal.dotProduct(vec2) < DELTA, "Normal is not orthogonal to second vector");
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");

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
        Point p4 = new Point(10, 11, 14);  // Changing Z-coordinate to make it non-collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p4),
                "Constructor should throw an exception when all points are collinear");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)} constructor.
     */
    @Test
    void testCtorPointNormal() {
        Vector normal = vec1.crossProduct(vec2).normalize();
        assertEquals(0, normal.dotProduct(vec1), "Normal is not orthogonal to first vector");
        assertEquals(0, normal.dotProduct(vec2), "Normal is not orthogonal to second vector");
        assertEquals(1, normal.length(), DELTA, "Normal vector is not a unit vector");
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: A ray that is not parallel or perpendicular to the plane intersects the plane
        Ray ray1 = new Ray(new Point(0, 0, 0), new Vector(3, 4, 3));
        assertEquals(1, plane.findIntersections(ray1).size(), "Ray should intersect the plane at one point");

        // TC02: A ray that does not intersect the plane
        Ray ray2 = new Ray(new Point(0, 0, 0), new Vector(1, 1, 0));
        assertNull(plane.findIntersections(ray2), "Ray should not intersect the plane");

        // =============== Boundary Values Tests ==================

        // TC11: The ray is contained in the plane
        Ray ray3 = new Ray(p1, vec1);
        assertNull(plane.findIntersections(ray3), "Ray should not intersect the plane");

        // TC12: The ray is parallel to the plane and not contained in it
        Ray ray4 = new Ray(new Point(0, 0, 1), new Vector(1, 1, 0));
        assertNull(plane.findIntersections(ray4), "Ray should not intersect the plane");

        // TC13: The ray is perpendicular to the plane and starts before it
        Vector normal = plane.getNormal(p1);
        Ray ray5 = new Ray(new Point(0, 0, -1), normal.scale(-1));
        assertEquals(1, plane.findIntersections(ray5).size(), "Ray should intersect the plane at one point");

        // TC14: The ray is perpendicular to the plane and starts on it
        Ray ray6 = new Ray(p1, normal);
        assertNull(plane.findIntersections(ray6), "Ray should not intersect the plane");

        // TC15: The ray is perpendicular to the plane and starts after it
        Ray ray7 = new Ray(new Point(0, 0, 10), normal.scale(-1));
        assertNull(plane.findIntersections(ray7), "Ray should not intersect the plane");

        // TC16: The ray begins at the same point as the plane
        Ray ray8 = new Ray(plane.getPoint(), new Vector(1, 2, 3));
        assertNull(plane.findIntersections(ray8), "Ray should not intersect the plane");

        // TC17: The ray begins on the plane
        Ray ray9 = new Ray(p1, new Vector(1, 2, 3));
        assertNull(plane.findIntersections(ray9), "Ray should not intersect the plane");
    }
}
