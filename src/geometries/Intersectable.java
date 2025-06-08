package geometries;
import lighting.LightSource;
import primitives.Material;
import primitives.Ray;
import primitives.Point;
import primitives.Vector;

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
     * Represents an intersection between a ray and a geometry.
     * Contains the geometry, intersection point, material, and various vectors
     * used for shading calculations.
     */
    public static class Intersection {
        public final Geometry geometry; // The geometry object that was intersected by the ray.

        public final Point point; // The exact point in 3D space where the intersection occurred.

        public final Material material = new Material(); // The material of the intersected geometry. Determines how the
        // surface reacts to light. This is initialized only if 'geometry' is not null.

        public Vector rayDirection; // The direction vector of the ray that caused this intersection.
        // Used in shading calculations and for determining reflection/refraction.

        public Vector normal; // The surface normal vector at the intersection point.
        // Used for all lighting calculations (diffuse, specular, etc.).

        public double rayScale; // The dot product of 'rayDirection' and 'normal'.
        // Used to determine if the ray hits the front or back face of the geometry,
        // and for offsetting the intersection point to avoid self-intersections.

        public LightSource lightSource; // The current light source being considered for shading at this intersection.

        public Vector lightDirection; // A vector from the light source to the intersection point (or vice versa, based
        // on convention). Used to compute light contributions and construct the shadow ray.

        public double lightScale; // The dot product of 'lightDirection' and 'normal'.
        // Represents the cosine of the angle between the light and the surface — key for diffuse shading.

        public Vector v; // The view direction vector (from the intersection point to the camera or eye).
        // Used for computing the specular component in the Phong reflection model.

        public double vNormal; // The dot product between 'v' and 'normal'.
        // Used to verify that the viewer sees the front side of the surface; if zero, surface is edge-on.


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
     * Template method that delegates the intersection calculation to concrete subclasses.
     * Returns a list of Intersection objects (point + geometry).
     *
     * @param ray the ray to test for intersections
     * @return list of intersection objects or null if none found
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        List<Intersection> intersections = calculateIntersectionsHelper(ray);
        return intersections == null || intersections.isEmpty() ? null : intersections;
    }

    /**
     * Calculates the intersections of a ray with the geometry and returns them as Intersection objects.
     *
     * @param ray The ray to find intersections with.
     * @return A list of Intersection objects, or an empty list if there are no intersections.
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);
}
