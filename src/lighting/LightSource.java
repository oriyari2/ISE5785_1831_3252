package lighting;
import primitives.*;

public interface LightSource {

    Color getIntensity(Point p);

    Vector getL(Point p);

    /**
     * Returns the distance from the given point to the light source.
     * For directional lights, returns Double.POSITIVE_INFINITY.
     *
     * @param point The point in the scene.
     * @return Distance to the light source.
     */
    double getDistance(Point point);
}
