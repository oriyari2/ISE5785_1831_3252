package renderer;

import geometries.*;
import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests: verifying ray intersections with geometric shapes constructed from the camera
 */
public class CameraIntersectionsIntegrationTests {

    /// Builder for initializing cameras in tests
    private final Camera.Builder camBuilder = Camera.getBuilder()
            .setDirection(new Point(0, 0, -1), Vector.MINUS_Y)
            .setVpSize(3, 3)
            .setVpDistance(1);

    /// Camera positioned at origin
    private final Camera camOrigin = camBuilder.setLocation(Point.ZERO).build();
    /// Camera slightly in front of origin
    private final Camera camOffset = camBuilder.setLocation(new Point(0, 0, 0.5)).build();

    /**
     * Helper function to test the total number of intersections between a shape and rays from the camera
     * @param cam the camera used to construct rays
     * @param shape the geometric shape to intersect
     * @param expectedCount expected number of intersection points
     */
    private void assertIntersectionCount(Camera cam, Intersectable shape, int expectedCount) {
        int totalHits = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                var intersections = shape.findIntersections(cam.constructRay(3, 3, col, row));
                totalHits += intersections == null ? 0 : intersections.size();
            }
        }
        assertEquals(expectedCount, totalHits, "Unexpected number of intersection points");
    }

    /// Test camera-ray intersection with spheres of various positions and sizes
    @Test
    public void cameraRaySphereIntegration() {
        // TC01: Small sphere directly in front of camera, expect 2 hits
        assertIntersectionCount(camOrigin, new Sphere(1.0, new Point(0, 0, -3)), 2);

        // TC02: Larger sphere enclosing the view plane, expect 18 hits
        assertIntersectionCount(camOffset, new Sphere(2.6, new Point(0, 0, -2.5)), 18);

        // TC03: Medium sphere, expect 10 hits
        assertIntersectionCount(camOffset, new Sphere(1.9, new Point(0, 0, -2)), 10);

        // TC04: Camera inside large sphere, expect 9 hits
        assertIntersectionCount(camOffset, new Sphere(4.1, new Point(0, 0, -1)), 9);

        // TC05: Sphere behind camera, expect 0 hits
        assertIntersectionCount(camOrigin, new Sphere(0.5, new Point(0, 0, 1)), 0);
    }

    /// Test camera-ray intersection with various planes
    @Test
    public void cameraRayPlaneIntegration() {
        // TC01: Plane perpendicular to camera direction, expect 9 hits
        assertIntersectionCount(camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)), 9);

        // TC02: Plane at a shallow angle, expect 9 hits
        assertIntersectionCount(camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 1, 2)), 9);

        // TC03: Plane almost parallel to lower rays, some rays miss, expect 6 hits
        assertIntersectionCount(camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)), 6);

        // TC04: Plane behind all rays (due to orientation), expect 0 hits
        assertIntersectionCount(camOrigin, new Plane(new Point(0, 0, 5), new Vector(0, -1, -1)), 0);
    }

    /// Test camera-ray intersection with triangles
    @Test
    public void cameraRayTriangleIntegration() {
        // TC01: Small triangle within center pixel, expect 1 hit
        assertIntersectionCount(camOrigin,
                new Triangle(new Point(1, 1, -2), new Point(-1, 1, -2), new Point(0, -0.5, -2)),
                1);

        // TC02: Larger triangle covering multiple pixels, expect 2 hits
        assertIntersectionCount(camOrigin,
                new Triangle(new Point(1, 1, -2), new Point(-1, 1, -2), new Point(0, -3, -2)),
                2);
    }
}
