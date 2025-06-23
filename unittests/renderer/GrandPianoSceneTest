package renderer;

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import primitives.*;
import scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for rendering a grand piano scene with comprehensive stage setup.
 * This class creates a detailed 3D scene featuring a grand piano, stage, lighting,
 * and decorative elements. The scene can be rendered as a complete composition
 * or individual components can be tested separately.
 *
 * The scene includes:
 * - Grand piano with detailed keyboard and body
 * - Stage with wooden floor and velvet walls
 * - Piano bench with wooden construction
 * - Chandelier with colored glass crystals
 * - Multiple lighting sources for dramatic effect
 *
 * @author Elisheva Schnur & Oriya Yitzhaky
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrandPianoSceneTest {

    /** The main scene for the grand piano rendering */
    private Scene scene;

    /** Camera builder for positioning and configuring the camera */
    private Camera.Builder cameraBuilder;

    /** Collection of all geometries in the scene */
    private Geometries allGeometries;

    /** List of all light sources in the scene */
    private List<LightSource> allLights;

    // =========================== MATERIAL DEFINITIONS ===========================

    /** Material for the main piano body - glossy black finish */
    private Material pianoMaterial;

    /** Material for white piano keys - slightly reflective ivory */
    private Material whiteKeyMaterial;

    /** Material for black piano keys - deep black with high shine */
    private Material blackKeyMaterial;

    /** Material for stage floor - rich brown wood texture */
    private Material stageMaterial;

    /** Material for wall surfaces - red velvet texture */
    private Material wallMaterial;

    /** Material for colored glass elements in chandelier */
    private Material coloredGlassMaterial;

    /** Material for crystal decorations - highly transparent and reflective */
    private Material crystalMaterial;

    /** Material for metallic surfaces like chandelier frame */
    private Material metalMaterial;

    /** Material for piano legs - matte black finish */
    private Material legMaterial;

    /** Material for keyboard base - dark matte surface */
    private Material keyboardBaseMaterial;

    // =========================== GEOMETRIC CONSTANTS ===========================

    /** Y-coordinate for the base of the piano */
    private static final double PIANO_Y_BASE = -50;

    /** Y-coordinate for the top of the piano */
    private static final double PIANO_Y_TOP = 50;

    /** Number of white keys on the piano keyboard */
    private static final int WHITE_KEYS_COUNT = 52;

    /** Width of each white key */
    private static final int WHITE_KEY_WIDTH = 16;

    /** Spacing between white keys */
    private static final int WHITE_KEY_SPACING = 18;

    /** Width of each black key */
    private static final int BLACK_KEY_WIDTH = 10;

    /**
     * Sets up the test environment before each test execution.
     * Initializes the scene, camera, geometry collections, and all materials.
     */
    @BeforeEach
    void setUp() {
        scene = new Scene("Grand Piano Scene");
        allGeometries = new Geometries();
        allLights = new ArrayList<>();

        cameraBuilder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE);

        initializeMaterials();
    }

    /**
     * Initializes all material properties used throughout the scene.
     * Each material is carefully crafted to achieve realistic lighting effects.
     */
    private void initializeMaterials() {
        pianoMaterial = new Material()
                .setKD(0.4).setKS(0.6).setShininess(100);

        whiteKeyMaterial = new Material()
                .setKD(0.6).setKS(0.4).setShininess(80)
                .setkR(new Double3(0.1, 0.1, 0.1));

        blackKeyMaterial = new Material()
                .setKD(0.3).setKS(0.7).setShininess(120)
                .setkR(new Double3(0.05, 0.05, 0.05));

        stageMaterial = new Material()
                .setKD(0.4).setKS(0.6).setShininess(80)
                .setkR(new Double3(0.3, 0.2, 0.1));

        wallMaterial = new Material()
                .setKD(0.8).setKS(0.2).setShininess(10)
                .setkR(new Double3(0.05, 0.05, 0.05));

        coloredGlassMaterial = new Material()
                .setKD(0.2).setKS(0.8).setShininess(250)
                .setkT(new Double3(0.7, 0.8, 0.9))
                .setkR(new Double3(0.2, 0.2, 0.2));

        crystalMaterial = new Material()
                .setKD(0.05).setKS(0.95).setShininess(400)
                .setkT(new Double3(0.95, 0.95, 0.98))
                .setkR(new Double3(0.3, 0.3, 0.3));

        metalMaterial = new Material()
                .setKD(0.2).setKS(0.8).setShininess(150)
                .setkR(new Double3(0.6, 0.6, 0.6));

        legMaterial = new Material()
                .setKD(0.9).setKS(0.1).setShininess(10)
                .setkR(new Double3(0.0, 0.0, 0.0));

        keyboardBaseMaterial = new Material()
                .setKD(0.6).setKS(0.4).setShininess(80)
                .setkR(new Double3(0.1, 0.1, 0.1));
    }

    /**
     * Helper method to add a polygon geometry to the scene with specified emission and material.
     * This method reduces code duplication when creating similar geometric elements.
     *
     * @param vertices The vertices that define the polygon
     * @param emission The emission color of the polygon
     * @param material The material properties of the polygon
     * @return The created Polygon geometry
     */
    private Polygon addPolygon(Point[] vertices, Color emission, Material material) {
        Polygon polygon = new Polygon(vertices)
                .setEmission(emission)
                .setMaterial(material);
        allGeometries.add(polygon);
        return polygon;
    }

    /**
     * Helper method to add a triangle geometry to the scene with specified emission and material.
     *
     * @param p1 First vertex of the triangle
     * @param p2 Second vertex of the triangle
     * @param p3 Third vertex of the triangle
     * @param emission The emission color of the triangle
     * @param material The material properties of the triangle
     * @return The created Triangle geometry
     */
    private Triangle addTriangle(Point p1, Point p2, Point p3, Color emission, Material material) {
        Triangle triangle = new Triangle(p1, p2, p3)
                .setEmission(emission)
                .setMaterial(material);
        allGeometries.add(triangle);
        return triangle;
    }

    /**
     * Helper method to add a sphere geometry to the scene with specified emission and material.
     *
     * @param center The center point of the sphere
     * @param radius The radius of the sphere
     * @param emission The emission color of the sphere
     * @param material The material properties of the sphere
     * @return The created Sphere geometry
     */
    private Sphere addSphere(Point center, double radius, Color emission, Material material) {
        Sphere sphere = new Sphere(center, radius)
                .setEmission(emission)
                .setMaterial(material);
        allGeometries.add(sphere);
        return sphere;
    }

    /**
     * Helper method to add a spotlight to the scene with specified parameters.
     *
     * @param intensity The color intensity of the light
     * @param position The position of the light source
     * @param direction The direction the light is pointing
     * @param kl Linear attenuation coefficient
     * @param kq Quadratic attenuation coefficient
     * @return The created SpotLight
     */
    private SpotLight addSpotLight(Color intensity, Point position, Vector direction, double kl, double kq) {
        SpotLight light = new SpotLight(intensity, position, direction).setKl(kl).setKq(kq);
        allLights.add(light);
        return light;
    }

    /**
     * Creates the stage foundation with wooden floor panels arranged in a grid pattern.
     * The stage provides the base platform for the entire scene.
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    void createStageFloor() {
        // Create wooden floor panels in a grid pattern
        for(int x = -2000; x < 2000; x += 200) {
            for(int z = -1500; z < 1500; z += 200) {
                // First triangle of each floor panel
                addTriangle(
                        new Point(x, -200, z),
                        new Point(x + 200, -200, z),
                        new Point(x, -200, z + 200),
                        new Color(139, 90, 43),
                        stageMaterial);

                // Second triangle of each floor panel
                addTriangle(
                        new Point(x + 200, -200, z),
                        new Point(x + 200, -200, z + 200),
                        new Point(x, -200, z + 200),
                        new Color(160, 105, 50),
                        stageMaterial);
            }
        }

        // Base platform under the piano area
        addPolygon(
                new Point[]{
                        new Point(-650, -201, -850),
                        new Point(650, -201, -850),
                        new Point(650, -201, -150),
                        new Point(-650, -201, -150)
                },
                new Color(30, 20, 10),
                stageMaterial);
    }

    /**
     * Creates the main body structure of the grand piano.
     * This includes the base, top surface, and side panels that form the piano's distinctive shape.
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    void createPianoBody() {
        // Define the characteristic grand piano shape with curved edges
        Point p1 = new Point(-550, PIANO_Y_BASE, -250);
        Point p2 = new Point(550, PIANO_Y_BASE, -250);
        Point p3 = new Point(550, PIANO_Y_BASE, -450);
        Point p4 = new Point(450, PIANO_Y_BASE, -750);
        Point p5 = new Point(200, PIANO_Y_BASE, -850);
        Point p6 = new Point(-250, PIANO_Y_BASE, -800);
        Point p7 = new Point(-550, PIANO_Y_BASE, -600);

        Point[] basePoints = {p1, p2, p3, p4, p5, p6, p7};

        // Create corresponding top points
        Point[] topPoints = new Point[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            topPoints[i] = basePoints[i].add(new Vector(0, PIANO_Y_TOP - PIANO_Y_BASE, 0));
        }

        // Piano base surface
        addPolygon(basePoints, new Color(5, 5, 5), pianoMaterial);

        // Piano top surface
        addPolygon(topPoints, new Color(8, 8, 8), pianoMaterial);

        // Side panels of the piano
        for (int i = 0; i < basePoints.length; i++) {
            addPolygon(
                    new Point[]{
                            basePoints[i],
                            basePoints[(i + 1) % basePoints.length],
                            topPoints[(i + 1) % topPoints.length],
                            topPoints[i]
                    },
                    new Color(6, 6, 6),
                    pianoMaterial);
        }
    }

    /**
     * Creates the three supporting legs of the grand piano.
     * Each leg is constructed from multiple spheres to create a cylindrical appearance.
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    void createPianoLegs() {
        // Define positions for the three piano legs
        Point[] legPositions = {
                new Point(450, -200, -300),    // Right front leg
                new Point(-450, -200, -300),   // Left front leg
                new Point(100, -200, -780)     // Back leg
        };

        double[] legRadii = {35, 35, 40}; // Different radii for visual variety

        // Create each leg using stacked spheres
        for (int legIndex = 0; legIndex < legPositions.length; legIndex++) {
            Point basePosition = legPositions[legIndex];
            double radius = legRadii[legIndex];

            for(int i = 0; i < 15; i++) {
                addSphere(
                        new Point(basePosition.xyz.d1(), basePosition.xyz.d2() + i * 10, basePosition.xyz.d3()),
                        radius,
                        new Color(10 + legIndex * 2, 10 + legIndex * 2, 10 + legIndex * 2),
                        legMaterial);
            }
        }
    }

    /**
     * Creates the keyboard structure including the base and support framework.
     * This provides the foundation for the piano keys.
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    void createKeyboardBase() {
        // Main keyboard base surface
        addPolygon(
                new Point[]{
                        new Point(-530, 20, -240),
                        new Point(530, 20, -240),
                        new Point(530, 25, -140),
                        new Point(-530, 25, -140)
                },
                new Color(5, 5, 5),
                keyboardBaseMaterial);

        // Left side panel
        addPolygon(
                new Point[]{
                        new Point(-530, 20, -240),
                        new Point(-530, 25, -140),
                        new Point(-530, 15, -140),
                        new Point(-530, 10, -240)
                },
                new Color(8, 8, 8),
                keyboardBaseMaterial);

        // Right side panel
        addPolygon(
                new Point[]{
                        new Point(530, 20, -240),
                        new Point(530, 10, -240),
                        new Point(530, 15, -140),
                        new Point(530, 25, -140)
                },
                new Color(8, 8, 8),
                keyboardBaseMaterial);

        // Front surface
        addPolygon(
                new Point[]{
                        new Point(-530, 25, -140),
                        new Point(530, 25, -140),
                        new Point(530, 15, -140),
                        new Point(-530, 15, -140)
                },
                new Color(10, 10, 10),
                keyboardBaseMaterial);

        // Black support strips under the keys
        addPolygon(
                new Point[]{
                        new Point(-510, 15, -235),
                        new Point(510, 15, -235),
                        new Point(510, 25, -230),
                        new Point(-510, 25, -230)
                },
                new Color(3, 3, 3),
                pianoMaterial);

        addPolygon(
                new Point[]{
                        new Point(-510, 15, -230),
                        new Point(510, 15, -230),
                        new Point(510, 25, -180),
                        new Point(-510, 25, -180)
                },
                new Color(4, 4, 4),
                pianoMaterial);
    }

    /**
     * Creates all the white keys of the piano keyboard.
     * Each key is constructed with multiple surfaces for realistic appearance.
     */
    @Test
    @org.junit.jupiter.api.Order(5)
    void createWhiteKeys() {
        for (int i = 0; i < WHITE_KEYS_COUNT; i++) {
            int x = -450 + i * WHITE_KEY_SPACING;

            // Main key surface
            addPolygon(
                    new Point[]{
                            new Point(x, 25, -230),
                            new Point(x + WHITE_KEY_WIDTH, 25, -230),
                            new Point(x + WHITE_KEY_WIDTH, 35, -160),
                            new Point(x, 35, -160)
                    },
                    new Color(255, 255, 255),
                    whiteKeyMaterial);

            // Top surface of the key
            addPolygon(
                    new Point[]{
                            new Point(x, 35, -160),
                            new Point(x + WHITE_KEY_WIDTH, 35, -160),
                            new Point(x + WHITE_KEY_WIDTH, 45, -140),
                            new Point(x, 45, -140)
                    },
                    new Color(248, 248, 248),
                    whiteKeyMaterial);

            // Front edge of the key
            addPolygon(
                    new Point[]{
                            new Point(x, 45, -140),
                            new Point(x + WHITE_KEY_WIDTH, 45, -140),
                            new Point(x + WHITE_KEY_WIDTH, 50, -140),
                            new Point(x, 50, -140)
                    },
                    new Color(250, 250, 250),
                    whiteKeyMaterial);
        }
    }

    /**
     * Creates the black keys of the piano keyboard following the standard piano pattern.
     * Black keys are positioned between specific white keys according to musical conventions.
     */
    @Test
    @org.junit.jupiter.api.Order(6)
    void createBlackKeys() {
        // Standard pattern for black key placement (sharps and flats)
        int[] blackKeyIndices = {0, 1, 3, 4, 5}; // Pattern within each octave

        for (int octave = 0; octave < 7; octave++) {
            for (int idx : blackKeyIndices) {
                int i = octave * 7 + idx;
                if (i >= WHITE_KEYS_COUNT - 7) break; // Prevent overflow

                int x = -441 + i * WHITE_KEY_SPACING; // Offset for positioning between white keys

                addPolygon(
                        new Point[]{
                                new Point(x, 35, -230),
                                new Point(x + BLACK_KEY_WIDTH, 35, -230),
                                new Point(x + BLACK_KEY_WIDTH, 50, -180),
                                new Point(x, 50, -180)
                        },
                        new Color(15, 15, 15),
                        blackKeyMaterial);
            }
        }
    }

    /**
     * Creates a piano bench with wooden construction and padded top.
     * The bench includes detailed legs and realistic proportions.
     */
    @Test
    @org.junit.jupiter.api.Order(7)
    void createPianoBench() {
        Material benchMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(50);

        // Main bench seat surface
        addPolygon(
                new Point[]{
                        new Point(-120, -30, 200),
                        new Point(120, -30, 200),
                        new Point(120, -20, 320),
                        new Point(-120, -20, 320)
                },
                new Color(139, 69, 19),
                benchMaterial);

        // Side panels
        addPolygon(
                new Point[]{
                        new Point(-120, -30, 200),
                        new Point(-120, -20, 320),
                        new Point(-120, -50, 320),
                        new Point(-120, -60, 200)
                },
                new Color(120, 60, 16),
                benchMaterial);

        addPolygon(
                new Point[]{
                        new Point(120, -30, 200),
                        new Point(120, -60, 200),
                        new Point(120, -50, 320),
                        new Point(120, -20, 320)
                },
                new Color(120, 60, 16),
                benchMaterial);

        // Front panel
        addPolygon(
                new Point[]{
                        new Point(-120, -60, 200),
                        new Point(120, -60, 200),
                        new Point(120, -30, 200),
                        new Point(-120, -30, 200)
                },
                new Color(110, 55, 14),
                benchMaterial);

        // Bench legs
        for(int i = 0; i < 4; i++) {
            int x = (i % 2 == 0) ? -90 : 90;
            int z = (i < 2) ? 230 : 290;

            for(int j = 0; j < 6; j++) {
                addSphere(
                        new Point(x, -200 + j * 25, z),
                        15,
                        new Color(101, 67, 33),
                        new Material().setKD(0.7).setKS(0.3).setShininess(30));
            }
        }
    }

    /**
     * Creates an ornate chandelier with multiple tiers of colored glass crystals.
     * The chandelier adds elegance and provides additional light sources for the scene.
     */
    @Test
    @org.junit.jupiter.api.Order(8)
    void createChandelier() {
        // Supporting rod for the chandelier
        for(int i = 0; i < 20; i++) {
            addSphere(
                    new Point(0, 1400 + i * 10, -500),
                    8,
                    new Color(200, 180, 120),
                    metalMaterial);
        }

        // Create three tiers of crystal decorations
        for(int ring = 0; ring < 3; ring++) {
            int numCrystals = 6 + ring * 2;
            double radius = 150 + ring * 80;
            double height = 1300 - ring * 100;

            for(int i = 0; i < numCrystals; i++) {
                double angle = i * 2 * Math.PI / numCrystals;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle) - 500;

                // Main colored glass sphere
                addSphere(
                        new Point(x, height, z),
                        25,
                        new Color(150, 200, 255),
                        coloredGlassMaterial);

                // Hanging crystal decorations
                for(int j = 0; j < 3; j++) {
                    addSphere(
                            new Point(x, height - 40 - j * 30, z),
                            8,
                            new Color(255, 255, 255),
                            crystalMaterial);
                }
            }
        }
    }

    /**
     * Creates the backdrop walls with rich red velvet texture.
     * The walls provide an elegant theater-like atmosphere for the scene.
     */
    @Test
    @org.junit.jupiter.api.Order(9)
    void createWalls() {
        // Back wall with triangular panels for texture
        for(int x = -2000; x < 2000; x += 300) {
            for(int y = -200; y < 1800; y += 300) {
                addTriangle(
                        new Point(x, y, -1500),
                        new Point(x + 300, y, -1500),
                        new Point(x, y + 300, -1500),
                        new Color(120, 20, 20),
                        wallMaterial);

                addTriangle(
                        new Point(x + 300, y, -1500),
                        new Point(x + 300, y + 300, -1500),
                        new Point(x, y + 300, -1500),
                        new Color(100, 15, 15),
                        wallMaterial);
            }
        }
    }

    /**
     * Sets up all lighting sources for the scene.
     * Creates a sophisticated lighting arrangement with multiple spotlights
     * to achieve dramatic and realistic illumination.
     */
    @Test
    @org.junit.jupiter.api.Order(10)
    void createLighting() {
        // Ambient lighting for overall scene illumination
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));

        // Main spotlight focused on the piano
        addSpotLight(
                new Color(1200, 1100, 900),
                new Point(0, 1500, 0),
                new Vector(0, -1, -0.2),
                0.00005, 0.000002);

        // Keyboard-focused spotlight
        addSpotLight(
                new Color(800, 750, 650),
                new Point(0, 800, -150),
                new Vector(0, -1, -0.1),
                0.0001, 0.000003);

        // Right side stage lighting
        addSpotLight(
                new Color(600, 400, 200),
                new Point(1500, 800, 500),
                new Vector(-1, -0.8, -1),
                0.0001, 0.000005);

        // Left side stage lighting
        addSpotLight(
                new Color(400, 500, 700),
                new Point(-1500, 800, 500),
                new Vector(1, -0.8, -1),
                0.0001, 0.000005);

        // Back wall accent lighting
        addSpotLight(
                new Color(300, 200, 400),
                new Point(0, 500, -1200),
                new Vector(0, 0, -1),
                0.0002, 0.00001);

        // Chandelier accent lighting (right side)
        addSpotLight(
                new Color(800, 600, 200),
                new Point(1200, 100, -800),
                new Vector(0, 0, 1),
                0.0003, 0.00002);

        // Chandelier accent lighting (left side)
        addSpotLight(
                new Color(800, 600, 200),
                new Point(-1200, 100, -800),
                new Vector(0, 0, 1),
                0.0003, 0.00002);
    }

    /**
     * Renders the complete grand piano scene with all components.
     * This is the main test that combines all elements and generates the final image.
     */
    @Test
    @org.junit.jupiter.api.Order(11)
    void renderCompleteScene() {
        // Execute all component creation methods
        createStageFloor();
        createPianoBody();
        createPianoLegs();
        createKeyboardBase();
        createWhiteKeys();
        createBlackKeys();
        createPianoBench();
        createChandelier();
        createWalls();
        createLighting();

        // Add all geometries to the scene
        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        // Configure and position the camera for optimal viewing
        cameraBuilder
                .setLocation(new Point(800, 400, 800))
                .setDirection(new Point(-200, -50, -400), Vector.AXIS_Y)
                .setVpDistance(1000)
                .setVpSize(2500, 2500)
                .setResolution(1000, 1000)
                .build()
                .renderImage()
                .writeToImage("grand_piano_on_stage_complete");
    }

    /**
     * Renders only the chandelier and lighting effects for decoration testing.
     * Useful for testing the ornate lighting fixtures and crystal effects.
     */
    @Test
    void renderChandelierOnly() {
        createChandelier();

        // Add dramatic lighting to showcase the chandelier
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 30)));

        // Multiple colored lights to show crystal refractions
        allLights.add(new SpotLight(new Color(800, 600, 200),
                new Point(0, 1800, -500),
                new Vector(0, -1, 0))
                .setKl(0.0001).setKq(0.000001));

        allLights.add(new SpotLight(new Color(400, 600, 800),
                new Point(300, 1000, -200),
                new Vector(-1, -1, -1))
                .setKl(0.0002).setKq(0.000003));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(400, 1200, -100))
                .setDirection(new Point(0, 1200, -500), Vector.AXIS_Y)
                .setVpDistance(600)
                .setVpSize(800, 800)
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("chandelier_test");
    }

    /**
     * Renders a close-up view of the piano keyboard for detail testing.
     * Focuses specifically on the key arrangement and materials.
     */
    @Test
    void renderKeyboardCloseup() {
        createKeyboardBase();
        createWhiteKeys();
        createBlackKeys();

        // Focused lighting for keyboard detail
        scene.setAmbientLight(new AmbientLight(new Color(40, 40, 40)));
        allLights.add(new SpotLight(new Color(1000, 950, 800),
                new Point(0, 300, 0),
                new Vector(0, -1, -0.5))
                .setKl(0.0001).setKq(0.000001));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(0, 150, 100))
                .setDirection(new Point(0, 30, -200), Vector.AXIS_Y)
                .setVpDistance(200)
                .setVpSize(600, 400)
                .setResolution(800, 600)
                .build()
                .renderImage()
                .writeToImage("keyboard_closeup_test");
    }

    /**
     * Renders the scene with enhanced lighting effects for dramatic testing.
     * Uses multiple colored lights to create a concert hall atmosphere.
     */
    @Test
    void renderDramaticLighting() {
        // Create minimal scene elements
        createStageFloor();
        createPianoBody();
        createKeyboardBase();
        createWhiteKeys();

        // Set dramatic ambient lighting
        scene.setAmbientLight(new AmbientLight(new Color(10, 5, 15)));

        // Create dramatic multi-colored lighting setup
        allLights.add(new SpotLight(new Color(1500, 800, 200),
                new Point(-800, 1200, 800),
                new Vector(1, -1, -1))
                .setKl(0.00005).setKq(0.000001));

        allLights.add(new SpotLight(new Color(200, 800, 1500),
                new Point(800, 1200, 800),
                new Vector(-1, -1, -1))
                .setKl(0.00005).setKq(0.000001));

        allLights.add(new SpotLight(new Color(800, 200, 1200),
                new Point(0, 2000, -1000),
                new Vector(0, -1, 1))
                .setKl(0.0001).setKq(0.000002));

        allLights.add(new SpotLight(new Color(1200, 1200, 400),
                new Point(0, 500, 1000),
                new Vector(0, -0.2, -1))
                .setKl(0.0001).setKq(0.000003));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(600, 300, 600))
                .setDirection(new Point(0, 0, -400), Vector.AXIS_Y)
                .setVpDistance(800)
                .setVpSize(1500, 1500)
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("dramatic_lighting_test");
    }

    /**
     * Renders a performance setup with piano and bench for compositional testing.
     * Shows the complete performance area without decorative elements.
     */
    @Test
    void renderPerformanceSetup() {
        createStageFloor();
        createPianoBody();
        createPianoLegs();
        createKeyboardBase();
        createWhiteKeys();
        createBlackKeys();
        createPianoBench();

        // Performance-appropriate lighting
        scene.setAmbientLight(new AmbientLight(new Color(35, 30, 40)));

        // Main stage lighting
        allLights.add(new SpotLight(new Color(1000, 900, 700),
                new Point(0, 1200, 200),
                new Vector(0, -1, -0.3))
                .setKl(0.00008).setKq(0.000002));

        // Audience perspective lighting
        allLights.add(new SpotLight(new Color(600, 500, 400),
                new Point(0, 800, 800),
                new Vector(0, -0.5, -1))
                .setKl(0.0001).setKq(0.000003));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(0, 200, 1000))
                .setDirection(new Point(0, -20, -300), Vector.AXIS_Y)
                .setVpDistance(800)
                .setVpSize(1600, 1200)
                .setResolution(900, 700)
                .build()
                .renderImage()
                .writeToImage("performance_setup_test");
    }

    /**
     * Renders an overhead view of the scene for layout testing.
     * Useful for verifying object positioning and spatial relationships.
     */
    @Test
    void renderOverheadView() {
        createStageFloor();
        createPianoBody();
        createPianoLegs();
        createKeyboardBase();
        createWhiteKeys();
        createBlackKeys();
        createPianoBench();
        createChandelier();

        // Even overhead lighting
        scene.setAmbientLight(new AmbientLight(new Color(60, 60, 60)));
        allLights.add(new SpotLight(new Color(800, 800, 800),
                new Point(0, 2000, 0),
                new Vector(0, -1, 0))
                .setKl(0.0001).setKq(0.000001));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(0, 1500, -400))
                .setDirection(new Point(0, -400, -400), Vector.AXIS_Z)
                .setVpDistance(1000)
                .setVpSize(2000, 2000)
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("overhead_layout_test");
    }

    /**
     * Renders a minimalist version with just essential elements for performance testing.
     * Uses reduced geometry for faster rendering while maintaining scene essence.
     */
    @Test
    void renderMinimalistVersion() {
        // Create simplified piano body (just main surfaces)
        Point[] basePoints = {
                new Point(-550, PIANO_Y_BASE, -250),
                new Point(550, PIANO_Y_BASE, -250),
                new Point(400, PIANO_Y_BASE, -750),
                new Point(-400, PIANO_Y_BASE, -750)
        };

        Point[] topPoints = new Point[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            topPoints[i] = basePoints[i].add(new Vector(0, PIANO_Y_TOP - PIANO_Y_BASE, 0));
        }

        addPolygon(basePoints, new Color(5, 5, 5), pianoMaterial);
        addPolygon(topPoints, new Color(8, 8, 8), pianoMaterial);

        // Simplified keyboard (fewer keys for performance)
        for (int i = 0; i < 20; i++) {
            int x = -180 + i * WHITE_KEY_SPACING;
            addPolygon(
                    new Point[]{
                            new Point(x, 25, -230),
                            new Point(x + WHITE_KEY_WIDTH, 25, -230),
                            new Point(x + WHITE_KEY_WIDTH, 45, -140),
                            new Point(x, 45, -140)
                    },
                    new Color(255, 255, 255),
                    whiteKeyMaterial);
        }

        // Simple stage
        addPolygon(
                new Point[]{
                        new Point(-1000, -200, -1000),
                        new Point(1000, -200, -1000),
                        new Point(1000, -200, 500),
                        new Point(-1000, -200, 500)
                },
                new Color(139, 90, 43),
                stageMaterial);

        // Basic lighting
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));
        allLights.add(new SpotLight(new Color(800, 700, 600),
                new Point(0, 800, 0),
                new Vector(0, -1, -0.2))
                .setKl(0.0001).setKq(0.000002));

        scene.setGeometries(allGeometries);
        scene.setLights(allLights);

        cameraBuilder
                .setLocation(new Point(400, 200, 400))
                .setDirection(new Point(0, 0, -300), Vector.AXIS_Y)
                .setVpDistance(500)
                .setVpSize(1000, 1000)
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("minimalist_piano_test");
    }
} piano components (body, legs, keyboard) for testing.
     * Useful for focusing on piano details without other scene elements.
        */
@Test
void renderPianoOnly() {
    createPianoBody();
    createPianoLegs();
    createKeyboardBase();
    createWhiteKeys();
    createBlackKeys();

    // Add basic lighting for piano-only scene
    scene.setAmbientLight(new AmbientLight(new Color(50, 50, 50)));
    allLights.add(new SpotLight(new Color(1000, 1000, 1000),
            new Point(0, 1000, 0),
            new Vector(0, -1, -0.1))
            .setKl(0.0001).setKq(0.000001));

    scene.setGeometries(allGeometries);
    scene.setLights(allLights);

    cameraBuilder
            .setLocation(new Point(0, 200, 400))
            .setDirection(new Point(0, 0, -400), Vector.AXIS_Y)
            .setVpDistance(500)
            .setVpSize(1000, 1000)
            .setResolution(800, 800)
            .build()
            .renderImage()
            .writeToImage("piano_only_test");
}

/**
 * Renders only the stage and walls for environment testing.
 * Useful for testing the stage setup without the piano.
 */
@Test
void renderStageOnly() {
    createStageFloor();
    createWalls();

    // Add basic environmental lighting
    scene.setAmbientLight(new AmbientLight(new Color(30, 25, 35)));
    allLights.add(new SpotLight(new Color(800, 600, 400),
            new Point(0, 1000, 0),
            new Vector(0, -1, 0))
            .setKl(0.0001).setKq(0.000002));

    scene.setGeometries(allGeometries);
    scene.setLights(allLights);

    cameraBuilder
            .setLocation(new Point(1000, 500, 0))
            .setDirection(new Point(0, -100, -500), Vector.AXIS_Y)
            .setVpDistance(800)
            .setVpSize(2000, 2000)
            .setResolution(600, 600)
            .build()
            .renderImage()
            .writeToImage("stage_environment_test");
}

/**
 * Renders only the