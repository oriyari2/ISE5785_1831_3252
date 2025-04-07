package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for Vector class.
 */
class VectorTest {

    /**
     * DELTA for test precision
     */
    final static private double DELTA = 0.000001;

    /**
     * Vectors for testing
     */
    final static private Vector v1 = new Vector(1, 2, 3);
    final static private Vector v1Opposite = new Vector(-1, -2, -3);
    final static private Vector v2 = new Vector(-2, -4, -6);
    final static private Vector v3 = new Vector(0, 3, -2);
    final static private Vector v4 = new Vector(1, 0, 0);

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testConstructorWithCoordinates() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct vector creation without exception
        assertDoesNotThrow(() -> new Vector(1, 2, 3), "Failed to create a valid vector");

        // =============== Boundary Values Tests ==================
        // TC11: Creating a zero vector should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "Creating zero vector should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#Vector(Double3)}.
     */
    @Test
    void testConstructorWithDouble3() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct vector creation using Double3
        assertDoesNotThrow(() -> new Vector(new Double3(1, 2, 3)), "Failed to create a valid vector");

        // =============== Boundary Values Tests ==================
        // TC11: Creating a zero vector should throw an exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), "Creating zero vector should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Check squared length calculation is correct
        assertEquals(14, v1.lengthSquared(), DELTA,"lengthSquared() incorrect value");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Check length calculation for unit vector
        assertEquals( 1, v4.length(), DELTA,"length() incorrect value");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAddVector() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct vector addition
        assertEquals(new Vector(-1, -2, -3), v1.add(v2),"Vector addition incorrect");

        // =============== Boundary Values Tests ==================
        // TC11: Adding a vector to its opposite should throw an exception
        assertThrows(IllegalArgumentException.class, () -> v1.add(v1Opposite),
                "Adding opposite vectors should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct vector scaling
        assertEquals(new Vector(5, 0, 0), v4.scale(5),  "Scaling vector incorrect" );

        // =============== Boundary Values Tests ==================
        // TC11: Scaling a vector by zero should throw an exception
        assertThrows( IllegalArgumentException.class,() -> v1.scale(0), "Scaling by zero should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct dot product calculation
        assertEquals(-28, v1.dotProduct(v2), DELTA, "Dot product incorrect");

        // =============== Boundary Values Tests ==================
        // TC11: Dot product of orthogonal vectors should be zero
        assertEquals(0, v1.dotProduct(v3), DELTA,
                "Dot product should be zero for orthogonal vectors");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // Compute the cross product of v1 and v3
        Vector vector = v1.crossProduct(v3);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct cross product calculation
        assertEquals(vector.length(), v1.length() * v3.length(), DELTA,
                "Cross product length incorrect");
        // Ensure cross product is orthogonal to first operand
        assertEquals(0, vector.dotProduct(v1),
                "Cross product should be orthogonal to first operand");
        // Ensure cross product is orthogonal to second operand
        assertEquals(0, vector.dotProduct(v3),
                "Cross product should be orthogonal to second operand");

        // =============== Boundary Values Tests ==================
        // TC11: Cross product of parallel vectors should throw an exception
        assertThrows(Exception.class, () -> v1.crossProduct(v2),
                "Cross product of parallel vectors should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // Compute the normalized vector
        Vector unitVector = v1.normalize();

        // ============ Equivalence Partitions Tests ==============
        // TC01: Normalized vector should have unit length
        assertEquals(1, unitVector.length(), DELTA, "Normalized vector should be unit length");
    }
    /**
     * Test method for {@link primitives.Vector# subtract(primitives.Vector)}.
     */
    @Test
    void testSubstruct() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ensure correct vector subtraction
        assertEquals( new Vector(3, 6, 9), v1.subtract(v2), "Vector subtraction incorrect");

        // =============== Boundary Values Tests ==================
        // TC11: Subtracting a vector from itself should throw an exception
        assertThrows( IllegalArgumentException.class, () -> v1.subtract(v1), "Subtracting the same vector should throw an exception");
    }
}