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
        // שלב 1: חישוב הווקטור בין הנקודה על המעטפת לבין הציר
        Vector toPoint = point.subtract(axis.getHead());  // וקטור מהמרכז לנקודה על המעטפת

        // שלב 2: חישוב הווקטור המפריד את רכיב הציר
        double t = toPoint.dotProduct(axis.getDirection());  // חישוב רכיב הציר
        Vector projection = axis.getDirection().scale(t);    // פרוקציה של הווקטור על הציר

        // שלב 3: חישוב הווקטור הנורמלי על ידי חיסור
        Vector normal = toPoint.subtract(projection);  // חישוב הווקטור הנורמלי

        // שלב 4: נורמליזציה
        return normal.normalize();
        //TODO: fix the description
        //TODO: fix that the normal super from cylinder
        //TODO: fix the names
        //TODO: fix the method to be more efficient
    }

}
