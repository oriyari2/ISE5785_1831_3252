package geometries;

import geometries.RadialGeometry;
import primitives.*;

import java.util.List;

import static primitives.Util.alignZero;

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

    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector v = ray.getDirection();
        Point p0 = ray.getHead();

        // The vector from the center of the sphere to the ray's head
        if (p0.equals(center)) {
            return List.of(ray.getPoint(radius));
        }

        Vector u = center.subtract(p0);
        double tm = alignZero(v.dotProduct(u));
        double d2 = alignZero(u.lengthSquared() - tm * tm);
        double r2 = alignZero(radius * radius);

        // No intersection if the distance from the ray to the center is greater than radius
        if (alignZero(d2 - r2) > 0) {
            return null;
        }

        double th = alignZero(Math.sqrt(r2 - d2));

        if (alignZero(th) == 0) {
            return null;
        }

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 <= 0 && t2 <= 0) return null;

        if (t1 > 0 && t2 > 0) return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0) return List.of(ray.getPoint(t1));
        if (t2 > 0) return List.of(ray.getPoint(t2));

        return null;
    }
}

