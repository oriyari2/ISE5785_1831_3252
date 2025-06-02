package geometries;

import geometries.RadialGeometry;
import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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
    public Sphere(Point center, double radius) {
        super(radius);  // Calls the constructor of RadialGeometry
        this.center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        // The normal is the vector from the center to the point on the surface
        return point.subtract(center).normalize();
    }


    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        Vector v = ray.getDirection();
        Point p0 = ray.getHead();

        // Special case: ray starts at the center of the sphere
        if (p0.equals(center)) {
            Point p = ray.getPoint(radius);
            return List.of(new Intersection(this, p));
        }

        // Vector from ray origin to center of sphere
        Vector u = center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double r2 = alignZero(radius * radius);

        // No intersection if the closest approach is farther than the radius
        if (alignZero(d2 - r2) > 0) {
            return null;
        }

        // Distance from the closest approach to the intersection points
        double th = alignZero(Math.sqrt(r2 - d2));
        if (isZero(th)) {
            return null; // Tangent case - treated as no intersection
        }

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // Both intersections are behind the ray origin
        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        // Create result list to collect intersection points
        List<Intersection> result = new LinkedList<>();

        if (t1 > 0) {
            result.add(new Intersection(this, ray.getPoint(t1)));
        }
        if (t2 > 0) {
            result.add(new Intersection(this, ray.getPoint(t2)));
        }

        return result.isEmpty() ? null : result;
    }

}

