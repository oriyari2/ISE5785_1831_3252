package lighting;
import primitives.*;

/**
 * The LightSource interface represents a light source in a 3D scene.
 * It provides methods to get the intensity of the light at a specific point
 * and the direction of the light from that point.
 */
public interface LightSource {

    /**
     * Gets the intensity of the light at a specific point.
     *
     * @param p The point at which the intensity is calculated.
     * @return A Color representing the intensity of the light at the point.
     */
    Color getIntensity(Point p);
    /**
     * Gets the direction of the light from a given point.
     *
     * @param p The point from which the direction is calculated.
     * @return A Vector representing the direction of the light from the point.
     */
    Vector getL(Point p);

}
