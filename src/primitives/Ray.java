package primitives;

import geometries.Intersectable;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Ray class represents a ray in 3D space.
 */
public final class Ray {
    /** The starting point of the ray */
    private final Point head;

    /** The normalized direction vector of the ray */
    private final Vector direction;
    private static final double DELTA = 0.1; // you can adjust this value

    /**
     * Constructor to initialize Ray with head point and direction vector.
     * @param head the head point of the ray
     * @param direction the direction vector of the ray
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && this.head.equals(other.head)
                && this.direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray [head=" + head + ", direction=" + direction + "]";
    }

    /**
     * Gets the head point of the ray.
     * @return the head point
     */
    public Point getHead() {
        return head;
    }
    /**
     * Gets the direction vector of the ray.
     * @return the direction vector
     */
    public Vector getDirection() {
        return direction;
    }
    /**
     * Gets a point on the ray at a distance t from the head.
     * @param t the distance from the head
     * @return the point on the ray
     */
    public Point getPoint(double t) {
        if (alignZero(t) == 0) return head;
        return head.add(direction.scale(t));
    }
    /**
     * Finds the closest point to the head from a list of points.
     *
     * @param intersections the list of points
     * @return the closest point to the head
     */
    public Intersectable.Intersection findClosestIntersection(List<Intersectable.Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) return null;

        Intersectable.Intersection closest = intersections.get(0);
        double minDistance = head.distanceSquared(closest.point);

        for (int i = 1; i < intersections.size(); i++) {
            double distance = head.distanceSquared(intersections.get(i).point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = intersections.get(i);
            }
        }

        return closest;
    }


    /**
     * Finds the closest point to the head from a list of points.
     *
     * @param points the list of points
     * @return the closest point to the head, or null if the list is empty or null
     */
    public Point findClosestPoint(List<Point> points) {
        Intersectable.Intersection closest = points == null || points.isEmpty()
                ? null
                : findClosestIntersection(
                points.stream().map(p -> new Intersectable.Intersection(null, p)).toList());

        return closest == null ? null : closest.point;
    }

    /**
     * Constructor to initialize Ray with head point, direction vector, and a normal vector.
     * This constructor ensures that the ray is offset slightly from the surface defined by the normal.
     *
     * @param head the head point of the ray
     * @param direction the direction vector of the ray
     * @param normal the normal vector of the surface
     */
    public Ray(Point head, Vector direction, Vector normal) {
        this.direction = direction.normalize();
        double dot = direction.dotProduct(normal);

        // If dot == 0, direction is orthogonal to normal, shift arbitrarily along normal
        Vector delta = normal.scale(
                isZero(dot) ? DELTA : (dot > 0 ? DELTA : -DELTA)
        );

        this.head = head.add(delta);
    }
    // Utility method to check for zero with precision threshold
    private static boolean isZero(double val) {
        final double EPSILON = 1e-10;
        return Math.abs(val) < EPSILON;
    }

}


