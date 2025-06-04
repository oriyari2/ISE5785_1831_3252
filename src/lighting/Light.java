package lighting;
import primitives.Color;


/**
 * Abstract class representing a light source in a 3D scene.
 * It provides the basic properties and methods that all light sources should have.
 */
abstract class Light {
    /**
     * The intensity of the light, represented as a color.
     * This color indicates how bright the light is and its color characteristics.
     */
    protected final Color intensity;

    /**
     * Constructs a Light with the specified intensity.
     *
     * @param intensity The color representing the intensity of the light.
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Gets the intensity of the light.
     *
     * @return The color representing the intensity of the light.
     */
    public Color getIntensity() {
        return intensity;
    }
}

