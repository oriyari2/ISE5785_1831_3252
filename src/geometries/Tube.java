package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube geometry, which is a type of radial geometry
 * defined by an axis (a Ray) and a radius.
 */
public class Tube extends RadialGeometry {

    /**
     * The axis of the tube, represented by a Ray.
     */
    protected final Ray axis;

    /**
     * Constructs a Tube with the given axis and radius.
     *
     * @param axis The axis of the tube, represented by a Ray.
     * @param radius The radius of the tube.
     */
    public Tube(Ray axis, double radius) {
        super(radius);  // Calls the constructor of RadialGeometry to initialize the tube's radius
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;  // Placeholder implementation, needs actual logic
    }
}
