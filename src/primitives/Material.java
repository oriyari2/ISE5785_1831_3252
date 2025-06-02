package primitives;

public class Material {

    // Material class represents the properties of a material in a 3D scene.
    public Double3 kA = Double3.ONE;

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

}
