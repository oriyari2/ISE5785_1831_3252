package renderer;

import static java.awt.Color.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import primitives.*;
import scene.Scene;
import scene.SceneBuilderXML;

/**
 * Test rendering a basic image
 * @author Dan
 */
class RenderTests {
    /** Default constructor to satisfy JavaDoc generator */
    RenderTests() { /* to satisfy JavaDoc generator */ }

    /** Camera builder of the tests */
    private final Camera.Builder camera = Camera.getBuilder() //
            .setLocation(Point.ZERO).setDirection(new Point(0, 0, -1), Vector.AXIS_Y) //
            .setVpDistance(100) //
            .setVpSize(500, 500);

    /**
     * Produce a scene with basic 3D model and render it into a png image with a
     * grid
     */
    @Test
    void renderTwoColorTest() {
        Scene scene = new Scene("Two color").setBackground(new Color(75, 127, 90))
                .setAmbientLight(new AmbientLight(new Color(255, 191, 191)));
        scene.geometries //
                .add(// center
                        new Sphere(new Point(0, 0, -100), 50d),
                        // up left
                        new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)),
                        // down left
                        new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)),
                        // down right
                        new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)));

        camera //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .setResolution(1000, 1000) //
                .build() //
                .renderImage() //
                .printGrid(100, new Color(YELLOW)) //
                .writeToImage("Two color render test");
    }

    // For stage 6 - please disregard in stage 5
    /**
     * Produce a scene with basic 3D model - including individual lights of the
     * bodies and render it into a png image with a grid
     */
    @Test
    void renderMultiColorTest() {
        Scene scene = new Scene("Multi color").setAmbientLight(new AmbientLight(new Color(51, 51, 51)));
        scene.geometries //
                .add(// center
                        new Sphere(new Point(0, 0, -100), 50),
                        // up left
                        new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
                                .setEmission(new Color(GREEN)),
                        // down left
                        new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                                .setEmission(new Color(RED)),
                        // down right
                        new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                                .setEmission(new Color(BLUE)));

        camera //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .setResolution(1000, 1000) //
                .build() //
                .renderImage() //
                .printGrid(100, new Color(WHITE)) //
                .writeToImage("color render test");
    }

    @Test
    void renderMultiColorLightTest() {
        Scene scene = new Scene("Multi color").setAmbientLight(new AmbientLight(new Color(WHITE)));
        scene.geometries //
                .add(// center
                        new Sphere(new Point(0, 0, -100), 50).setMaterial(new Material().setkA(new Double3(0.4))),
                        // up left
                        new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
                                .setMaterial(new Material().setkA(new Double3(0, 0.8, 0))),
                        // down left
                        new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                                .setMaterial(new Material().setkA(new Double3(0.8, 0, 0))),
                        // down right
                        new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                                .setMaterial(new Material().setkA(new Double3(0.0, 0, 0.8))));
        camera //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .setResolution(1000, 1000) //
                .build() //
                .renderImage() //
                .printGrid(100, new Color(WHITE)) //
                .writeToImage("color render test with lights");
    }

    /** Test for JSON based scene - for bonus */
    @Test
    void basicRenderJson() {
        Scene scene = new Scene("Using Json");
        // enter XML file name and parse from JSON file into scene object instead of the
        // new Scene above,
        // Use the code you added in appropriate packages
        // ...
        // NB: unit tests is not the correct place to put XML parsing code

        camera //
                .setRayTracer(scene, RayTracerType.SIMPLE) //
                .setResolution(1000, 1000) //
                .build() //
                .renderImage() //
                .printGrid(100, new Color(YELLOW)) //
                .writeToImage("xml render test");
    }
    /**
     * Test for rendering a scene loaded from an XML file.
     * <p>
     * This test demonstrates how to load a scene from an external XML configuration,
     * build a camera using the builder pattern, render the image, draw a grid over it,
     * and save the final output to a PNG image.
     * </p>
     */
    @Test
    void basicRenderXml() {
        // Path to the XML file that defines the scene configuration
        String xmlFilePath = "renderTestTwoColors.xml";

        // Load the scene from the XML file, asserting no exception occurs
        Scene scene = assertDoesNotThrow(() -> SceneBuilderXML.loadSceneFromXML(xmlFilePath),
                "Failed to load scene from XML");

        // Build a camera using the builder pattern
        Camera camera = new Camera.Builder()
                .setLocation(Point.ZERO) // Set camera position at the origin (0, 0, 0)
                .setDirection(new Point(0, 0, -1), Vector.AXIS_Y) // Look in the negative Z direction, with Y as up
                .setVpDistance(100) // Distance from the camera to the view plane
                .setVpSize(500, 500) // Size of the view plane (width × height)
                .setRayTracer(scene, RayTracerType.SIMPLE) // Attach a basic ray tracer to the scene
                .setResolution(1000, 1000) // Set output image resolution
                .build(); // Build and return the Camera object

        // Render the image based on the scene and camera configuration
        camera.renderImage();

        // Draw a grid with cell size 100 pixels and white color
        camera.printGrid(100, new Color(WHITE));

        // Save the rendered image to a file
        camera.writeToImage("basicRenderXmlOutput");
    }







}
