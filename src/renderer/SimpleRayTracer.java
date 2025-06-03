package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import primitives.Material; // Assuming Material is in primitives package

import java.util.List;

import static primitives.Util.isZero;

public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

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

    private Color calcColor(Intersection intersection, Ray ray) {
        // Preprocessing: get normal, view direction, etc.
        // The original preprocessIntersection logic can be kept or integrated here.
        // For simplicity, let's assume normal is available and rayDirection is V_incoming.
        Vector v_incoming = ray.getDirection();
        Vector normal = intersection.geometry.getNormal(intersection.point);

        // If ray hits from behind or parallel to surface (optional check, depends on requirements)
        // double nv_incoming = normal.dotProduct(v_incoming);
        // if (isZero(nv_incoming)) {
        //     return Color.BLACK; // Or background, or emissive only
        // }
        // if (nv_incoming > 0) { // Hitting back-face, normal points away from ray
        //    // Handle back-face culling or two-sided materials if needed
        //    // For now, let's assume we light it, or use a flipped normal for lighting.
        //    // normal = normal.scale(-1); // Example if always lighting front relative to ray
        // }


        // Start with the geometry's emission color
        Color color = intersection.geometry.getEmission();
        Material material = intersection.geometry.getMaterial();

        // Add ambient light from the scene
        if (scene.ambientLight != null && material != null) {
            // Material.kA is public Double3 kA = Double3.ONE; by default
            // scene.ambientLight.getIntensity() should provide the ambient light color Ia
            // Make sure AmbientLight has a getIntensity() method, likely inherited from Light.
            // If Light class has `protected Color intensity;`, then a public getIntensity() is needed.
            // For example, in Light.java: public Color getIntensity() { return this.intensity; }
            color = color.add(scene.ambientLight.getIntensity().scale(material.kA));
        }


        // Add local lighting effects (diffuse and specular)
        // The ray parameter provides the incoming ray direction for V calculation
        color = color.add(calcLocalLighting(intersection, ray, normal, material));

        return color;
    }

    // Original preprocessIntersection - might be useful to call or integrate
    // private boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
    //     intersection.rayDirection = rayDirection; // Storing V_incoming
    //     intersection.normal = intersection.geometry.getNormal(intersection.point);
    //     intersection.rayScale = intersection.normal.dotProduct(rayDirection); // N dot V_incoming
    //     return !isZero(intersection.rayScale);
    // }

    private Color calcLocalLighting(Intersection intersection, Ray ray, Vector normal, Material material) {
        Color resultColor = Color.BLACK;
        Point p = intersection.point;
        Vector v = ray.getDirection().scale(-1).normalize(); // Vector from point to camera (V)

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(p); // Vector from point p to light source, normalized
            double nl = normal.dotProduct(l);

            // Light contributes only if it's on the same side as the normal (for diffuse)
            // and view vector is on the same side as normal (for one-sided surfaces)
            // A common check: nl > 0 and normal.dotProduct(v) > 0
            // Or, if nl and normal.dotProduct(v) have the same sign.
            // For simplicity here, only check nl > 0 for diffuse. Specular might still apply
            // if nl < 0 for transparent/refractive materials, but that's more advanced.
            if (nl > 0) { // Light strikes the front of the surface
                Color lightIntensityAtP = lightSource.getIntensity(p); // Attenuated intensity
                if (!lightIntensityAtP.equals(Color.BLACK)) {
                    Double3 diffuse = calcDiffuse(material, nl);
                    Double3 specular = calcSpecular(material, normal, l, v, nl);

                    Double3 lightContribution = diffuse.add(specular);
                    resultColor = resultColor.add(lightIntensityAtP.scale(lightContribution));
                }
            }
        }
        return resultColor;
    }

    private Double3 calcDiffuse(Material material, double nl) {
        // nl is N.L, should be > 0 from caller for diffuse contribution
        // Material.kD is public Double3 kD = Double3.ZERO by default
        return material.kD.scale(Math.max(0,nl)); // Ensure non-negative
    }

    private Double3 calcSpecular(Material material, Vector n, Vector l, Vector v, double nl) {
        // Material.kS is public Double3 kS = Double3.ZERO by default
        // Material.nSh is public int nSh = 0 by default
        // nl is N.L (dot product of normal and light direction)

        // R = 2(N.L)N - L  (assuming L and N are normalized)
        Vector r = n.scale(2 * nl).subtract(l).normalize(); // Corrected reflection vector R

        double vr = v.dotProduct(r); // V.R

        // If nSh is 0, Math.pow(positive, 0) is 1. Math.pow(0,0) is 1.
        // This makes specular very broad if nSh = 0. Usually nSh >= 1.
        double specFactor = (material.nSh == 0 && vr <=0) ? 0 : Math.pow(Math.max(0, vr), material.nSh);

        return material.kS.scale(specFactor);
    }
}