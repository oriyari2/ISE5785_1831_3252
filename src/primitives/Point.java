package primitives;

/**
 * Represents a point in a 3D space using {@link Double3}.
 * A point is immutable and can be used in geometric calculations.
 */
public class Point {
    /** The XYZ coordinates of the point */
    final Double3 xyz;

    /** The zero point (0,0,0) */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point with given x, y, and z coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from a {@link Double3} object.
     *
     * @param xyz A {@link Double3} object representing the coordinates
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other)
                && this.xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return xyz.toString();
    }

    /**
     * Adds a vector to this point and returns a new point.
     *
     * @param vector The vector to add
     * @return A new {@code Point} after adding the vector
     */
    public Point add(Vector vector) {
        return new Point(xyz.add(vector.xyz));
    }

    /**
     * Subtracts another point from this point to get a vector.
     *
     * @param other The point to subtract
     * @return A {@link Vector} representing the difference between the points
     */
    public Vector subtract(Point other) {
        return new Vector(xyz.subtract(other.xyz));
    }

    /**
     * Calculates the squared distance between this point and another point.
     * This method is more efficient than {@link #distance(Point)} because it avoids computing the square root.
     *
     * @param other The other point
     * @return The squared distance between the two points
     */
    public double distanceSquared(Point other) {
        Double3 diff = this.xyz.subtract(other.xyz); // שימוש במתודה subtract של Double3
        return diff.d1() * diff.d1() + diff.d2() * diff.d2() + diff.d3() * diff.d3(); // הכפלה ישירה
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * @param other The other point
     * @return The distance between the two points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other)); // שימוש בפונקציה distanceSquared
    }
}

