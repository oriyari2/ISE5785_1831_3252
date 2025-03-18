package primitives;

public class Point {
    protected final Double3 xyz;

    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
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
        Double3 double3 = this.xyz.add(vector.xyz);
        return new Point(double3.d1(),double3.d2(), double3.d3());
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
