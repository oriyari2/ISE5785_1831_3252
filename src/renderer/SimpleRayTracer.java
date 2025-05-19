package renderer;

import lighting.AmbientLight;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * SimpleRayTracer is a concrete implementation of the RayTracerBase class.
 * It provides a basic ray tracing algorithm for rendering a scene.
 */
public class SimpleRayTracer extends RayTracerBase {
    /**
     * Constructor for SimpleRayTracer.
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        List<Point> points = scene.geometries.findIntersections(ray);
        if (points == null) {
            return scene.background != null ? scene.background : AmbientLight.NONE.getIntensity(); // ברירת מחדל לשחור
        }
        Point closestPoint = ray.findClosestPoint(points);
        return calcColor(closestPoint) != null ? calcColor(closestPoint) : Color.BLACK;
    }


    private Color calcColor(Point point) {
        return scene.ambientLight.getIntensity();
    }


}
