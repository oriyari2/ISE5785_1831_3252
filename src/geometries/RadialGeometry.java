package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * An abstract class that represents geometries with a radial dimension,
 * such as spheres, cylinders, etc. These geometries are defined by a radius.
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the radial geometry.
     */
    final protected double radius;

    /**
     * Constructs a RadialGeometry with the given radius.
     *
     * @param radius The radius of the geometry, which must be positive.
     */
    public RadialGeometry(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive.");
        }
        this.radius = radius;
    }
}

