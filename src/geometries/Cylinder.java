package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a cylinder in 3D space, which extends a {@link Tube}.
 * A cylinder has a defined height in addition to the properties of a tube.
 */
public class Cylinder extends Tube {

    /**
     * Represents the height of the cylinder.
     */
    private final double height;

    /**
     * Constructs a cylinder with a given axis, radius, and height.
     *
     * @param axis   The central axis of the cylinder as a {@link Ray}
     * @param radius The radius of the cylinder
     * @param height The height of the cylinder
     */
    public Cylinder(Ray axis, double radius, double height) {
        super(axis, radius);
        this.height = height;
    }

    /**
     * Returns the height of the cylinder.
     *
     * @return The height of the cylinder
     */
    public double getHeight() {
        return height;
    }

    @Override
    public Vector getNormal(Point point) {
        // TODO: Implement normal calculation logic
        return null;
    }
}

