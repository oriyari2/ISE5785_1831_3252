package renderer;

import static java.awt.Color.*;

import geometries.Polygon;
import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import primitives.Color;
import primitives.Point;
import scene.Scene;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class PianoScenceTest {

    // --- Constants for efficiency and readability ---
    private static final double PIANO_Y_BASE = -50;
    private static final double PIANO_Y_TOP = 50;
    private static final double STAGE_FLOOR_Y = -200;
    private static final int STAGE_TILE_SIZE = 200; // Side length of the floor triangle
    private static final double WALL_Z_POSITION = -1500;
    private static final int WALL_TILE_SIZE = 300; // Side length of the wall square

    /** Scene for the tests */
    private Scene scene;
    /** Camera builder for the tests with triangles */
    private Camera.Builder cameraBuilder;

    // Materials definitions - moved to class level for reuse
    private final Material pianoMaterial = new Material()
            .setKD(0.4).setKS(0.6).setShininess(100);
    private final Material whiteKeyMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80)
            .setkR(new Double3(0.1, 0.1, 0.1));
    private final Material blackKeyMaterial = new Material()
            .setKD(0.3).setKS(0.7).setShininess(120)
            .setkR(new Double3(0.05, 0.05, 0.05));
    private final Material stageMaterial = new Material()
            .setKD(0.4).setKS(0.6).setShininess(30)
            .setkR(new Double3(0.1, 0.1, 0.1)); // Reduced to 0.1, 0.1, 0.1 to lessen reflection
    private final Material wallMaterial = new Material()
            .setKD(0.8).setKS(0.2).setShininess(10)
            .setkR(new Double3(0.05, 0.05, 0.05));
    private final Material coloredGlassMaterial = new Material()
            .setKD(0.2).setKS(0.8).setShininess(250)
            .setkT(new Double3(0.7, 0.8, 0.9))
            .setkR(new Double3(0.2, 0.2, 0.2));
    private final Material crystalMaterial = new Material()
            .setKD(0.05).setKS(0.95).setShininess(400)
            .setkT(new Double3(0.95, 0.95, 0.98))
            .setkR(new Double3(0.3, 0.3, 0.3));
    private final Material metalMaterial = new Material()
            .setKD(0.2).setKS(0.8).setShininess(150)
            .setkR(new Double3(0.6, 0.6, 0.6));
    private final Material legMaterial = new Material()
            .setKD(0.9).setKS(0.1).setShininess(10)
            .setkR(new Double3(0.0, 0.0, 0.0));
    private final Material keyboardBaseMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(80)
            .setkR(new Double3(0.1, 0.1, 0.1));
    private final Material benchWoodMaterial = new Material()
            .setKD(0.6).setKS(0.4).setShininess(50);
    private final Material benchLegMaterial = new Material()
            .setKD(0.7).setKS(0.3).setShininess(30);



    /**
     * Helper method to add multiple geometries to the scene,
     * setting their emission and material if they are Geometry instances.
     *
     * @param emission The emission color for the geometries.
     * @param material The material for the geometries.
     * @param geometriesToAdd One or more Intersectable objects to be added.
     */
    private void addGeometriesToScene(Color emission, Material material, Intersectable... geometriesToAdd) {
        List<Intersectable> tempGeometries = new LinkedList<>();
        for (Intersectable geo : geometriesToAdd) {
            // Check if the object is an instance of Geometry before setting emission and material
            if (geo instanceof Geometry) {
                tempGeometries.add(((Geometry) geo).setEmission(emission).setMaterial(material));
            } else {
                // If it's an Intersectable but not a Geometry, add it as is.
                tempGeometries.add(geo);
                System.err.println("Warning: Attempted to set emission and material on a non-Geometry Intersectable. Skipping for: " + geo.getClass().getSimpleName());
            }
        }
        // Only add to scene.geometries if there are items to add to avoid creating empty Geometries objects
        if (!tempGeometries.isEmpty()) {
            scene.geometries.add(new Geometries(tempGeometries.toArray(new Intersectable[0])));
        }
    }

    @Test
    void testStageCreation() {
        setupScene("Stage Creation Test");
        // For individual component tests, add a basic light
        // Note: For full scenes, addSceneLighting will be called.
        // For component tests, we can keep a simpler ambient light here or use addSceneLighting as well.
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));

        buildStage(); // Call the helper method to build the stage

        // Basic camera and render for stage test
        cameraBuilder
                .setLocation(new Point(0, 500, 1000))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("stage_creation_test");
    }

    @Test
    void testGrandPianoBodyCreation() {
        setupScene("Grand Piano Body Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));
        buildGrandPianoBody();

        cameraBuilder
                .setLocation(new Point(0, 200, 500))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("grand_piano_body_test");
    }

    @Test
    void testPianoLegsCreation() {
        setupScene("Piano Legs Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));
        buildPianoLegs();

        cameraBuilder
                .setLocation(new Point(0, -100, 0))
                .setDirection(new Point(0, -200, -500), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("piano_legs_test");
    }

    @Test
    void testKeyboardCreation() {
        setupScene("Keyboard Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));
        buildKeyboard();

        cameraBuilder
                .setLocation(new Point(0, 100, 0))
                .setDirection(new Point(0, 30, -200), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("keyboard_creation_test");
    }

    @Test
    void testChandelierCreation() {
        setupScene("Chandelier Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));
        buildChandelier();

        cameraBuilder
                .setLocation(new Point(0, 1500, 0))
                .setDirection(new Point(0, 1000, -500), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("chandelier_creation_test");
    }

    @Test
    void testBenchCreation() {
        setupScene("Bench Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));
        buildBench();

        cameraBuilder
                .setLocation(new Point(0, 0, 400))
                .setDirection(new Point(0, -50, 0), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("bench_creation_test");
    }

    @Test
    void testLightingSetup() {
        setupScene("Lighting Setup Test");

        // Adding a simple object to see the lighting effects
        addGeometriesToScene(new Color(WHITE), new Material().setKD(0.5).setKS(0.5).setShininess(100),
                new Sphere(new Point(0, 0, 0), 100));

        addSceneLighting(); // Call the updated lighting method

        cameraBuilder
                .setLocation(new Point(0, 200, 500))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("lighting_setup_test");
    }

    @Test
    void grandPianoOnStageScene() {
        setupScene("Grand Piano On Stage Scene");

        // Call individual build methods to construct the full scene
        buildStage();
        buildGrandPianoBody();
        buildPianoLegs();
        buildKeyboard();
        buildChandelier();
        buildBench();
        addSceneLighting(); // Use the enhanced lighting

        // Final camera setup for the full scene
        cameraBuilder
                .setLocation(new Point(800, 400, 800))
                .setDirection(new Point(-200, -50, -400), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("grand_piano_on_stage");
    }

    /**
     * Helper method to build the stage.
     */
    private void buildStage() {
        // Stage floor - built using triangles (as in the original code)
        List<Intersectable> stageFloorGeometries = new LinkedList<>();
        for (int x = -2000; x < 2000; x += STAGE_TILE_SIZE) {
            for (int z = -1500; z < 1500; z += STAGE_TILE_SIZE) {
                // Triangle 1
                stageFloorGeometries.add(
                        new Triangle(
                                new Point(x, STAGE_FLOOR_Y, z),
                                new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z),
                                new Point(x, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE))
                );
                // Triangle 2
                stageFloorGeometries.add(
                        new Triangle(
                                new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z),
                                new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE),
                                new Point(x, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE))
                );
            }
        }
        addGeometriesToScene(new Color(139, 90, 43), stageMaterial, stageFloorGeometries.toArray(new Intersectable[0]));

        // Base under the piano (4-point Polygon)
        addGeometriesToScene(new Color(30, 20, 10), stageMaterial,
                new Polygon(new Point(-650, -201, -850), new Point(650, -201, -850), new Point(650, -201, -150), new Point(-650, -201, -150)));

        // Back stage wall - built using squares (4-point Polygon)
        List<Intersectable> wallGeometries = new LinkedList<>();
        for (int x = -2000; x < 2000; x += WALL_TILE_SIZE) {
            for (int y = -200; y < 1800; y += WALL_TILE_SIZE) {
                wallGeometries.add(
                        new Polygon(
                                new Point(x, y, WALL_Z_POSITION),
                                new Point(x + WALL_TILE_SIZE, y, WALL_Z_POSITION),
                                new Point(x + WALL_TILE_SIZE, y + WALL_TILE_SIZE, WALL_Z_POSITION),
                                new Point(x, y + WALL_TILE_SIZE, WALL_Z_POSITION))
                );
            }
        }
        addGeometriesToScene(new Color(120, 20, 20), wallMaterial, wallGeometries.toArray(new Intersectable[0]));
    }

    /**
     * Helper method to build the grand piano body.
     */
    private void buildGrandPianoBody() {
        Point p1 = new Point(-550, PIANO_Y_BASE, -250);
        Point p2 = new Point(550, PIANO_Y_BASE, -250);
        Point p3 = new Point(550, PIANO_Y_BASE, -450);
        Point p4 = new Point(450, PIANO_Y_BASE, -750);
        Point p5 = new Point(200, PIANO_Y_BASE, -850);
        Point p6 = new Point(-250, PIANO_Y_BASE, -800);
        Point p7 = new Point(-550, PIANO_Y_BASE, -600);

        Point[] basePoints = {p1, p2, p3, p4, p5, p6, p7};
        Point[] topPoints = new Point[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            topPoints[i] = basePoints[i].add(new Vector(0, PIANO_Y_TOP - PIANO_Y_BASE, 0));
        }

        addGeometriesToScene(new Color(5, 5, 5), pianoMaterial,
                new Polygon(basePoints));
        addGeometriesToScene(new Color(8, 8, 8), pianoMaterial,
                new Polygon(topPoints));

        List<Intersectable> pianoSideGeometries = new LinkedList<>();
        for (int i = 0; i < basePoints.length; i++) {
            pianoSideGeometries.add(new Polygon(
                    basePoints[i],
                    basePoints[(i + 1) % basePoints.length],
                    topPoints[(i + 1) % topPoints.length],
                    topPoints[i]));
        }
        addGeometriesToScene(new Color(6, 6, 6), pianoMaterial, pianoSideGeometries.toArray(new Intersectable[0]));
    }

    /**
     * Helper method to build the piano legs.
     */
    private void buildPianoLegs() {
        List<Intersectable> legSpheres = new LinkedList<>();
        for (int i = 0; i < 15; i++) {
            legSpheres.add(new Sphere(new Point(450, -200 + i * 10, -300), 35));
            legSpheres.add(new Sphere(new Point(-450, -200 + i * 10, -300), 35));
            legSpheres.add(new Sphere(new Point(100, -200 + i * 10, -780), 40));
        }
        addGeometriesToScene(new Color(10, 10, 10), legMaterial, legSpheres.toArray(new Intersectable[0]));

        // Metallic rod for the lid
        List<Intersectable> metallicRodSpheres = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            metallicRodSpheres.add(new Sphere(new Point(0, 1400 + i * 10, -500), 8));
        }
        addGeometriesToScene(new Color(200, 180, 120), metalMaterial, metallicRodSpheres.toArray(new Intersectable[0]));
    }

    /**
     * Helper method to build the keyboard.
     */
    private void buildKeyboard() {
        addGeometriesToScene(new Color(5, 5, 5), keyboardBaseMaterial,
                new Polygon(new Point(-530, 20, -240), new Point(530, 20, -240), new Point(530, 25, -140), new Point(-530, 25, -140)));
        addGeometriesToScene(new Color(8, 8, 8), keyboardBaseMaterial,
                new Polygon(new Point(-530, 20, -240), new Point(-530, 25, -140), new Point(-530, 15, -140), new Point(-530, 10, -240)));
        addGeometriesToScene(new Color(8, 8, 8), keyboardBaseMaterial,
                new Polygon(new Point(530, 20, -240), new Point(530, 10, -240), new Point(530, 15, -140), new Point(530, 25, -140)));
        addGeometriesToScene(new Color(10, 10, 10), keyboardBaseMaterial,
                new Polygon(new Point(-530, 25, -140), new Point(530, 25, -140), new Point(530, 15, -140), new Point(-530, 15, -140)));

        addGeometriesToScene(new Color(3, 3, 3), pianoMaterial,
                new Polygon(new Point(-510, 15, -235), new Point(510, 15, -235), new Point(510, 25, -230), new Point(-510, 25, -230)));
        addGeometriesToScene(new Color(4, 4, 4), pianoMaterial,
                new Polygon(new Point(-510, 15, -230), new Point(510, 15, -230), new Point(510, 25, -180), new Point(-510, 25, -180)));

        List<Intersectable> whiteKeys = new LinkedList<>();
        for (int i = 0; i < 52; i++) {
            int x = -450 + i * 18;
            whiteKeys.add(new Polygon(new Point(x, 25, -230), new Point(x + 16, 25, -230), new Point(x + 16, 35, -160), new Point(x, 35, -160)));
            whiteKeys.add(new Polygon(new Point(x, 35, -160), new Point(x + 16, 35, -160), new Point(x + 16, 45, -140), new Point(x, 45, -140)));
            whiteKeys.add(new Polygon(new Point(x, 45, -140), new Point(x + 16, 45, -140), new Point(x + 16, 50, -140), new Point(x, 50, -140)));
        }
        addGeometriesToScene(new Color(255, 255, 255), whiteKeyMaterial, whiteKeys.toArray(new Intersectable[0]));

        List<Intersectable> blackKeys = new LinkedList<>();
        int[] blackKeyIndices = {0, 1, 3, 4, 5};
        for (int octave = 0; octave < 7; octave++) {
            for (int idx : blackKeyIndices) {
                int i = octave * 7 + idx;
                int x = -441 + i * 18;
                blackKeys.add(new Polygon(new Point(x, 35, -230), new Point(x + 10, 35, -230), new Point(x + 10, 50, -180), new Point(x, 50, -180)));
            }
        }
        addGeometriesToScene(new Color(15, 15, 15), blackKeyMaterial, blackKeys.toArray(new Intersectable[0]));
    }

    /**
     * Helper method to build the chandelier.
     */
    private void buildChandelier() {
        List<Intersectable> chandelierElements = new LinkedList<>();
        for (int ring = 0; ring < 3; ring++) {
            int numCrystals = 6 + ring * 2;
            double radius = 150 + ring * 80;
            double height = 1300 - ring * 100;

            for (int i = 0; i < numCrystals; i++) {
                double angle = i * 2 * Math.PI / numCrystals;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle) - 500;

                chandelierElements.add(new Sphere(new Point(x, height, z), 25)); // Colored glass sphere
                for (int j = 0; j < 3; j++) {
                    chandelierElements.add(new Sphere(new Point(x, height - 40 - j * 30, z), 8)); // Crystal spheres
                }
            }
        }
        // Assuming the first sphere in chandelierElements is the colored glass, and the rest are crystals
        if (!chandelierElements.isEmpty()) {
            // Filter and add colored glass spheres
            List<Intersectable> coloredGlassSpheres = new LinkedList<>();
            for (int ring = 0; ring < 3; ring++) {
                int numCrystals = 6 + ring * 2;
                double radius = 150 + ring * 80;
                double height = 1300 - ring * 100;
                for (int i = 0; i < numCrystals; i++) {
                    double angle = i * 2 * Math.PI / numCrystals;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle) - 500;
                    coloredGlassSpheres.add(new Sphere(new Point(x, height, z), 25));
                }
            }
            addGeometriesToScene(new Color(150, 200, 255), coloredGlassMaterial, coloredGlassSpheres.toArray(new Intersectable[0]));

            // Filter and add crystal spheres
            List<Intersectable> crystalSpheres = new LinkedList<>();
            for (int ring = 0; ring < 3; ring++) {
                int numCrystals = 6 + ring * 2;
                double radius = 150 + ring * 80;
                double height = 1300 - ring * 100;
                for (int i = 0; i < numCrystals; i++) {
                    double angle = i * 2 * Math.PI / numCrystals;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle) - 500;
                    for (int j = 0; j < 3; j++) {
                        crystalSpheres.add(new Sphere(new Point(x, height - 40 - j * 30, z), 8));
                    }
                }
            }
            addGeometriesToScene(new Color(255, 255, 255), crystalMaterial, crystalSpheres.toArray(new Intersectable[0]));
        }
    }

    /**
     * Helper method to build the bench.
     */
    private void buildBench() {
        addGeometriesToScene(new Color(139, 69, 19), benchWoodMaterial,
                new Polygon(new Point(-120, -30, 200), new Point(120, -30, 200), new Point(120, -20, 320), new Point(-120, -20, 320)));

        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(-120, -30, 200), new Point(-120, -20, 320), new Point(-120, -50, 320), new Point(-120, -60, 200)));
        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(120, -30, 200), new Point(120, -60, 200), new Point(120, -50, 320), new Point(120, -20, 320)));

        addGeometriesToScene(new Color(110, 55, 14), benchWoodMaterial,
                new Polygon(new Point(-120, -60, 200), new Point(120, -60, 200), new Point(120, -30, 200), new Point(-120, -30, 200)));

        List<Intersectable> benchLegs = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int x = (i % 2 == 0) ? -90 : 90;
            int z = (i < 2) ? 230 : 290;
            for (int j = 0; j < 6; j++) {
                benchLegs.add(new Sphere(new Point(x, -200 + j * 25, z), 15));
            }
        }
        addGeometriesToScene(new Color(101, 67, 33), benchLegMaterial, benchLegs.toArray(new Intersectable[0]));
    }

    /**
     * Helper method to add lighting to the scene.
     * This method is enhanced to provide more realistic lighting using all light source types.
     */
    private void addSceneLighting() {
        // Clear existing lights to ensure a fresh setup for this method call
        scene.lights.clear();

        // 1. Ambient Light: Provides a very subtle, soft overall illumination.
        //    Slightly warmer color for a more inviting atmosphere.
        scene.setAmbientLight(new AmbientLight(new Color(30, 25, 35)));

        // 2. Directional Light: Simulates a strong, distant light source, like natural light
        //    from a large window or a broad stage wash from overhead, creating general directionality.
        //    Light comes from top-left-front.
        scene.lights.add(new DirectionalLight(new Color(250, 240, 230), new Vector(-0.5, -1, -0.5)));

        // 3. Point Light:
        //    a. Chandelier Light: Illuminates the area around the chandelier.
        //       Increased intensity and slightly adjusted attenuation for a wider spread.
        scene.lights.add(
                new PointLight(new Color(220, 210, 190), new Point(0, 1200, -500))
                        .setKl(0.00006).setKq(0.000008)); // Adjusted for more spread

        //    b. Subtle Fill Light (below stage): Very dim light to lift shadows from below, simulating
        //       some light bouncing off the floor or very subtle stage footlights.
        scene.lights.add(
                new PointLight(new Color(40, 40, 50), new Point(0, -150, 0))
                        .setKl(0.0005).setKq(0.00005));

        // 4. Spot Lights: Used for focused illumination and dramatic effects.
        //    a. Main Piano Spotlight: Bright, focused light directly on the piano,
        //       creating strong highlights and shadows. Higher beam exponent for a tighter cone.
        scene.lights.add(
                new SpotLight(new Color(1000, 950, 800), new Point(0, 800, -200), new Vector(0, -1, 0.1))
                        .setKl(0.000005).setKq(0.0000005)
                        .setBeamExponent(30)); // Very tight beam

        //    b. Front Fill Spotlight: Softer, wider beam from the front to reduce harsh shadows
        //       created by the main spotlight on the front of the piano and performer.
        scene.lights.add(
                new SpotLight(new Color(300, 300, 350), new Point(0, 300, 300), new Vector(0, -0.7, -1))
                        .setKl(0.000001).setKq(0.0000001)
                        .setBeamExponent(5)); // Wider, softer beam

        //    c. Stage Side Lights (Warm & Cool): Add dimension and color variation to the stage.
        //       Moderate beam exponent for general illumination of stage areas.
        scene.lights.add(
                new SpotLight(new Color(400, 300, 200), new Point(1000, 500, 0), new Vector(-1, -0.5, 0))
                        .setKl(0.000008).setKq(0.0000008)
                        .setBeamExponent(15)); // Moderate beam
        scene.lights.add(
                new SpotLight(new Color(200, 300, 400), new Point(-1000, 500, 0), new Vector(1, -0.5, 0))
                        .setKl(0.000008).setKq(0.0000008)
                        .setBeamExponent(15)); // Moderate beam

        //    d. Backlight for the Wall: Separates the back wall from the main stage elements,
        //       creating a sense of depth and atmospheric glow.
        scene.lights.add(
                new SpotLight(new Color(150, 100, 200), new Point(0, 400, -1400), new Vector(0, 0, 1))
                        .setKl(0.00001).setKq(0.000001) // Slightly less attenuation for wider wash
                        .setBeamExponent(10)); // General wash on the wall
    }

    /**
     * Initializes the scene and camera for each test.
     */
    private void setupScene(String testName) {
        scene = new Scene(testName);
        cameraBuilder = Camera.getBuilder()
                .setSuperSamplingLevel(6) // Example: 6x6 anti-aliasing
                .setSamplingMethod(Camera.SamplingMethod.GRID)
                .setIncludeOriginalRayInAA(true)
                .setRayTracer(scene, RayTracerType.SIMPLE) // For backward compatibility in existing tests
                .setVpDistance(1000)
                .setVpSize(2500, 2500)
                .setResolution(1000, 1000)
                .setMultithreading(-2);

        // Ambient light is now set in addSceneLighting to allow more control in full scene
    }
}
