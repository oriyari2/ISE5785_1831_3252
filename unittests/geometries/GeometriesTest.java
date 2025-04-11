package geometries;

import geometries.*;
import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A list of geometries in the scene.
 */
class GeometriesTest {

    /**
     * Test method for {@link geometries.Geometries#findIntersections(Ray)}.
     *
     * This test checks the behavior of the findIntersections method
     * under different scenarios:
     * - Empty collection
     * - No shapes are intersected
     * - Only one shape is intersected
     * - Some but not all shapes are intersected
     * - All shapes are intersected
     *
     * The number of intersection points is checked, not their values.
     */
    @Test
    void testFindIntersections() {
        // Geometries for testing
        Sphere sphere = new Sphere(1, new Point(0, 0, 3));
        Plane plane = new Plane(new Point(0, 0, 3), new Vector(0, 0, 1));
        Triangle triangle = new Triangle(
                new Point(1, 1, 3),
                new Point(-1, 1, 3),
                new Point(0, -1, 3)
        );
        Polygon polygon = new Polygon(
                new Point(1, 1, 3),
                new Point(1, -1, 3),
                new Point(-1, -1, 3),
                new Point(-1, 1, 3)
        );

        Ray ray = new Ray(new Point(0, 0, -1), new Vector(0, 0, 1));

        // ============ Equivalence Partition Test ==============

        // TC01: Some but not all geometries are intersected
        Geometries someHitGeometries = new Geometries(
                sphere,        // 2 intersections
                triangle,      // 1 intersection
                new Plane(new Point(0, 0, -5), new Vector(1, 0, 0)) // no intersection
        );
        List<Point> intersections = someHitGeometries.findIntersections(ray);
        assertNotNull(intersections, "Expected intersections");
        assertEquals(3, intersections.size(), "Expected 3 intersection points");

        // ============ Boundary Values Tests ==============

        // TC11: Empty collection
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(ray),
                "Empty geometries collection should return null");

        // TC12: No shape is intersected
        Geometries geometriesNoIntersection = new Geometries(
                new Sphere(1, new Point(0, 0, -3)), // behind ray
                new Plane(new Point(0, 0, -5), new Vector(0, 1, 0)), // parallel
                new Polygon( // behind ray
                        new Point(1, 1, -3),
                        new Point(1, -1, -3),
                        new Point(-1, -1, -3),
                        new Point(-1, 1, -3)
                )
        );
        assertNull(geometriesNoIntersection.findIntersections(ray),
                "Ray shouldn't intersect any geometry");

        // TC13: Only one shape is intersected
        Geometries oneHitGeometries = new Geometries(
                new Sphere(1, new Point(0, 0, -3)), // no intersection
                triangle // 1 intersection
        );
        assertEquals(1, oneHitGeometries.findIntersections(ray).size(),
                "Expected one intersection point with triangle");

        // TC14: All geometries are intersected
        Geometries allHitGeometries = new Geometries(
                sphere,   // 2 points
                plane,    // 1 point
                triangle, // 1 point
                polygon   // 1 point
        );
        intersections = allHitGeometries.findIntersections(ray);
        assertNotNull(intersections, "Expected intersections");
        assertEquals(5, intersections.size(), "Expected 5 intersection points");
    }
}
