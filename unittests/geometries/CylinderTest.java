package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Testing Cylinder
 */
public class CylinderTest {

    /**
     * DELTA for test precision
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Setup =============
        Cylinder cylinder = new Cylinder(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 1, 5);

        // ============ Equivalence Partitions Tests =============
        // TC01: Test normal on the side surface of the cylinder
        Point pointOnSide = new Point(1, 0.5, 2);  // Slightly moved to avoid zero vector case
        Vector expectedNormal = new Vector(1, 0, 0).normalize();
        assertEquals(expectedNormal, cylinder.getNormal(pointOnSide), "Incorrect normal at cylinder side");

        // TC02: Test normal on the top base
        Point pointOnTopBase = new Point(0.5, 0.5, 5);
        expectedNormal = new Vector(0, 0, 1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnTopBase), "Incorrect normal at top base");

        // TC03: Test normal on the bottom base
        Point pointOnBottomBase = new Point(0.5, -0.5, 0);
        expectedNormal = new Vector(0, 0, -1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnBottomBase), "Incorrect normal at bottom base");

        // =============== Boundary Values Tests ==================
        // TC04: Test normal at the center of the top base
        Point centerTop = new Point(0.1, 0.1, 5);  // Moved slightly from the center
        expectedNormal = new Vector(0, 0, 1);
        assertEquals(expectedNormal, cylinder.getNormal(centerTop), "Incorrect normal at center of top base");

        // TC05: Test normal at the center of the bottom base
        Point centerBottom = new Point(-0.1, -0.1, 0); // Moved slightly from the center
        expectedNormal = new Vector(0, 0, -1);
        assertEquals(expectedNormal, cylinder.getNormal(centerBottom), "Incorrect normal at center of bottom base");

        // TC06: Test normal at the edge between the side and the top base
        Point edgeTop = new Point(0.99, 0.01, 5);  // Slightly moved
        expectedNormal = new Vector(0, 0, 1);
        assertEquals(expectedNormal, cylinder.getNormal(edgeTop), "Incorrect normal at edge of top base");

        // TC07: Test normal at the edge between the side and the bottom base
        Point edgeBottom = new Point(0.99, -0.01, 0);  // Slightly moved
        expectedNormal = new Vector(0, 0, -1);
        assertEquals(expectedNormal, cylinder.getNormal(edgeBottom), "Incorrect normal at edge of bottom base");
    }
}