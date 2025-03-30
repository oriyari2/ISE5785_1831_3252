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
        Cylinder cylinder = new Cylinder(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 1, 5);

        // TC01: Test the normal at a point on the side of the cylinder
        Point pointOnSide = new Point(1, 0, 2);
        Vector expectedNormal = new Vector(1, 0, 0).normalize();
        assertEquals(expectedNormal, cylinder.getNormal(pointOnSide),
                "Incorrect normal at side of cylinder");

        // TC02: Test the normal at a point on the top base of the cylinder
        Point pointOnTopBase = new Point(0, 0, 5);
        expectedNormal = new Vector(0, 0, 1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnTopBase),
                "Incorrect normal at top base of cylinder");

        // TC03: Test the normal at a point on the bottom base of the cylinder
        Point pointOnBottomBase = new Point(0, 0, 0);
        expectedNormal = new Vector(0, 0, -1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnBottomBase),
                "Incorrect normal at bottom base of cylinder");

        // TC04: Test the normal at a point on the edge of the bottom base
        Point pointOnBottomEdge = new Point(1, 0, 0);
        expectedNormal = new Vector(0, 0, -1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnBottomEdge),
                "Incorrect normal at bottom edge of cylinder");

        // TC05: Test the normal at a point on the edge of the top base
        Point pointOnTopEdge = new Point(1, 0, 5);
        expectedNormal = new Vector(0, 0, 1);
        assertEquals(expectedNormal, cylinder.getNormal(pointOnTopEdge),
                "Incorrect normal at top edge of cylinder");
    }
}