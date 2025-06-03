package lighting;

import primitives.*;

/**
 * A spotlight is a point light with a direction.
 * Its intensity is modulated by the angle between the direction and the target point.
 */
public class SpotLight extends PointLight {
    private final Vector direction; // d_L in the formula: direction the spotlight is shining

    /**
     * Constructs a SpotLight with intensity, position and direction.
     *
     * @param intensity The base color/intensity of the light.
     * @param position  The position of the spot light (P_L).
     * @param direction The direction in which the light is shining (d_L, will be normalized).
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
        Vector l_from_light_to_point = super.getL(p).scale(-1);
        double projection = direction.dotProduct(l_from_light_to_point);

        if (Util.isZero(projection) || projection < 0) {
            return Color.BLACK;
        }

        double factor = projection;
        double attenuation = getAttenuation(p);

        return super.intensity.scale(factor / attenuation);
    }
}