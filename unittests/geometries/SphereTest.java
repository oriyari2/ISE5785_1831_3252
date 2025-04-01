package geometries;
import geometries.Sphere;
import primitives.Point;
import primitives.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
 */
class SphereTest {

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Test normal at a point on the surface of the sphere (radius = 1, center at (0, 0, 0)).
        Sphere sphere = new Sphere(1, new Point(0, 0, 0));
        Point pointOnSurface = new Point(1, 0, 0);
        Vector expectedNormal = new Vector(1, 0, 0).normalize();
        assertEquals(expectedNormal, sphere.getNormal(pointOnSurface),
                "ERROR: Incorrect normal at point on surface of sphere");

        // =============== Boundary Values Tests ==================
        // TC11: Test normal at a point on the opposite side of the sphere (radius = 1, center at (0, 0, 0)).
        Point pointOnOppositeSide = new Point(-1, 0, 0);
        expectedNormal = new Vector(-1, 0, 0).normalize();
        assertEquals(expectedNormal, sphere.getNormal(pointOnOppositeSide),
                "ERROR: Incorrect normal at point on opposite side of sphere");
    }
}
