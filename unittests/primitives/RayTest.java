package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTest {

    @Test
    void testGetPoint() {

        // ============ Equivalence Partitions Tests ==============
        // TC01: Negative distance
        Ray ray1 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(new Point(1, 2, 6), ray1.getPoint(3), "getPoint() failed for positive t");

        // TC02: Positive distance
        Ray ray2 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(new Point(1, 2, 1), ray2.getPoint(-2), "getPoint() failed for negative t");

        // =============== Boundary Values Tests ==================
        // TC11: Zero distance
        Ray ray3 = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1));
        assertEquals(ray3.getHead(), ray3.getPoint(0), "getPoint() failed for t = 0 (should return head)");
    }
}