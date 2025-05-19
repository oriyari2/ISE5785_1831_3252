package renderer;

import primitives.*;
import scene.Scene;

/**
 Abstract base class for ray tracing.
 This class provides the basic structure for tracing rays in a given scene.
 It holds a reference to the scene and defines an abstract method for tracing rays.
 */
public abstract class RayTracerBase {

    /// The scene to be rendered
    protected final Scene scene;

    /// Constructor that initializes the ray tracer with a specific scene
    /// @param scene the scene to trace rays through
    RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /// Abstract method to trace a single ray and calculate the resulting color
    /// @param ray the ray to trace through the scene
    /// @return the color computed for the ray after interacting with the scene
    public abstract Color traceRay(Ray ray);
}
