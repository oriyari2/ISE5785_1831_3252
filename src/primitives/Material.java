package primitives;

public class Material {

    // Material class represents the properties of a material in a 3D scene.
    public Double3 kA = Double3.ONE;

    /**
     * The diffuse coefficient (kD) represents how much light is reflected diffusely.
     * It is a measure of the material's ability to scatter light in many directions.
     */
    public Double3 kD = Double3.ZERO;

    /**
     * The specular coefficient (kS) represents how much light is reflected specularly.
     * It is a measure of the material's ability to reflect light in a specific direction,
     * creating highlights on the surface.
     */
    public Double3 kS = Double3.ZERO;

    /**
     * The shininess coefficient (nSh) determines the size of the specular highlight.
     * A higher value results in a smaller, sharper highlight, while a lower value results in a larger, softer highlight.
     */
    public int nSh = 0;
    /**
     * Constructs a Material with default properties.
     * The default ambient coefficient (kA) is set to 1.
     */
    public Material setkA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Constructs a Material with a specified ambient coefficient.
     *
     * @param kA The ambient coefficient as a {@link Double3} object.
     */
    public Material setkA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Constructs a Material with a specified diffuse coefficient.
     * The diffuse coefficient (kD) represents how much light is reflected diffusely.
     * It is a measure of the material's ability to scatter light in many directions.
     *
     * @param kD The diffuse coefficient as a {@link Double3} object.
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Constructs a Material with a specified diffuse coefficient.
     *
     * @param kD The diffuse coefficient as a {@link Double3} object.
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Constructs a Material with a specified specular coefficient.
     *
     * @param kS The specular coefficient as a {@link Double3} object.
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Constructs a Material with a specified specular coefficient.
     *
     * @param kS The specular coefficient as a {@link Double3} object.
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess coefficient (nSh) of the material.
     * The shininess coefficient determines the size of the specular highlight.
     * A higher value results in a smaller, sharper highlight, while a lower value results in a larger, softer highlight.
     *
     * @param nSh The shininess coefficient as an integer.
     * @return The current Material instance for method chaining.
     */
    public Material setnSh(int nSh) {
        this.nSh = nSh;
        return this;
    }


}
