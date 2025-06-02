package geometries;
import primitives.Point;
import primitives.Ray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for Cylinder class.
 */
public class Geometries extends Intersectable {

    /**
     * A list of geometries in the scene.
     */
    private List<Intersectable> geometries = new LinkedList<>();

    /**
     * Default constructor for the Geometries class.
     * Initializes an empty list of geometries.
     */
    public Geometries() {
    }

    /**
     * Constructor for the Geometries class that accepts an array of Intersectable objects.
     * Initializes the list of geometries with the provided objects.
     *
     * @param geometries An array of Intersectable objects to be added to the list.
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more Intersectable objects to the list of geometries.
     *
     * @param geometries One or more Intersectable objects to be added.
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }


    /**
     *  Calculates all the intersection points between a given ray and the set of geometries.
     *  This method iterates over all contained geometries and accumulates their individual intersections
     *  using their own calculateIntersectionsHelper methods.
     *
     *  @param ray the ray to intersect with the geometries
     *  @return a list of Intersection objects representing all intersections found,
     *          or null if there are no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;

        // Iterate over all geometries and find intersections
        for (Intersectable geo : geometries) {
            List<Intersection> intersections = geo.calculateIntersectionsHelper(ray);

            // If intersections are found, add them to the result list
            if (intersections != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(intersections);
            }
        }
        return result;
    }

}
