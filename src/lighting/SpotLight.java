package lighting;

import primitives.*;

/**
 * A spotlight is a {@link PointLight} with a specific direction.
 * <p>
 * The intensity is modulated by the angle between the direction of the spotlight
 * and the vector from the light's position to the target point.
 * <p>
 * The spotlight can also be concentrated into a narrower beam by setting a beam exponent.
 */
public class SpotLight extends PointLight {
    /**
     * The normalized direction vector the spotlight is shining in (d_L).
     */
    private final Vector direction;

    /**
     * Controls the concentration of the beam.
     * A higher value creates a narrower and more focused beam.
     * Default is 1.0.
     */
    private double beamExponent = 1.0;

    /**
     * Constructs a {@code SpotLight} with the given intensity, position, and direction.
     *
     * @param intensity the base color/intensity of the light
     * @param position  the position of the spotlight (P_L)
     * @param direction the direction in which the light is shining (d_L), will be normalized
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Sets the beam exponent to control the narrowness of the spotlight.
     * A higher exponent results in a sharper, more focused beam.
     *
     * @param beamExponent the exponent value (should be >= 0); default is 1.0
     * @return this {@code SpotLight} instance (for method chaining)
     */
    public SpotLight setBeamExponent(double beamExponent) {
        if (beamExponent < 0) {
            this.beamExponent = 1.0;
        } else {
            this.beamExponent = beamExponent;
        }
        return this;
    }

    /**
     * Sets the constant attenuation factor.
     * Overrides to return {@code SpotLight} type for method chaining.
     *
     * @param kC the constant attenuation coefficient
     * @return this {@code SpotLight} instance
     */
    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     * Overrides to return {@code SpotLight} type for method chaining.
     *
     * @param kL the linear attenuation coefficient
     * @return this {@code SpotLight} instance
     */
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     * Overrides to return {@code SpotLight} type for method chaining.
     *
     * @param kQ the quadratic attenuation coefficient
     * @return this {@code SpotLight} instance
     */
    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    /**
     * Computes the color intensity of the spotlight at a given point.
     * <p>
     * The intensity is calculated by combining the attenuation due to distance
     * (as in {@link PointLight}) and the angle between the spotlight direction
     * and the vector to the point.
     *
     * @param p the point to evaluate the light's intensity at
     * @return the resulting {@code Color} at the point
     */
    @Override
    public Color getIntensity(Point p) {
        // Vector from light to point
        Vector lightToPointNormalized = super.getL(p).scale(-1);

        // Cosine of angle between spotlight direction and direction to point
        double cosAlpha = this.direction.dotProduct(lightToPointNormalized);

        // If the angle is greater than 90° or nearly zero, no light is emitted
        if (Util.isZero(cosAlpha) || cosAlpha <= 0) {
            return Color.BLACK;
        }

        // Base intensity from PointLight (includes attenuation)
        Color intensityFromPointLight = super.getIntensity(p);

        // If base intensity is zero (e.g., due to distance), return black
        if (intensityFromPointLight.equals(Color.BLACK)) {
            return Color.BLACK;
        }

        // Apply beam concentration using the exponent
        double beamFactor = (this.beamExponent == 1.0) ?
                cosAlpha :
                Math.pow(cosAlpha, this.beamExponent);

        return intensityFromPointLight.scale(beamFactor);
    }
}
