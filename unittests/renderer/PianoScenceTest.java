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

/**
 * Unit tests for the Grand Piano scene.
 * This class contains tests for various components of the Grand Piano scene,
 * including the stage, piano body, legs, keyboard, chandelier, and bench.
 * Each test builds a specific part of the scene and renders it to an image.
 */
public class PianoScenceTest {

    private static final double PIANO_Y_BASE = -50;
    private static final double PIANO_Y_TOP = 50;
    private static final double STAGE_FLOOR_Y = -200;
    private static final double WALL_Z_POSITION = -1500;
    private static final int STAGE_TILE_SIZE = 400; // Side length of the floor tile (triangle-based)
    private static final int WALL_TILE_SIZE = 600; // Side length of the wall tile (triangle-based)

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
    // Updated stageMaterial: glossy brown wood, with kR set to ZERO for no direct reflections
    private final Material stageMaterial = new Material()
            .setKD(0.4).setKS(0.6).setShininess(80) // Shininess from your provided snippet (80)
            .setkR(Double3.ZERO); // CRITICAL CHANGE: Set kR to ZERO for no mirror-like reflections
    // Updated wallMaterial: red velvet
    private final Material wallMaterial = new Material()
            .setKD(0.8).setKS(0.2).setShininess(10) // From your provided snippet
            .setkR(new Double3(0.005, 0.005, 0.005)); // From your provided snippet
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
     * This method is now primarily for general geometries, not specific floor/wall tiles.
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

    /**
     * Test methods for various components of the Grand Piano scene.
     * Each test builds a specific part of the scene and renders it to an image.
     */
    @Test
    void testStageCreation() {
        setupScene("Stage Creation Test");
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30))); // Basic light for component test
        buildStage(); // Call the helper method to build the stage

        cameraBuilder
                .setLocation(new Point(0, 500, 1000))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("stage_creation_test");
    }

    /**
     * Tests the creation of the Grand Piano body.
     * This test builds the piano body and renders it to an image.
     */
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

    /**
     * Tests the creation of the Piano Legs.
     * This test builds the piano legs and renders it to an image.
     */
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

    /**
     * Tests the creation of the Keyboard.
     * This test builds the keyboard and renders it to an image.
     */
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

    /**
     * Tests the creation of the Chandelier.
     * This test builds the chandelier and renders it to an image.
     */
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

    /**
     * Tests the creation of the Bench.
     * This test builds the bench and renders it to an image.
     */
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

    /**
     * Tests the lighting setup in the scene.
     * This test adds lighting to the scene and renders it to an image.
     */
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


    /**
     * Helper method to build the stage.
     * This method is updated to reflect the new material properties and emission colors for the floor and walls.
     */
    private void buildStage() {
        // Stage floor - built using triangles as before
        for (int x = -2000; x < 2000; x += STAGE_TILE_SIZE) {
            for (int z = -1500; z < 1500; z += STAGE_TILE_SIZE) {
                scene.geometries.add(new Triangle(new Point(x, STAGE_FLOOR_Y, z), new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z), new Point(x, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE)).setEmission(new Color(139, 90, 43)).setMaterial(stageMaterial));
                scene.geometries.add(new Triangle(new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z), new Point(x + STAGE_TILE_SIZE, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE), new Point(x, STAGE_FLOOR_Y, z + STAGE_TILE_SIZE)).setEmission(new Color(160, 105, 50)).setMaterial(stageMaterial));
            }
        }

        // Base under piano
        scene.geometries.add(new Polygon(new Point(-650, -201, -850), new Point(650, -201, -850), new Point(650, -201, -150), new Point(-650, -201, -150)).setEmission(new Color(30, 20, 10)).setMaterial(stageMaterial));

        // Back stage wall - horizontal rectangles side by side, alternating colors
        boolean toggleColor = false;
        for (int x = -2000; x < 2000; x += WALL_TILE_SIZE) {
            Color emissionColor = toggleColor ? new Color(120, 20, 20) : new Color(100, 15, 15);
            scene.geometries.add(new Polygon(new Point(x, -200, WALL_Z_POSITION), new Point(x + WALL_TILE_SIZE, -200, WALL_Z_POSITION), new Point(x + WALL_TILE_SIZE, 1800, WALL_Z_POSITION), new Point(x, 1800, WALL_Z_POSITION)).setEmission(emissionColor).setMaterial(wallMaterial));
            toggleColor = !toggleColor;
        }
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
                new Polygon(new Point(-120, -30 - 30, 200),
                        new Point(120, -30 - 30, 200),
                        new Point(120, -20 - 40, 320),
                        new Point(-120, -20 - 40, 320)));

        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(-120, -30 - 30, 200),
                        new Point(-120, -20 - 40, 320),
                        new Point(-120, -50 - 60, 320),
                        new Point(-120, -60 - 70, 200)));

        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(120, -30 - 30, 200),
                        new Point(120, -60 - 70, 200),
                        new Point(120, -50 - 60, 320),
                        new Point(120, -20 - 40, 320)));

        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(-120, -20 - 40, 320),
                        new Point(120, -20 - 40, 320),
                        new Point(120, -50 - 60, 320),
                        new Point(-120, -50 - 60, 320)));

        addGeometriesToScene(new Color(120, 60, 16), benchWoodMaterial,
                new Polygon(new Point(-120, -30 - 30, 200),
                        new Point(-120, -60 - 70, 200),
                        new Point(120, -60 - 70, 200),
                        new Point(120, -30 - 30, 200)));

        addGeometriesToScene(new Color(110, 55, 14), benchWoodMaterial,
                new Polygon(new Point(-120, -130, 200),
                        new Point(120, -130, 200),
                        new Point(120, -110, 320),
                        new Point(-120, -110, 320)));

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
        //    Slightly warmer color for a more inviting atmosphere. Reduced slightly.
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));

        // 2. Directional Light: Simulates a strong, distant light source, like natural light
        //    from a large window or a broad stage wash from overhead, creating general directionality.
        //    Light comes from top-left-front. Reduced intensity.
        scene.lights.add(new DirectionalLight(new Color(180, 170, 160), new Vector(-0.5, -1, -0.5)));

        // 3. Point Light:
        //    a. Chandelier Light: Illuminates the area around the chandelier.
        //       Reduced intensity to minimize unwanted reflections on the floor.
        scene.lights.add(
                new PointLight(new Color(120, 110, 100), new Point(0, 1200, -500)) // Significantly reduced intensity
                        .setKl(0.00008).setKq(0.00001)); // Slightly higher attenuation for local effect

        //    b. Subtle Fill Light (below stage): Very dim light to lift shadows from below.
        //       Kept subtle to avoid over-lighting.
        scene.lights.add(
                new PointLight(new Color(30, 30, 40), new Point(0, -150, 0)) // Slightly dimmer
                        .setKl(0.0005).setKq(0.00005));

        // 4. Spot Lights: Used for focused illumination and dramatic effects.
        //    a. Main Piano Spotlight: Bright, focused light directly on the piano.
        //       Significantly reduced intensity to prevent over-exposure.
        scene.lights.add(
                new SpotLight(new Color(400, 380, 300), new Point(0, 800, -200), new Vector(0, -1, 0.1)) // Reduced intensity
                        .setKl(0.000008).setKq(0.0000008) // Adjusted attenuation slightly
                        .setBeamExponent(40)); // Tightened beam slightly for more focus

        //    b. Front Fill Spotlight: Softer, wider beam from the front to reduce harsh shadows.
        //       Reduced intensity further.
        scene.lights.add(
                new SpotLight(new Color(100, 100, 120), new Point(0, 300, 300), new Vector(0, -0.7, -1)) // Reduced intensity
                        .setKl(0.000001).setKq(0.0000001)
                        .setBeamExponent(5)); // Wider, softer beam

        //    c. Stage Side Lights (Warm & Cool): Add dimension and color variation to the stage.
        //       Reduced intensity slightly for overall balance.
        scene.lights.add(
                new SpotLight(new Color(250, 180, 150), new Point(1000, 500, 0), new Vector(-1, -0.5, 0)) // Reduced intensity
                        .setKl(0.00001).setKq(0.000001)
                        .setBeamExponent(15));
        scene.lights.add(
                new SpotLight(new Color(150, 200, 300), new Point(-1000, 500, 0), new Vector(1, -0.5, 0)) // Reduced intensity
                        .setKl(0.00001).setKq(0.000001)
                        .setBeamExponent(15));

        //    d. Backlight for the Wall: Separates the back wall from the main stage elements.
        //       Reduced intensity slightly.
        scene.lights.add(
                new SpotLight(new Color(100, 70, 150), new Point(0, 400, -1400), new Vector(0, 0, 1)) // Reduced intensity
                        .setKl(0.00002).setKq(0.000002)
                        .setBeamExponent(10));
    }

    /**
     * Initializes the scene and camera for each test.
     */
    private void setupScene(String testName) {
        scene = new Scene(testName);
        cameraBuilder = Camera.getBuilder()
//                --superSampling settings--

                // .setSuperSamplingLevel(9)
              //  .setSamplingMethod(Camera.SamplingMethod.GRID)
                .setIncludeOriginalRayInAA(true)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setVpDistance(1000)
                .setVpSize(2500, 2500)
                .setDebugPrint(0.1)
                .setResolution(1000, 1000)
                .enableDepthOfField(800, 5.0, 16) // focal distance=800, aperture=5, 16 samples

//                -- adaptive sampling settings --
//                .setSamplingMethod(Camera.SamplingMethod.ADAPTIVE)
//                .setAdaptiveMaxLevel(3)
//                .setAdaptiveColorThreshold(5)

//                 --Jittered sampling settings--
//                .setSamplingMethod(Camera.SamplingMethod.JITTERED)  // Enable jittered sampling
//                .setJitterMagnitude(0.3)
//                .setJitterSeed(12345L)
//                .setSuperSamplingLevel(4)
                .setMultithreading(-2);
    }

    /**
     * Test for the complete Grand Piano scene.
     * This test builds the entire scene, including the stage, piano body, legs, keyboard, chandelier, and bench,
     * and renders it to an image.
     */
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
        addSceneLighting();

        // Final camera setup for the full scene
        cameraBuilder
                .setLocation(new Point(800, 400, 800))
                .setDirection(new Point(-200, -50, -400), Vector.AXIS_Y)
                .build()
                .renderImage()
                .writeToImage("grand_piano_on_stage");
    }
}