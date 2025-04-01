package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import geometries.Cylinder;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for {@link geometries.Cylinder} class.
 */
class CylinderTest {

    /**
     * Small delta value for floating-point comparison.
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     * Checks the normal calculation for different cases on the cylinder.
     */
    @Test
    void testGetNormal() {
        // Create a cylinder with radius 5 and height 5, along the Z-axis
        Cylinder cyl = new Cylinder(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 5, 5);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Point is on the side of the cylinder
        assertEquals(new Vector(0, -1, 0), cyl.getNormal(new Point(0, -5, 2)),
                "ERROR: Cylinder.getNormal() does not work correctly (point on the side)");

        // TC02: Point is on the top base of the cylinder
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0, 2, 5)),
                "ERROR: Cylinder.getNormal() does not work correctly (point on top)");

        // TC03: Point is on the bottom base of the cylinder
        assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0, 2, 0)),
                "ERROR: Cylinder.getNormal() does not work correctly (point on the bottom)");

        // =============== Boundary Values Tests ==================
        // TC04: Point is on the edge between the side and the top base
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0, 5, 5)),
                "ERROR: Cylinder.getNormal() does not work correctly (edge of top base)");

        // TC05: Point is on the edge between the side and the bottom base
        assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(-5, 0, 0)),
                "ERROR: Cylinder.getNormal() does not work correctly (edge of bottom base)");

        // Additional boundary tests:
        // TC06: Point is at the center of the top base
        assertEquals(new Vector(0, 0, 1), cyl.getNormal(new Point(0.1, 0.1, 5)),
                "ERROR: Cylinder.getNormal() does not work correctly (center of top base)");

        // TC07: Point is at the center of the bottom base
        assertEquals(new Vector(0, 0, -1), cyl.getNormal(new Point(0.1, 0.1, 0)),
                "ERROR: Cylinder.getNormal() does not work correctly (center of bottom base)");
    }
}