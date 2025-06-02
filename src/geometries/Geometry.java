package geometries;
import primitives.*;

import java.util.List;

/**
 * An abstract class representing a geometric object in 3D space.
 * <p>
 * This class serves as a base for all geometric shapes and ensures that
 * every shape has a method to compute the normal vector at a given point.
 */
public abstract class Geometry extends Intersectable {

    /**
     * Gets the color of the geometry.
     *
     * @return The color of the geometry
     */
    protected Color emission = Color.BLACK;

    /**
     * The material properties of the geometry, such as reflectivity, transparency, etc.
     */
    private Material material = new Material();

    /**
     * Constructs a Geometry object with default material properties.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties of the geometry.
     *
     * @param material The material to set for the geometry
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }
    /**
     * Computes the normal vector to the surface of the geometric shape at a given point.
     *
     * @param point The point on the surface of the geometric object
     * @return The normal vector at the given point
     */
    public abstract Vector getNormal(Point point);

    /**
     * Gets the color of the geometry's emission.
     *
     * @return The emission color of the geometry
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission The color to set as the emission color
     * @return The current Geometry instance for method chaining
     */
    public Geometry setEmission(Color emission) {
       this.emission = emission;
       return this;
    }

}

