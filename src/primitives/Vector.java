package primitives;

public class Vector extends Point {
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (this.xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("We can't create zero vector");
    }
    public Vector(Double3 xyz){
        super(xyz);
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
