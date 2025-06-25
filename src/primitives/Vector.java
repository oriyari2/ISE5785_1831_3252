package primitives;

import static primitives.Util.isZero; // Ensure Util.isZero is imported

/**
 * Represents a three-dimensional vector in 3D space
 * A vector extends {@link Point} but cannot be the zero vector (0,0,0).
 */
public class Vector extends Point {
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    public static final Vector AXIS_Z = new Vector(0, 0, 1);
    public static final Vector MINUS_Y = new Vector(0,-1,0);
    /**
     * Constructs a vector from three coordinates.
     *
     * @param x The x-coordinate of the vector
     * @param y The y-coordinate of the vector
     * @param z The z-coordinate of the vector
     * @throws IllegalArgumentException if the vector is the zero vector (0,0,0)
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (super.xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("We can't create zero vector");
    }

    /**
     * Constructs a vector from a {@link Double3} object.
     *
     * @param xyz The {@link Double3} object representing the vector
     * @throws IllegalArgumentException if the vector is the zero vector (0,0,0)
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (super.xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("We can't create zero vector");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Vector other && super.equals(other);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Computes the squared length (magnitude) of the vector.
     *
     * @return The squared length of the vector
     */
    public double lengthSquared() {
        return this.dotProduct(this);
    }

    /**
     * Computes the length (magnitude) of the vector.
     *
     * @return The length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add
     * @return A new vector representing the sum
     */
    public Vector add(Vector other) {
        return new Vector(xyz.add(other.xyz));
    }

    /**
     * Scales the vector by a scalar.
     *
     * @param scalar The scalar value to multiply by
     * @return A new vector representing the scaled vector
     */
    public Vector scale(double scalar) {
        return new Vector(super.xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this vector with another vector.
     *
     * @param other The other vector
     * @return The dot product result
     */
    public double dotProduct(Vector other) {
        return (xyz.d1() * other.xyz.d1() + xyz.d2() * other.xyz.d2() + xyz.d3() * other.xyz.d3());
    }

    /**
     * Computes the cross product of this vector with another vector.
     *
     * @param other The other vector
     * @return A new vector representing the cross product result
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
                xyz.d2() * other.xyz.d3() - xyz.d3() * other.xyz.d2(),
                xyz.d3() * other.xyz.d1() - xyz.d1() * other.xyz.d3(),
                xyz.d1() * other.xyz.d2() - xyz.d2() * other.xyz.d1()
        );
    }

    /**
     * Normalizes the vector, making it a unit vector.
     *
     * @return A new vector that is the normalized version of this vector
     * @throws ArithmeticException if the vector is the zero vector
     */
    public Vector normalize() {
        double length = length();
        return scale(1 / length);
    }

    /**
     * Checks if two vectors are orthogonal.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return true if the vectors are orthogonal, false otherwise.
     */
    public static boolean isOrthogonal(Vector v1, Vector v2) {
        // Orthogonality is determined by a zero dot product
        return isZero(v1.dotProduct(v2));
    }
}
