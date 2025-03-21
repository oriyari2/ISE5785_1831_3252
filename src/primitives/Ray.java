package primitives;

/**
 * Represents a ray in 3D space, defined by a starting point (head) and a normalized direction vector.
 */
public final class Ray {
    /** The starting point of the ray */
    private final Point head;

    /** The normalized direction vector of the ray */
    private final Vector direction;

    /**
     * Constructs a ray with a given starting point and direction.
     * The direction vector is automatically normalized.
     *
     * @param head The starting point of the ray
     * @param direction The direction vector (will be normalized)
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
}

