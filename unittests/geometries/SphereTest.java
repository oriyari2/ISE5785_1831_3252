package geometries;
import geometries.Sphere;
import primitives.Point;
import primitives.Vector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SphereTest {

    @Test
    void testGetNormal() {
        // Create a sphere with center at (0, 0, 0) and radius 1
        Sphere sphere = new Sphere(1, new Point(0, 0, 0));

        // Test a point on the surface of the sphere
        Point pointOnSurface = new Point(1, 0, 0);
        Vector expectedNormal = new Vector(1, 0, 0).normalize();
        assertEquals(expectedNormal, sphere.getNormal(pointOnSurface),
                "Incorrect normal at point on surface of sphere");

        // Test a point on the opposite side of the sphere
        Point pointOnOppositeSide = new Point(-1, 0, 0);
        expectedNormal = new Vector(-1, 0, 0).normalize();
        assertEquals(expectedNormal, sphere.getNormal(pointOnOppositeSide),
                "Incorrect normal at point on opposite side of sphere");
    }
}