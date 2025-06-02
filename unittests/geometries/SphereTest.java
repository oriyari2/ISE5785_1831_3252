package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Default constructor for PolygonTest.
 */
class SphereTest {
    /**
     * DELTA for test precision
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     * Checks the normal calculation for different cases on the sphere.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Normal at a point on the sphere surface (radius = 1, center at (0, 0, 0))
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);

        Vector expectedNormal = p100.subtract(new Point(0, 0, 0)).normalize();
        assertEquals(expectedNormal, sphere.getNormal(p100),
                "ERROR: Incorrect normal at point on surface of sphere");

        // =============== Boundary Values Tests ==================
        // TC11: Normal at a point on the opposite side of the sphere
        Vector expectedNormalOpposite = p001.subtract(new Point(0, 0, 0)).normalize();
        assertEquals(expectedNormalOpposite, sphere.getNormal(p001),
                "ERROR: Incorrect normal at point on opposite side of sphere");
    }

    /** A point used in some tests */
    private final Point p001 = new Point(0, 0, 1);
    /** A point used in some tests */
    private final Point p100 = new Point(1, 0, 0);
    /** A vector used in some tests */
    private final Vector v001 = new Vector(0, 0, 1);
    /**
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Sphere sphere = new Sphere(p100, 1d);
        final Point gp1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        final Point gp2 = new Point(1.53484692283495, 0.844948974278318, 0);
        final var exp = List.of(gp1, gp2);
        final Vector v310 = new Vector(3, 1, 0);
        final Vector v110 = new Vector(1, 1, 0);
        final Point p01 = new Point(-1, 0, 0);
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray's line out of sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        final var result1 = sphere.findIntersections(new Ray(p01, v310));
        assertNotNull(result1, "Can't be empty list");
        assertEquals(2, result1.size(), "Wrong number of points");
        assertEquals(exp, result1, "Ray crosses sphere");

        // TC03: Ray starts inside the sphere (1 point)
        Ray rayInside = new Ray(new Point(1, 0.5, 0), new Vector(1, 0, 0));
        var result2 = sphere.findIntersections(rayInside);
        assertNotNull(result2, "Ray from inside should intersect once");
        assertEquals(1, result2.size(), "Wrong number of points");

        // TC04: Ray starts after the sphere (0 points)
        Ray rayAfter = new Ray(new Point(3, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(rayAfter), "Ray after sphere should return null");

        // =============== Boundary Values Tests ==================
        // Group 1: Ray's line crosses the sphere (not through center)
        // TC11: Ray starts at sphere and goes inside (1 point)
        Ray rayFromSurfaceIn = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        var result3 = sphere.findIntersections(rayFromSurfaceIn);
        assertNotNull(result3);
        assertEquals(1, result3.size(), "Wrong number of points from surface inward");

        // TC12: Ray starts at sphere and goes outside (0 points)
        Ray rayFromSurfaceOut = new Ray(new Point(0, 0, 0), new Vector(-1, 0, 0));
        assertNull(sphere.findIntersections(rayFromSurfaceOut), "Ray from surface outward should miss");

        // Group 2: Ray through the center
        // TC21: Ray starts before and goes through center (2 points)
        Ray rayThroughCenter = new Ray(new Point(-1, 0, 0), new Vector(2, 0, 0));
        var result4 = sphere.findIntersections(rayThroughCenter);
        assertNotNull(result4);
        assertEquals(2, result4.size(), "Ray through center should intersect twice");

        // TC22: Ray starts at sphere and goes through center (1 point)
        Ray rayFromSurfaceThroughCenter = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        var result5 = sphere.findIntersections(rayFromSurfaceThroughCenter);
        assertNotNull(result5);
        assertEquals(1, result5.size(), "Ray from surface to center should intersect once");

        // TC23: Ray starts inside and goes through center (1 point)
        Ray rayInsideCenter = new Ray(new Point(1.25, 0, 0), new Vector(-1, 0, 0));
        var result6 = sphere.findIntersections(rayInsideCenter);
        assertNotNull(result6);
        assertEquals(1, result6.size(), "Ray from inside through center should intersect once");

        // TC24: Ray starts at center (1 point)
        Ray rayFromCenter = new Ray(p100, new Vector(0, 1, 0));
        var result7 = sphere.findIntersections(rayFromCenter);
        assertNotNull(result7);
        assertEquals(1, result7.size(), "Ray from center should intersect once");

        // TC25: Ray starts at sphere and goes away through center (0 points)
        Ray rayFromSurfaceOutCenter = new Ray(new Point(2, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(rayFromSurfaceOutCenter));

        // TC26: Ray starts after sphere (0 points)
        Ray rayAfterCenter = new Ray(new Point(3, 0, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(rayAfterCenter));

        // Group 3: Tangent rays
        // TC31: Ray starts before tangent point
        Ray tangentBefore = new Ray(new Point(0, 1, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(tangentBefore));

        // TC32: Ray starts at tangent point
        Ray tangentAt = new Ray(new Point(1, 1, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(tangentAt));

        // TC33: Ray starts after tangent point
        Ray tangentAfter = new Ray(new Point(2, 1, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(tangentAfter));

        // Group 4: Special cases
        // TC41: Ray is outside and orthogonal to line from ray start to center
        Ray orthogonalOutside = new Ray(new Point(0, 2, 0), new Vector(1, 0, 0));
        assertNull(sphere.findIntersections(orthogonalOutside));

        // TC42: Ray starts inside, orthogonal to center direction (should intersect)
        Ray orthogonalInside = new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0));
        var result8 = sphere.findIntersections(orthogonalInside);
        assertNotNull(result8);
        assertEquals(1, result8.size(), "Ray inside and orthogonal should intersect once");
    }

}
