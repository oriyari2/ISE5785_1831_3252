package geometries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Plane;
import geometries.Polygon;
import primitives.*;

/**
 * Unit tests for Polygon class.
 */
class PolygonTest {
    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /** Test method for {@link geometries.Polygon#Polygon(primitives.Point...)}. */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct concave quadrangular with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(-1, 1, 1)),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(0, 1, 0),
                        new Point(1, 0, 0),
                        new Point(-1, 1, 1)), //
                "Constructed a polygon with wrong order of vertices");

        // TC03: Not in the same plane
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(0, 2, 2)), //
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(0.5, 0.25, 0.5)), //
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC10: Vertex on a side of a quadrangular
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(0, 0.5, 0.5)),
                "Constructed a polygon with vertix on a side");

        // TC11: Last point = first point
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(0, 0, 1)),
                "Constructed a polygon with vertice on a side");

        // TC12: Co-located points
        assertThrows(IllegalArgumentException.class, //
                () -> new Polygon(new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(0, 1, 0)),
                "Constructed a polygon with vertice on a side");

    }

    /** Test method for {@link geometries.Polygon#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: There is a simple single test here - using a quad
        Point[] pts =
                {new Point(0, 0, 1),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0),
                        new Point(-1, 1, 1)};
        Polygon pol = new Polygon(pts);
        // ensure there are no exceptions
        assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
        // generate the test result
        Vector result = pol.getNormal(new Point(0, 0, 1));
        // ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
        // ensure the result is orthogonal to all the edges
        for (int i = 0; i < 3; ++i)
            assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
                    "Polygon's normal is not orthogonal to one of the edges");
    }

    /**
     * Test method for {@link geometries.Polygon#findIntersections(primitives.Ray)}.
     * Tests various cases for ray intersection with the polygon.
     */
    @Test
    void testFindIntersections() {
        Polygon polygon = new Polygon(
                new Point(1, 0, 0),
                new Point(0, 2, 0),
                new Point(-1, 1, 0),
                new Point(-1, -1, 0)
        );

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the polygon (1 point)
        Ray ray1 = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));
        List<Point> result1 = polygon.findIntersections(ray1);
        assertNotNull(result1, "Result should not be null");
        assertEquals(1, result1.size(), "Ray should intersect the polygon");
        assertEquals(new Point(0, 0, 0), result1.get(0), "Wrong intersection point");

        // TC02: Ray intersects outside the polygon (0 points)
        Ray ray2 = new Ray(new Point(2, 2, -1), new Vector(0, 0, 1));
        List<Point> result2 = polygon.findIntersections(ray2);
        assertTrue(result2 == null || result2.isEmpty(), "Ray should not intersect the polygon");

        // TC03: Ray is parallel and does not intersect (0 points)
        Ray ray3 = new Ray(new Point(0, 0, 1), new Vector(1, 0, 0));
        List<Point> result3 = polygon.findIntersections(ray3);
        assertTrue(result3 == null || result3.isEmpty(), "Ray should not intersect the polygon");

        // =============== Boundary Values Tests ==================

        // TC04: Ray hits exactly on the edge of the polygon (0 points)
        Ray ray4 = new Ray(new Point(0.5, 1, -1), new Vector(0, 0, 1));
        List<Point> result4 = polygon.findIntersections(ray4);
        assertTrue(result4 == null || result4.isEmpty(), "Ray on edge should not intersect");

        // TC05: Ray hits exactly on a vertex of the polygon (0 points)
        Ray ray5 = new Ray(new Point(1, 0, -1), new Vector(0, 0, 1));
        List<Point> result5 = polygon.findIntersections(ray5);
        assertTrue(result5 == null || result5.isEmpty(), "Ray on vertex should not intersect");

        // TC06: Ray continues the extension of an edge (0 points)
        Ray ray6 = new Ray(new Point(2, -2, -1), new Vector(0, 0, 1));
        List<Point> result6 = polygon.findIntersections(ray6);
        assertTrue(result6 == null || result6.isEmpty(), "Ray along edge extension should not intersect");
    }


}