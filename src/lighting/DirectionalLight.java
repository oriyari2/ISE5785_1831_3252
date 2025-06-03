package lighting;

import primitives.*;

/**
 * A directional light that has a constant intensity and direction.
 * It simulates light from a very far source, like the sun.
 */
public class DirectionalLight extends Light implements LightSource {
    private final Vector direction;

    /**
     * Constructs a DirectionalLight with the specified intensity and direction.
     *
     * @param intensity The color representing the light's intensity.
     * @param direction The direction vector of the light (will be normalized).
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        // Return normalized vector towards the light (opposite direction)
        return direction.scale(-1);
    }

    @Override
    public Color getIntensity(Point p) {
        // No attenuation; constant intensity
        return intensity;
    }
}
