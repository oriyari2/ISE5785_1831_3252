package geometries;
import primitives.Ray;
import primitives.Point;
import java.util.List;

/**
 * A list of geometries in the scene.
 */
public interface Intersectable {

    /**
     * Finds the intersection points of a ray with the geometry.
     *
     * @param ray The ray to find intersections with.
     * @return A list of intersection points, or an empty list if there are no intersections.
     */
    List<Point> findIntersections(Ray ray);
}
