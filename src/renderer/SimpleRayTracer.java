package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

public class SimpleRayTracer extends RayTracerBase {

    /**
     * Maximum recursion level for color calculation.
     * This limits the depth of reflection and refraction calculations.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * Minimum value for k in color calculations.
     * If the product of k values falls below this threshold, the color is considered negligible.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * Initial value for k in color calculations.
     * This is used to start the recursive color calculation process.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructor for SimpleRayTracer.
     * Initializes the ray tracer with the given scene.
     *
     * @param scene The scene to be rendered by this ray tracer.
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene and returns the color at the intersection point.
     * If no intersection is found, it returns the background color of the scene.
     *
     * @param ray The ray to trace through the scene.
     * @return The color at the intersection point, or the background color if no intersection is found.
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection intersection = findClosestIntersection(ray);
        return intersection == null ? scene.background : calcColor(intersection, ray);
    }

    /**
     * Calculates the color at the intersection point based on local and global effects.
     *
     * @param intersection The intersection point with the geometry.
     * @param ray          The ray that hit the geometry.
     * @return The calculated color at the intersection point.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            return Color.BLACK;
        }
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA));
    }

    /**
     * Calculates the color at the intersection point based on local and global effects.
     *
     * @param intersection The intersection point with the geometry.
     * @param level        The current recursion level for reflection/refraction.
     * @param k            The product of material coefficients (kD, kS, kT, kR).
     * @return The calculated color at the intersection point.
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates the global effects (reflection and refraction) at the intersection point.
     *
     * @param intersection The intersection point with the geometry.
     * @param level        The current recursion level for reflection/refraction.
     * @param k            The product of material coefficients (kD, kS, kT, kR).
     * @return The calculated color contribution from global effects.
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcGlobalEffect(constructRefractedRay(intersection), level, k, intersection.geometry.getMaterial().kT)
                .add(calcGlobalEffect(constructReflectedRay(intersection), level, k, intersection.geometry.getMaterial().kR));
    }

    /**
     * Calculates the global effect (reflection or refraction) for a given ray.
     *
     * @param ray   The ray to trace for global effects.
     * @param level The current recursion level for reflection/refraction.
     * @param k     The product of material coefficients (kD, kS, kT, kR).
     * @param kx    The specific coefficient (kT or kR) for the current calculation.
     * @return The calculated color contribution from the global effect.
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) {
            return scene.background.scale(kx);
        }

        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Finds the closest intersection of a ray with the scene geometries.
     *
     * @param ray The ray to test for intersections.
     * @return The closest intersection point, or null if no intersections are found.
     */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null || intersections.isEmpty()) return null;

        Intersection closest = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distanceSquared = ray.getHead().distanceSquared(intersection.point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = intersection;
            }
        }
        return closest;
    }

    /**
     * Preprocesses the intersection by setting the view vector and normal.
     * Checks if the view vector is aligned with the normal (not edge-on).
     *
     * @param intersection The intersection point with the geometry.
     * @param v            The view vector from the intersection point to the camera.
     * @return true if the view vector is not edge-on, false otherwise.
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Calculates the local effects (diffuse and specular reflection) at the intersection point.
     *
     * @param intersection The intersection point with the geometry.
     * @param k            The product of material coefficients (kD, kS, kT, kR).
     * @return The calculated color contribution from local effects.
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color localLight = intersection.geometry.getEmission();
        Point p = intersection.point;
        Vector v = intersection.v.scale(-1).normalize();
        Vector n = intersection.normal;

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(p);
            double nl = alignZero(n.dotProduct(l));
            double nv = alignZero(n.dotProduct(v));

            // Light and viewer must be on the same side of the surface
            if (nl * nv > 0) {
                // Set relevant light data in the intersection for transparency and shading calculation
                intersection.lightSource = lightSource;
                intersection.lightDirection = l;
                intersection.lightScale = nl;

                // Get the transparency factor along the shadow ray
                Double3 ktr = transparency(intersection);
                // If not completely blocked
                if (!ktr.equals(Double3.ZERO)) {
                    Vector nEff = (nl < 0) ? n.scale(-1) : n;
                    double nEffDotL = nEff.dotProduct(l);
                    Color lightIntensity = lightSource.getIntensity(p);

                    if (!lightIntensity.equals(Color.BLACK) && nEffDotL > 0) {
                        // Calculate diffuse and specular effects
                        Double3 diffuse = calcDiffuse(intersection);
                        Double3 specular = calcSpecular(intersection);

                        // Total local factor scaled by transparency and k
                        Double3 factor = k.product(ktr).product(diffuse.add(specular));
                        localLight = localLight.add(lightIntensity.scale(factor));
                    }
                }
            }
        }
        return localLight;
    }




    /**
     * Calculates the diffuse reflection component at the intersection point.
     *
     * @param intersection The intersection point with the geometry.
     * @return The calculated diffuse color contribution.
     */
    private Double3 calcDiffuse(Intersection intersection) {
        Material material = intersection.geometry.getMaterial();
        Vector n = intersection.normal;
        Vector l = intersection.lightDirection;
        double nl = alignZero(n.dotProduct(l));
        Vector nEff = (nl < 0) ? n.scale(-1) : n;
        double nEffDotL = alignZero(nEff.dotProduct(l));
        return material.kD.scale(Math.max(0, nEffDotL));
    }


    /**
     * Calculates the specular reflection component at the intersection point.
     *
     * @param intersection The intersection point with the geometry.
     * @return The calculated specular color contribution.
     */
    private Double3 calcSpecular(Intersection intersection) {
        Material material = intersection.geometry.getMaterial();
        Vector n = intersection.normal;
        Vector l = intersection.lightSource.getL(intersection.point);
        Vector v = intersection.v.scale(-1).normalize();
        double nl = alignZero(n.dotProduct(l));
        Vector nEff = (nl < 0) ? n.scale(-1) : n;
        Vector r = nEff.scale(2 * nEff.dotProduct(l)).subtract(l).normalize();
        double vr = v.dotProduct(r);
        if (vr <= 0) return Double3.ZERO;

        double spec = Math.pow(vr, material.nSh);
        return material.kS.scale(spec);
    }

    /**
     * Constructs a reflected ray based on the intersection point and normal.
     * The ray's head is offset by a small DELTA along the normal direction to avoid self-intersection.
     *
     * @param intersection The intersection point with the geometry.
     * @return A new Ray object representing the reflected ray.
     */
    private Ray constructReflectedRay(Intersection intersection) {
        Vector v = intersection.v.normalize();
        Vector n = intersection.normal;

        // Flip normal if it faces the same direction as the incoming ray
        if (v.dotProduct(n) > 0) {
            n = n.scale(-1);
        }

        Vector r = v.subtract(n.scale(2 * v.dotProduct(n))).normalize();

        // Use Ray's constructor that offsets the head by DELTA along the normal
        return new Ray(intersection.point, r, n);
    }

    /**
     * Constructs a refracted ray based on the intersection point and normal.
     * The ray's head is offset by a small DELTA along the normal direction to avoid self-intersection.
     *
     * @param intersection The intersection point with the geometry.
     * @return A new Ray object representing the refracted ray.
     */
    private Ray constructRefractedRay(Intersection intersection) {
        Vector v = intersection.v.normalize();
        Vector n = intersection.normal;

        // Flip normal to ensure correct DELTA offset direction (for exiting the surface)
        if (v.dotProduct(n) > 0) {
            n = n.scale(-1);
        }

        // Use Ray's constructor that offsets the head by DELTA along the normal
        return new Ray(intersection.point, v, n);
    }

    /**
     * Checks if the intersection point is unshaded by any geometry.
     * Constructs a shadow ray and checks for intersections with other geometries.
     *
     * @param intersection The intersection point with the geometry.
     * @return true if the point is unshaded, false otherwise.
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.lightDirection;

        // Use Ray constructor to offset the origin along the normal direction
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal.scale(intersection.lightScale));

        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);
        double lightDistance = intersection.lightSource.getDistance(intersection.point);

        if (shadowIntersections != null) {
            for (Intersection shadowInter : shadowIntersections) {
                double dist = shadowInter.point.distance(intersection.point);
                if (dist < lightDistance
                        && shadowInter.geometry != intersection.geometry
                        && shadowInter.geometry.getMaterial().kT.d1() < MIN_CALC_COLOR_K) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Calculates the transparency at the intersection point based on the shadow ray.
     * It checks for intersections along the path to the light source and accumulates transparency.
     *
     * @param intersection The intersection point with the geometry.
     * @return The accumulated transparency factor at the intersection point.
     */
    private Double3 transparency(Intersection intersection) {
        // Direction vector from the point to the light source
        Vector pointToLight = intersection.lightDirection;

        // Create a shadow ray, slightly offset in the normal direction to avoid self-intersection
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal.scale(intersection.lightScale));

        // Get all intersections of the shadow ray with the scene's geometries
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);

        // Distance from the point to the light source
        double lightDistance = intersection.lightSource.getDistance(intersection.point);

        // Initialize accumulated transparency factor to 1 (no obstruction)
        Double3 ktr = Double3.ONE;

        // If there are no intersections, the path is fully transparent
        if (shadowIntersections == null || shadowIntersections.isEmpty()) {
            return ktr;
        }

        // Go through each intersection of the shadow ray
        for (Intersection shadowInter : shadowIntersections) {
            double dist = shadowInter.point.distance(intersection.point);

            // Only consider objects that are between the point and the light source
            // and ensure we don't consider the same surface
            if (dist < lightDistance && alignZero(dist) > 0 && shadowInter.geometry != intersection.geometry) {
                // Get the transparency coefficient (kT) of the intersected geometry
                Double3 kT = shadowInter.geometry.getMaterial().kT;

                // Accumulate the transparency along the shadow ray
                ktr = ktr.product(kT);

                // Optimization: if transparency becomes negligible, return zero
                if (ktr.lowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }

        // Return the accumulated transparency
        return ktr;
    }
}