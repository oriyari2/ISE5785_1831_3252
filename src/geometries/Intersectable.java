package geometries;
import primitives.Ray;
import primitives.Point;
import java.util.List;

/**
 * A list of geometries in the scene.
 */
public abstract class Intersectable {

    /**
     * Finds the intersection points of a ray with the geometry.
     *
     * @param ray The ray to find intersections with.
     * @return A list of intersection points, or an empty list if there are no intersections.
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }

    /**
     * Finds the intersection points of a ray with the geometry and returns them as Intersection objects.
     *
     * @param ray The ray to find intersections with.
     * @return A list of Intersection objects, or an empty list if there are no intersections.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;

        /**
         * Constructs an Intersection object with the specified geometry and point.
         *
         * @param geometry The geometry involved in the intersection.
         * @param point    The point of intersection.
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Intersection other)) return false;
            return this.geometry == other.geometry && this.point.equals(other.point);
        }

    }


    /**
     * Calculates the intersections of a ray with the geometry and returns them as Intersection objects.
     *
     * @param ray The ray to find intersections with.
     * @return A list of Intersection objects, or an empty list if there are no intersections.
     */
    protected List<Intersection> calculateIntersectionsHelper(Ray ray)
    {
        List<Point> intersections = findIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return List.of(); // Return an empty list if no intersections found
        }

        // Create Intersection objects for each intersection point
        return intersections.stream()
                .map(point -> new Intersection((Geometry)this, point))
                .toList();
    }

    public final List<Intersection> calculateIntersections(Ray ray) {
        List<Intersection> intersections = calculateIntersectionsHelper(ray);
        if (intersections.isEmpty()) {
            return null; // Return an empty list if no intersections found
        }
        return intersections;
    }




}
