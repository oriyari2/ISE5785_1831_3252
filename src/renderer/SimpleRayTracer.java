package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;


import static primitives.Util.*;

/**
 * Simple ray tracer that implements basic Phong shading.
 * This class extends RayTracerBase and provides core ray tracing functionality:
 * finding intersections, determining closest intersection, and applying
 * ambient, diffuse, and specular lighting components.
 */
public class SimpleRayTracer extends RayTracerBase {


    /**
     * Constants for the ray tracing algorithm.
     * These values are used to limit the recursion depth and avoid infinite loops.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Threshold for floating point comparisons to avoid precision issues
     */
    private static final double DELTA = 0.1;

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to render
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene and calculates the resulting color.
     *
     * @param ray the ray to trace
     * @return the color seen along the ray
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        if (intersections == null || intersections.isEmpty()) {
            return scene.background != null ? scene.background : Color.BLACK;
        }

        Intersection closestIntersection = findClosestIntersection(ray.getHead(), intersections);
        if (closestIntersection == null) {
            return scene.background != null ? scene.background : Color.BLACK;
        }

        return calcColor(closestIntersection, ray);
    }

    /**
     * Finds the closest intersection point to the ray origin.
     *
     * @param origin        the origin point of the ray
     * @param intersections list of intersection points
     * @return the closest intersection to the origin
     */
    private Intersection findClosestIntersection(Point origin, List<Intersection> intersections) {
        Intersection closest = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distanceSquared = origin.distanceSquared(intersection.point);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = intersection;
            }
        }
        return closest;
    }

    /**
     * Preprocesses the intersection data by calculating the normal vector and
     * checking if the view direction is aligned with the normal.
     *
     * @param intersection the intersection data to preprocess
     * @param v            the view direction vector
     * @return true if the view direction is not aligned with the normal, false otherwise
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Calculates the final color at a given intersection point using the Phong reflection model.
     *
     * @param intersection the intersection data
     * @param ray the incoming ray
     * @return the computed color at the intersection
     */
    /**
     * Calculates the color at the point of intersection using only local lighting effects.
     * If the intersection is invalid (e.g., normal and view direction are perpendicular),
     * returns black.
     *
     * @param intersection The intersection details including geometry and point of intersection.
     * @param ray          The ray that caused the intersection.
     * @return The resulting color from local lighting effects or black if intersection is invalid.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA))
                : Color.BLACK;

    }

    /**
     * Recursively calculates the color at the intersection point, considering both local
     * and global effects (reflections and refractions).
     *
     * @param intersection the intersection details
     * @param level        the recursion level for reflections/refractions
     * @param k            the scaling factor for the color
     * @return the final color at the intersection point
     */
    private Color calcColor(Intersection intersection,int level, Double3 k) {
        Color color= calcLocalEffects(intersection, k);
        return 1 == level ?color: color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates the local lighting effects at the intersection point.
     * This includes diffuse and specular reflections based on the Phong reflection model.
     *
     * @param intersection the intersection details
     * @param v            the view direction vector
     * @param n            the normal vector at the intersection point
     * @param material     the material properties of the intersected geometry
     * @return the color contribution from local lighting effects
     */
    private Color calcLocalEffects(Intersection intersection, Vector v, Vector n, Material material) {
        Color localLight = Color.BLACK;
        Point p = intersection.point;

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(p);
            double nl = alignZero(n.dotProduct(l));
            double nv = alignZero(n.dotProduct(v));

            // Continue only if light and view are on the same side of the surface
            if (nl * nv > 0) {
                // Update intersection with light info for shading
                intersection.lightSource = lightSource;
                intersection.lightDirection = l;
                intersection.lightScale = nl;

                // Check for shadow before applying light effects
                if (unshaded(intersection)) {
                    Vector nEff = (nl < 0) ? n.scale(-1) : n;
                    double nEffDotL = nEff.dotProduct(l);
                    Color lightIntensity = lightSource.getIntensity(p);

                    if (!lightIntensity.equals(Color.BLACK) && nEffDotL > 0) {
                        Double3 diffuse = calcDiffuse(material, nEffDotL);
                        Double3 specular = calcSpecular(material, nEff, l, v, nEffDotL);
                        localLight = localLight.add(lightIntensity.scale(diffuse.add(specular)));
                    }
                }
            }
        }

        return localLight;
    }


    /**
     * Calculates the diffuse reflection component using Phong model.
     *
     * @param material the surface material
     * @param nl       dot product of normal and light direction, expected > 0
     * @return the scaled diffuse reflection
     */
    private Double3 calcDiffuse(Material material, double nl) {
        return material.kD.scale(nl);
    }

    /**
     * Calculates the specular reflection component using Phong model.
     *
     * @param material the surface material
     * @param nEff     the effective normal vector
     * @param l        the light direction vector
     * @param v        the view direction vector
     * @param nlEff    the dot product of nEff and l
     * @return the scaled specular reflection
     */
    private Double3 calcSpecular(Material material, Vector nEff, Vector l, Vector v, double nlEff) {
        Vector r = nEff.scale(2 * nlEff).subtract(l).normalize();
        double vr = v.dotProduct(r);

        if (vr <= 0) {
            return Double3.ZERO;
        }

        double spec = Math.pow(vr, material.nSh);
        return material.kS.scale(spec);
    }

    /**
     * Checks if the intersection point is unshaded with respect to the light source.
     * It casts a shadow ray from the point towards the light and verifies if any object
     * blocks the light before reaching the source.
     *
     * @param intersection The intersection information including point, normal, light direction, and light source.
     * @return true if the point is not shadowed (unshaded), false if it is in shadow.
     */
    private boolean unshaded(Intersection intersection) {
        // Direction from the intersection point towards the light source
        Vector pointToLight = intersection.lightDirection; // Assuming this vector points from point to light

        // Small offset along the normal to avoid self-shadowing artifacts ("shadow acne")
        Vector delta = intersection.normal.scale(intersection.lightScale < 0 ? DELTA : -DELTA);

        // Create a shadow ray that starts just above the surface point, heading towards the light
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);

        // Calculate intersections of the shadow ray with scene geometries
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);

        // Get the distance from the intersection point to the light source
        double lightDistance = intersection.lightSource.getDistance(intersection.point);

        if (shadowIntersections != null) {
            for (Intersection shadowInter : shadowIntersections) {
                // Distance from the intersection point to the current shadow intersection
                double dist = shadowInter.point.distance(intersection.point);

                // If there is an intersection closer than the light source (and not the same geometry),
                // then the point is in shadow (occluded)
                if (dist < lightDistance && shadowInter.geometry != intersection.geometry) {
                    return false; // Shadow detected
                }
            }
        }

        // No blocking geometry found, point is unshaded
        return true;
    }



    /**
     * Finds the closest intersection point of the given ray with the scene's
     * geometries.
     *
     * @param ray the ray to trace
     * @return the closest intersection, or null if there are no intersections
     */
    private Intersection findClosestIntersection(Ray ray) {
        var intersections = scene.geometries.calculateIntersections(ray);
        return ray.findClosestIntersection(intersections);
    }

    private Color calcGlobalEffects(Intersection intersection,int level, Double3 k) {
        Ray reflectedRay = reflectedRay(intersection);
        Ray refractedRay = refractedRay(intersection);

        Color reflectedColor = calcGlobalEffect(reflectedRay, level, k, intersection.material.kr);
        Color refractedColor = calcGlobalEffect(refractedRay, level, k, intersection.material.kt);

        return reflectedColor.add(refractedColor);
    }
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) return scene.background.scale(kx);
        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, level - 1, kkx).scale(kx) : Color.BLACK;
    }

    /**
     * Constructs a reflected ray based on the incoming ray direction and the normal at the intersection point.
     *
     * @param intersection the intersection containing the point, direction and normal
     * @return a new Ray representing the reflected ray
     */
    private Ray reflectedRay(Intersection intersection) {
        // R = D - 2 * (D · N) * N
        Vector reflectedDirection = intersection.direction
                .subtract(intersection.normal.scale(2 * intersection.rayDotNormal));

//        // Small shift to avoid self-intersection ("shadow acne")
//        Vector delta = intersection.normal.scale(intersection.rayDotNormal < 0 ? DELTA : -DELTA);
//        Point reflectedOrigin = intersection.point.add(delta);
//
//        return new Ray(reflectedOrigin, reflectedDirection);
        return new Ray(intersection.point, reflectedDirection, intersection.normal);

    }


    /**
     * Constructs a refracted ray (same direction as the original ray), slightly shifted to avoid self-shadowing.
     *
     * @param intersection the intersection containing the point and normal
     * @return a new Ray representing the refracted (transmitted) ray
     */
    private Ray refractedRay(Intersection intersection) {
        return new Ray(intersection.point, intersection.rayDirection, intersection.normal);
    }

}
