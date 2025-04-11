package geometries;
import primitives.Point;
import primitives.Ray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



public class Geometries implements Intersectable {
    // Geometries class implementation
    private List <Intersectable> geometries= new LinkedList<Intersectable>();
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
    public Geometries(Intersectable...geometries) {
       add (geometries);
    }

    private void add(Intersectable...geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;
        for (Intersectable item : geometries) {
            List<Point> itemPoints = item.findIntersections(ray);
            if (itemPoints != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(itemPoints);
            }
        }
        return result;
    }

}
