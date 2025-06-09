package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * The Scene class represents a 3D scene containing background color,
 * ambient light, and a collection of geometries.
 */
public class Scene {
    /** The name of the scene */
    public String name;

    /** The background color of the scene */
    public Color background = Color.BLACK; // Default background color

    /** The ambient light of the scene */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /** The collection of geometries in the scene */
    public Geometries geometries = new Geometries();

    /**
     * Sets the list of light sources in the scene.
     *
     * @param lights the list of light sources
     * @return the updated Scene object
     */
    public Scene setLights(List<LightSource> lights) {
        this.lights = lights;
        return this;
    }

    /** The list of light sources in the scene */
    public List<LightSource> lights = new LinkedList<>();
    /**
     * Constructs a new Scene with the given name.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background the background color
     * @return the updated Scene object
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the geometries of the scene.
     *
     * @param geometries the collection of geometries
     * @return the updated Scene object
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light
     * @return the updated Scene object
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }
}
