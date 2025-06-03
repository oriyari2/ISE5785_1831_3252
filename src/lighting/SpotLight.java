package lighting;

import primitives.*;

/**
 * A spotlight is a point light with a direction.
 * Its intensity is modulated by the angle between the direction and the target point.
 */
public class SpotLight extends PointLight {
    private final Vector direction;

    /**
     * Constructs a SpotLight with intensity, position and direction.
     *
     * @param intensity The base color/intensity of the light.
     * @param position  The position of the spot light.
     * @param direction The direction in which the light is shining (will be normalized).
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets constant attenuation (method chaining).
     */
    public SpotLight setkC(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * Sets linear attenuation (method chaining).
     */
    public SpotLight setkL(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * Sets quadratic attenuation (method chaining).
     */
    public SpotLight setkQ(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double projection = direction.dotProduct(l);

        if (projection <= 0) {
            // The point is outside the cone of the spotlight
            return Color.BLACK;
        }

        double attenuation = getAttenuation(p);
        return intensity.scale(projection / attenuation);
    }
}
