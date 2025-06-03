package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.isZero;
import static primitives.Util.alignZero;

/**
 * Simple ray tracer that implements basic Phong shading.
 * This class extends RayTracerBase and provides core ray tracing functionality:
 * finding intersections, determining closest intersection, and applying
 * ambient, diffuse, and specular lighting components.
 */
public class SimpleRayTracer extends RayTracerBase {

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
     * @param origin the origin point of the ray
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
     * Calculates the final color at a given intersection point using the Phong reflection model.
     *
     * @param intersection the intersection data
     * @param ray the incoming ray
     * @return the computed color at the intersection
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        Vector v = ray.getDirection();
        Vector n = intersection.geometry.getNormal(intersection.point);
        Material material = intersection.geometry.getMaterial();

        double nv = n.dotProduct(v);
        if (isZero(nv)) {
            Color result = intersection.geometry.getEmission();
            if (scene.ambientLight != null && material != null) {
                result = result.add(scene.ambientLight.getIntensity().scale(material.kA));
            }
            return result;
        }

        Color color = intersection.geometry.getEmission();
        if (scene.ambientLight != null && material != null) {
            color = color.add(scene.ambientLight.getIntensity().scale(material.kA));
        }

        Vector vToCamera = v.scale(-1).normalize();
        color = color.add(calcLocalEffects(intersection, vToCamera, n, material));
        return color;
    }

    /**
     * Calculates the local lighting effects (diffuse + specular) from all light sources.
     *
     * @param intersection the intersection point data
     * @param v the normalized vector pointing to the camera
     * @param n the normal vector at the point
     * @param material the material of the surface
     * @return the resulting color from local illumination
     */
    private Color calcLocalEffects(Intersection intersection, Vector v, Vector n, Material material) {
        Color localColorContribution = Color.BLACK;
        Point p = intersection.point;

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(p);

            double nl = n.dotProduct(l);
            double nv = n.dotProduct(v);

            if (alignZero(nl * nv) > 0) {
                Vector nEff = (nl < 0) ? n.scale(-1) : n;
                double nEffDotL = nEff.dotProduct(l);
                Color lightIntensity = lightSource.getIntensity(p);

                if (!lightIntensity.equals(Color.BLACK) && nEffDotL > 0) {
                    Double3 diffuse = calcDiffuse(material, nEffDotL);
                    Double3 specular = calcSpecular(material, nEff, l, v, nEffDotL);
                    localColorContribution = localColorContribution.add(lightIntensity.scale(diffuse.add(specular)));
                }
            }
        }
        return localColorContribution;
    }

    /**
     * Calculates the diffuse reflection component using Phong model.
     *
     * @param material the surface material
     * @param nl dot product of normal and light direction, expected > 0
     * @return the scaled diffuse reflection
     */
    private Double3 calcDiffuse(Material material, double nl) {
        return material.kD.scale(nl);
    }

    /**
     * Calculates the specular reflection component using Phong model.
     *
     * @param material the surface material
     * @param nEff the effective normal vector
     * @param l the light direction vector
     * @param v the view direction vector
     * @param nlEff the dot product of nEff and l
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
}
