package geometries;

import geometries.RadialGeometry;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere geometry, which is a type of radial geometry
 * defined by a center point and a radius.
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a Sphere with a given radius and center point.
     *
     * @param radius The radius of the sphere, which must be positive.
     * @param center The center point of the sphere.
     */
    public Sphere(double radius, Point center) {
        super(radius);  // Calls the constructor of RadialGeometry
        this.center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        // The normal is the vector from the center to the point on the surface
        return point.subtract(center).normalize();
    }
}

