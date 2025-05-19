package unittests.renderer;

import static java.awt.Color.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.AmbientLight;
import primitives.*;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;
import scene.SceneBuilderXML;

/**
 * Test rendering a basic image
 * @author Dan
 */
public class RenderTests {
    /** Default constructor to satisfy JavaDoc generator */
    public RenderTests() { /* to satisfy JavaDoc generator */ }

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
    public void renderTwoColorTest() {
        Scene scene = new Scene("Two color").setBackground(new Color(75, 127, 90))
                .setAmbientLight(new AmbientLight(new Color(255, 191, 191)));
        scene.geometries //
                .add(// center
                        new Sphere(50d, new Point(0, 0, -100)),
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
   /*@Test
   public void renderMultiColorTest() {
      Scene scene = new Scene("Multi color").setAmbientLight(new AmbientLight(new Color(51, 51, 51)));
      scene.geometries //
         .add(// center
              new Sphere(new Point(0, 0, -100), 50),
              // up left
              new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)) //
                 .setEmission(new Color(GREEN)),
              // down left
              new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)) //
                 .setEmission(new Color(RED)),
              // down right
              new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)) //
                 .setEmission(new Color(BLUE)));

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid(100, new Color(WHITE)) //
         .writeToImage("color render test");
   }*/

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
