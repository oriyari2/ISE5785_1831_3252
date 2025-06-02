package renderer;

import geometries.Intersectable.Intersection;
import lighting.AmbientLight;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * SimpleRayTracer is a concrete implementation of the RayTracerBase class.
 * It provides a basic ray tracing algorithm for rendering a scene.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a SimpleRayTracer with the specified scene.
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Use the new intersection method that returns a list of Intersection objects
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        // If there are no intersections, return the background or ambient light
        if (intersections == null || intersections.isEmpty()) {
            return scene.background != null ? scene.background : AmbientLight.NONE.getIntensity();
        }

        // Find the closest intersection to the ray origin
        Intersection closestIntersection = ray.findClosestIntersection(intersections);

        // If no closest intersection is found, return the background or ambient light
        if (closestIntersection == null) {
            return scene.background != null ? scene.background : AmbientLight.NONE.getIntensity();
        }

        // Calculate the color at the closest intersection point
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color at a given intersection in the scene.
     * @param intersection the intersection to calculate the color for
     * @return the color at the given intersection
     */
    private Color calcColor(Intersection intersection) {
        return intersection.geometry.getEmission().add(scene.ambientLight.getIntensity());
    }

}
