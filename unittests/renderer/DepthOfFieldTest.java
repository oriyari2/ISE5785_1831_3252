package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import primitives.Color;
import primitives.Point;
import scene.Scene;

/**
 * Unit test for demonstrating Depth of Field improvement.
 * This test creates a simple scene with spheres positioned at different depths
 * to showcase the depth of field effect.
 */
public class DepthOfFieldTest {

    /** Scene for the test */
    private Scene scene;
    /** Camera builder for the test */
    private Camera.Builder cameraBuilder;

    // Materials for different spheres
    private final Material redMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80);
    private final Material blueMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80);
    private final Material greenMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80);
    private final Material yellowMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80);
    private final Material purpleMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80);

    /**
     * Test without Depth of Field - all spheres should be equally sharp
     */
    @Test
    void testWithoutDepthOfField() {
        setupScene("Depth of Field Test - Without DOF");

        // Create spheres in diagonal arrangement
        createSpheresInDepth();

        // Add basic lighting
        addBasicLighting();

        // Camera without depth of field - positioned to see all spheres
        cameraBuilder
                .setLocation(new Point(-300, 300, 800))
                .setDirection(new Point(100, -50, -100), Vector.AXIS_Y)
                // No depth of field settings
                .build()
                .renderImage()
                .writeToImage("depth_of_field_diagonal_without");
    }

    /**
     * Test with Depth of Field - focus on front spheres
     * Back spheres should be progressively more blurred
     */
    @Test
    void testWithDepthOfField() {
        setupScene("Depth of Field Test - With DOF");

        // Create spheres in diagonal arrangement
        createSpheresInDepth();

        // Add basic lighting
        addBasicLighting();

        // Camera with depth of field - focus on the front-middle area
        cameraBuilder
                .setLocation(new Point(-300, 300, 800))
                .setDirection(new Point(100, -50, -100), Vector.AXIS_Y)
                .enableDepthOfField(900, 12.0, 25) // focal distance to front-middle spheres, larger aperture for visible effect
                .build()
                .renderImage()
                .writeToImage("depth_of_field_diagonal_with");
    }

    /**
     * Test with strong Depth of Field effect - more pronounced blur on back spheres
     */
    @Test
    void testWithStrongDepthOfField() {
        setupScene("Depth of Field Test - Strong DOF");

        // Create spheres in diagonal arrangement
        createSpheresInDepth();

        // Add basic lighting
        addBasicLighting();

        // Camera with strong depth of field effect
        cameraBuilder
                .setLocation(new Point(-300, 300, 800))
                .setDirection(new Point(100, -50, -100), Vector.AXIS_Y)
                .enableDepthOfField(900, 20.0, 30) // very large aperture for strong blur effect
                .build()
                .renderImage()
                .writeToImage("depth_of_field_diagonal_strong");
    }

    /**
     * Test focusing on middle spheres
     */
    @Test
    void testFocusOnMiddleSpheres() {
        setupScene("Depth of Field Test - Focus Middle");

        // Create spheres in diagonal arrangement
        createSpheresInDepth();

        // Add basic lighting
        addBasicLighting();

        // Camera focusing on middle spheres
        cameraBuilder
                .setLocation(new Point(-300, 300, 800))
                .setDirection(new Point(100, -50, -100), Vector.AXIS_Y)
                .enableDepthOfField(1100, 15.0, 25) // focal distance to middle spheres
                .build()
                .renderImage()
                .writeToImage("depth_of_field_diagonal_middle");
    }

    /**
     * Test focusing on back spheres
     */
    @Test
    void testFocusOnBackSpheres() {
        setupScene("Depth of Field Test - Focus Back");

        // Create spheres in diagonal arrangement
        createSpheresInDepth();

        // Add basic lighting
        addBasicLighting();

        // Camera focusing on back spheres
        cameraBuilder
                .setLocation(new Point(-300, 300, 800))
                .setDirection(new Point(100, -50, -100), Vector.AXIS_Y)
                .enableDepthOfField(1400, 15.0, 25) // focal distance to back spheres
                .build()
                .renderImage()
                .writeToImage("depth_of_field_diagonal_back");
    }

    /**
     * Creates spheres positioned diagonally at different depths with different colors
     */
    private void createSpheresInDepth() {
        // Arrange spheres diagonally from front-left to back-right
        // Each sphere moves further away and to the right

        // Sphere 1 (closest to camera) - Red - front-left
        scene.geometries.add(
                new Sphere(new Point(-200, 100, 300), 60)
                        .setEmission(new Color(200, 50, 50))
                        .setMaterial(redMaterial)
        );

        // Sphere 2 - Blue
        scene.geometries.add(
                new Sphere(new Point(-100, 50, 150), 60)
                        .setEmission(new Color(50, 50, 200))
                        .setMaterial(blueMaterial)
        );

        // Sphere 3 (focal point) - Green - center
        scene.geometries.add(
                new Sphere(new Point(0, 0, 0), 60)
                        .setEmission(new Color(50, 200, 50))
                        .setMaterial(greenMaterial)
        );

        // Sphere 4 - Yellow
        scene.geometries.add(
                new Sphere(new Point(100, -50, -150), 60)
                        .setEmission(new Color(200, 200, 50))
                        .setMaterial(yellowMaterial)
        );

        // Sphere 5 (farthest from camera) - Purple - back-right
        scene.geometries.add(
                new Sphere(new Point(200, -100, -300), 60)
                        .setEmission(new Color(150, 50, 200))
                        .setMaterial(purpleMaterial)
        );

        // Sphere 6 - Orange - even further back
        scene.geometries.add(
                new Sphere(new Point(300, -150, -450), 60)
                        .setEmission(new Color(255, 150, 50))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(80))
        );

        // Sphere 7 - Cyan - furthest back
        scene.geometries.add(
                new Sphere(new Point(400, -200, -600), 60)
                        .setEmission(new Color(50, 200, 200))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(80))
        );

        // Add a simple floor for reference
        scene.geometries.add(
                new Polygon(
                        new Point(-600, -250, -800),
                        new Point(600, -250, -800),
                        new Point(600, -250, 400),
                        new Point(-600, -250, 400)
                )
                        .setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(10))
        );
    }

    /**
     * Adds basic lighting to the scene
     */
    private void addBasicLighting() {
        // Ambient light
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        // Main directional light
        scene.lights.add(
                new DirectionalLight(new Color(150, 150, 150), new Vector(-0.5, -1, -0.5))
        );

        // Point light for additional illumination
        scene.lights.add(
                new PointLight(new Color(100, 100, 100), new Point(200, 200, 600))
                        .setKl(0.0001).setKq(0.00001)
        );
    }

    /**
     * Initializes the scene and camera for the test
     */
    private void setupScene(String testName) {
        scene = new Scene(testName);
        cameraBuilder = Camera.getBuilder()
                .setSuperSamplingLevel(1) // Keep it simple for DOF demonstration
                .setSamplingMethod(Camera.SamplingMethod.GRID)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setVpDistance(1000)
                .setVpSize(800, 800)
                .setResolution(600, 600)
                .setMultithreading(-1);
    }
}