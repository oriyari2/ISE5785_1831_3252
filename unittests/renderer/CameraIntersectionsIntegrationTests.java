package renderer;

import geometries.*;
import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests: verifying ray intersections with geometric shapes constructed from the camera
 */
public class CameraIntersectionsIntegrationTests {

    // === Constants for the view plane size ===
    private static final int NX = 3;
    private static final int NY = 3;

    /// Builder with fixed direction and view plane distance
    private final Camera.Builder camBaseBuilder = Camera.getBuilder()
            .setDirection(new Point(0, 0, -1), Vector.MINUS_Y)
            .setVpDistance(1);

    /// Camera positioned at origin with view plane size NX x NY
    private final Camera camOrigin = camBaseBuilder.setLocation(Point.ZERO).setVpSize(NX, NY).build();
    /// Camera slightly in front of origin with view plane size NX x NY
    private final Camera camOffset = camBaseBuilder.setLocation(new Point(0, 0, 0.5)).setVpSize(NX, NY).build();

    /**
     * Helper function to test the total number of intersections between a shape and rays from the camera
     *
     * @param expected expected number of intersection points
     * @param camera   the camera used to construct rays
     * @param shape    the geometric shape to intersect
     */
    private void assertIntersectionCount(int expected, Camera camera, Intersectable shape) {
        int totalHits = 0;
        for (int row = 0; row < NY; ++row) {
            for (int col = 0; col < NX; ++col) {
                var intersections = shape.findIntersections(camera.constructRay(NX, NY, col, row));
                totalHits += intersections == null ? 0 : intersections.size();
            }
        }
        assertEquals(expected, totalHits, "Unexpected number of intersection points");
    }

    /// Test camera-ray intersection with spheres of various positions and sizes
    @Test
    public void cameraRaySphereIntegration() {
        // TC01: Small sphere directly in front of camera, expect 2 hits
        assertIntersectionCount(2, camOrigin, new Sphere(new Point(0, 0, -3), 1.0));

        // TC02: Larger sphere enclosing the view plane, expect 18 hits
        assertIntersectionCount(18, camOffset, new Sphere(new Point(0, 0, -2.5), 2.6));

        // TC03: Medium sphere, expect 10 hits
        assertIntersectionCount(10, camOffset, new Sphere(new Point(0, 0, -2), 1.9));

        // TC04: Camera inside large sphere, expect 9 hits
        assertIntersectionCount(9, camOffset, new Sphere(new Point(0, 0, -1), 4.1));

        // TC05: Sphere behind camera, expect 0 hits
        assertIntersectionCount(0, camOrigin, new Sphere(new Point(0, 0, 1), 0.5));
    }

    /// Test camera-ray intersection with various planes
    @Test
    public void cameraRayPlaneIntegration() {
        // TC01: Plane perpendicular to camera direction, expect 9 hits
        assertIntersectionCount(9, camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)));

        // TC02: Plane at a shallow angle, expect 9 hits
        assertIntersectionCount(9, camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 1, 2)));

        // TC03: Plane almost parallel to lower rays, some rays miss, expect 6 hits
        assertIntersectionCount(6, camOrigin, new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)));

        // TC04: Plane behind all rays (due to orientation), expect 0 hits
        assertIntersectionCount(0, camOrigin, new Plane(new Point(0, 0, 5), new Vector(0, -1, -1)));
    }

    /// Test camera-ray intersection with triangles
    @Test
    public void cameraRayTriangleIntegration() {
        // TC01: Small triangle within center pixel, expect 1 hit
        assertIntersectionCount(1, camOrigin,
                new Triangle(new Point(1, 1, -2), new Point(-1, 1, -2), new Point(0, -0.5, -2))
        );

        // TC02: Larger triangle covering multiple pixels, expect 2 hits
        assertIntersectionCount(2, camOrigin,
                new Triangle(new Point(1, 1, -2), new Point(-1, 1, -2), new Point(0, -3, -2))
        );
    }
}
