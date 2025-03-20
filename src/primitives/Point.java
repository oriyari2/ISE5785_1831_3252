package primitives;

public class Point {
    final Double3 xyz;
    public static final Point ZERO = new Point(Double3.ZERO);

    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

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

    public Point add(Vector vector) {
        return new Point(xyz.add(vector.xyz));
    }

    public Vector subtract(Point other) {
        return new Vector(xyz.subtract(other.xyz));
    }

    public double distanceSquared(Point other) {
        Double3 diff = this.xyz.subtract(other.xyz); // שימוש במתודה subtract של Double3
        return diff.d1() * diff.d1() + diff.d2() * diff.d2() + diff.d3() * diff.d3(); // הכפלה ישירה
    }

    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other)); // שימוש בפונקציה distanceSquared
    }

}
