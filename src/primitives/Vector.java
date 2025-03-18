package primitives;

public class Vector {
    protected Double3 xyz;

    public Vector(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    public Vector(Double3 xyz) {
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Zero vector is not allowed");
        }
        this.xyz = xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector)) return false;
        Vector other = (Vector) obj;
        return xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return xyz.toString();
    }

    public double lengthSquared() {
        return xyz.d1 * xyz.d1 + xyz.d2 * xyz.d2 + xyz.d3 * xyz.d3;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector add(Vector other) {
        return new Vector(xyz.add(other.xyz));
    }

    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    public Vector dotProduct(Vector other) {
        return new Vector(
                xyz.d1 * other.xyz.d1,
                xyz.d2 * other.xyz.d2,
                xyz.d3 * other.xyz.d3
        );
    }

    public Vector crossProduct(Vector other) {
        return new Vector(
                xyz.d2 * other.xyz.d3 - xyz.d3 * other.xyz.d2,
                xyz.d3 * other.xyz.d1 - xyz.d1 * other.xyz.d3,
                xyz.d1 * other.xyz.d2 - xyz.d2 * other.xyz.d1
        );
    }

    public Vector normalize() {
        double length = length();
        if (length == 0) throw new ArithmeticException("Cannot normalize zero vector");
        return scale(1 / length);
    }
}
