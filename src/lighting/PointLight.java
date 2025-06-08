package lighting;

import primitives.*;

/**
 * A point light source that radiates light in all directions from a given position.
 * Its intensity attenuates with distance using constant, linear, and quadratic factors.
 */
public class PointLight extends Light implements LightSource {
    private final Point position;
    private double kC = 1.0; // Constant attenuation factor
    private double kL = 0.0; // Linear attenuation factor
    private double kQ = 0.0; // Quadratic attenuation factor

    /**
     * Constructs a PointLight with the given intensity and position.
     *
     * @param intensity The base color/intensity of the light.
     * @param position  The position of the point light in space.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    @Override
    public Vector getL(Point p) {
        // Return normalized direction from point p to the light source
        return position.subtract(p).normalize();
    }

    @Override
    public double getDistance(Point point) {
        // Return the distance between the point and the light position
        return position.distance(point);
    }

    @Override
    public Color getIntensity(Point p) {
        // Compute attenuation based on distance and scale the light's color
        double attenuation = getAttenuation(p);
        return intensity.scale(1 / attenuation);
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kC Constant attenuation
     * @return This light for method chaining
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kL Linear attenuation
     * @return This light for method chaining
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kQ Quadratic attenuation
     * @return This light for method chaining
     */
    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    /**
     * Computes the attenuation factor (a double) based on the distance to point p.
     *
     * @param p The point where intensity is calculated
     * @return Attenuation factor (always >= 1)
     */
    protected double getAttenuation(Point p) {
        double dSquared = position.distanceSquared(p);
        double d = Math.sqrt(dSquared);
        return kC + kL * d + kQ * dSquared;
    }
}