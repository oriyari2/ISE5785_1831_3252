package primitives;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @org.junit.jupiter.api.Test
    void testLengthSquared() {
        Vector vector = new Vector(1, 2, 3);
        assertEquals(14, vector.lengthSquared(),0.00001, "ERROR: lengthSquared() wrong value");
    }

    @org.junit.jupiter.api.Test
    void testLength() {
    }

    @org.junit.jupiter.api.Test
    void testAdd() {
    }

    @org.junit.jupiter.api.Test
    void testScale() {
    }

    @org.junit.jupiter.api.Test
    void testDotProduct() {
    }

    @org.junit.jupiter.api.Test
    void testCrossProduct() {
    }

    @org.junit.jupiter.api.Test
    void testNormalize() {
    }
}