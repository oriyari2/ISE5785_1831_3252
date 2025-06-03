package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import java.util.List;

import static primitives.Util.isZero;

/**
 * SimpleRayTracer is a basic ray tracer implementation.
 * It computes color based on local illumination: ambient, diffuse, and specular lighting.
 */
public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Find intersections of the ray with the scene's geometries
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        if (intersections == null || intersections.isEmpty()) {
            // No intersections found - return background color
            return scene.background != null ? scene.background : Color.BLACK;
        }

        // Find the closest intersection to the ray origin
        Intersection closestIntersection = findClosestIntersection(ray.getHead(), intersections);
        if (closestIntersection == null) {
            return scene.background != null ? scene.background : Color.BLACK;
        }

        // Calculate the color at the closest intersection point
        return calcColor(closestIntersection, ray);
    }

    /**
     * Finds the closest intersection to the given point from a list of intersections.
     *
     * @param origin        The origin point to measure distance from.
     * @param intersections List of intersections to search.
     * @return The closest Intersection object or null if the list is empty.
     */
    private Intersection findClosestIntersection(Point origin, List<Intersection> intersections) {
        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distance = origin.distance(intersection.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = intersection;
            }
        }
        return closest;
    }

    /**
     * Calculates the color at the intersection point, including emission, diffuse and specular lighting.
     *
     * @param intersection The intersection information.
     * @param ray          The ray that caused the intersection.
     * @return The resulting color at the intersection.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // Preprocess intersection: calculate normal, ray direction scale, etc.
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            // If preprocessing fails (e.g., ray is parallel to surface), return black
            return Color.BLACK;
        }

        // Start with the geometry's emission color
        Color color = intersection.geometry.getEmission();

        // Add local lighting effects (diffuse and specular)
        color = color.add(calcLocalLighting(intersection));

        return color;
    }

    /**
     * Prepares intersection data for shading calculations.
     * Sets the ray direction, normal at the point, and calculates dot product (rayScale).
     *
     * @param intersection The intersection to prepare.
     * @param rayDirection The direction vector of the ray.
     * @return True if the intersection is valid for shading; false otherwise.
     */
    private boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        intersection.rayDirection = rayDirection;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.rayScale = intersection.normal.dotProduct(rayDirection);
        // Return false if the ray is parallel to the surface (dot product zero)
        return !isZero(intersection.rayScale);
    }

    /**
     * Sets light source parameters on the intersection for shading.
     *
     * @param intersection The intersection point being shaded.
     * @param lightSource  The light source to evaluate.
     * @return True if the light contributes to shading (not perpendicular); false otherwise.
     */
    private boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.lightSource = lightSource;
        intersection.lightDirection = lightSource.getL(intersection.point);
        intersection.lightScale = intersection.lightDirection.dotProduct(intersection.normal);
        // Return false if light or view vector is perpendicular to the surface
        return !(isZero(intersection.rayScale) || isZero(intersection.lightScale));
    }

    /**
     * Calculates local lighting (diffuse and specular) for the intersection.
     *
     * @param intersection The intersection information.
     * @return The color contribution from local lighting.
     */
    private Color calcLocalLighting(Intersection intersection) {
        Color resultColor = Color.BLACK;

        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) {
                continue;
            }

            // Calculate diffuse and specular components
            Double3 diffuse = calcDiffuse(intersection);
            Double3 specular = calcSpecular(intersection);

            Color intensity = lightSource.getIntensity(intersection.point);
            Double3 lightContribution = diffuse.add(specular);

            resultColor = resultColor.add(intensity.scale(lightContribution));
        }

        return resultColor;
    }

    /**
     * Calculates the diffuse lighting coefficient at the intersection.
     *
     * @param intersection The intersection information.
     * @return The diffuse coefficient as Double3.
     */
    private Double3 calcDiffuse(Intersection intersection) {
        Double3 kD = intersection.geometry.getMaterial().kD;
        double maxDot = Math.max(0, intersection.lightScale);
        return kD.scale(maxDot);
    }

    /**
     * Calculates the specular lighting coefficient at the intersection.
     *
     * @param intersection The intersection information.
     * @return The specular coefficient as Double3.
     */
    private Double3 calcSpecular(Intersection intersection) {
        Double3 kS = intersection.geometry.getMaterial().kS;
        int nShininess = intersection.geometry.getMaterial().nSh;

        Vector r = intersection.lightDirection.subtract(
                intersection.normal.scale(2 * intersection.lightScale)).normalize();

        double dotRV = r.dotProduct(intersection.rayDirection.scale(-1));
        double maxDot = Math.max(0, dotRV);
        double specFactor = Math.pow(maxDot, nShininess);

        return kS.scale(specFactor);
    }
}
