package primitives;

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
     * @param points the list of points
     * @return the closest point to the head
     */
    public Point findClosestPoint(List<Point> points)
    {
        // Check if the list is null or empty
        if (points == null || points.isEmpty()) return null;
        // Initialize the closest point to the first point in the list
        Point closestPoint = points.get(0);
        // Calculate the squared distance from the head to the first point
        double minDistance = head.distanceSquared(closestPoint);
        // Iterate through the rest of the points to find the closest one
        for (int i = 1; i < points.size(); i++) {
            double distance = head.distanceSquared(points.get(i));
            // If the current point is closer, update the closest point and distance
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = points.get(i);
            }
        }
        return closestPoint;
    }
}


