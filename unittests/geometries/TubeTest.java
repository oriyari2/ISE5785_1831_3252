package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TubeTest {

    private static final double DELTA = 0.000001;

    @Test
    void testGetNormal() {
        // ============ Setup =============
        Ray axisRay = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(axisRay, 1.0);

        // ============ Equivalence Partitions Tests =============
        // TC01: Test the normal at a point on the tube's surface
        Point point = new Point(1, 0, 5);
        Vector normal = tube.getNormal(point);

        // Check if the normal is of unit length
        assertEquals(1, normal.length(), DELTA, "Normal vector is not normalized");

        // Check if the normal is correctly directed
        assertEquals(new Vector(1, 0, 0), normal, "Wrong normal to tube");

        // =============== Boundary Values Tests ==================
        // TC11: Test the normal at a point directly above the ray's head (not creating zero vector)
        Point boundaryPoint = new Point(1, 0, 0.000001); // Slightly offset to avoid zero vector
        Vector boundaryNormal = tube.getNormal(boundaryPoint);

        // Check if the normal is of unit length
        assertEquals(1, boundaryNormal.length(), DELTA, "Boundary normal vector is not normalized");

        // Check if the normal is correctly directed
        assertEquals(new Vector(1, 0, 0), boundaryNormal, "Wrong boundary normal to tube");
    }
}