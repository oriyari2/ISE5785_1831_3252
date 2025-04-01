package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for Cylinder class.
 */
class CylinderTest {

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Cylinder cyl = new Cylinder(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 5, 5);
        // ============ Equivalence Partitions Tests ==============
        // TC01: point is on the side (like in tube): Test that the result of the normal vector of the point is proper
        assertEquals(cyl.getNormal(new Point(0, -5, 2)), new Vector(0, -1, 0), "ERROR: Cylinder.getNormal() does not work correctly(point on the side)");
        // TC02: point is on the top: Test that the result of the normal vector of the point is proper
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0, 2, 5)), "ERROR: Cylinder.getNormal() does not work correctly(point on top)");
        // TC03: point is on the bottom: Test that the result of the normal vector of the point is proper
        assertEquals(cyl.getNormal(new Point(0, 2, 0)), new Vector(0, 0, -1), "ERROR: Cylinder.getNormal() does not work correctly(point is on the bottom)");

        // tests for the edges points:
        // =============== Boundary Values Tests ==================
        // TC04: point is on the edge between the side and the top: Test that the points of the Plane are all different
        assertEquals(cyl.getNormal(new Point(0, 5, 5)), new Vector(0, 0, 1), "ERROR: Cylinder.getNormal() does not work correctly");
        // TC05: point is on the edge between the side and the bottom: Test that the points of the Plane are all different
        assertEquals(cyl.getNormal(new Point(-5, 0, 0)), new Vector(0, 0, -1), "ERROR: Cylinder.getNormal() does not work correctly");

        // Additional boundary tests:
        // TC06: point is at the center of the top base
        assertEquals(cyl.getNormal(new Point(0.1, 0.1, 5)), new Vector(0, 0, 1), "ERROR: Cylinder.getNormal() does not work correctly (center of top base)");
        // TC07: point is at the center of the bottom base
        assertEquals(cyl.getNormal(new Point(0.1, 0.1, 0)), new Vector(0, 0, -1), "ERROR: Cylinder.getNormal() does not work correctly (center of bottom base)");
    }
}