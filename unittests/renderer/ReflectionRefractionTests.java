package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 * @author Dan Zilberstein
 */
class ReflectionRefractionTests {
    /** Default constructor to satisfy JavaDoc generator */
    ReflectionRefractionTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene          scene         = new Scene("Test scene");
    /** Camera builder for the tests with triangles */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    void twoSpheres() {
        scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50d).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setkT(0.3)), //
                new Sphere(new Point(0, 0, -50), 25d).setEmission(new Color(RED)) //
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
        scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004).setKq(0.0000006));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(150, 150) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("refractionTwoSpheres");
    }

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    void twoSpheresOnMirrors() {
        scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400d).setEmission(new Color(0, 50, 100)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
                                .setkT(new Double3(0.5, 0, 0))), //
                new Sphere(new Point(-950, -900, -1000), 200d).setEmission(new Color(100, 50, 20)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setkR(1)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setkR(new Double3(0.5, 0, 0.4))));
        scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
        scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001).setKq(0.000005));

        cameraBuilder
                .setLocation(new Point(0, 0, 10000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(10000).setVpSize(2500, 2500) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("reflectionTwoSpheresMirrored");
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    void trianglesTransparentSphere() {
        scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Sphere(new Point(60, 50, -50), 30d).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setkT(0.6)));
        scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(200, 200) //
                .setResolution(600, 600) //
                .build() //
                .renderImage() //
                .writeToImage("refractionShadow");
    }

    @Test
    void grandPianoOnStageScene() {

        //Building the materials for the grand piano scene
        Material pianoMaterial = new Material()
                .setKD(0.4).setKS(0.6).setShininess(100);

        //Material for white keys - reduced reflection
        Material whiteKeyMaterial = new Material()
                .setKD(0.6).setKS(0.4).setShininess(80)
                .setkR(new Double3(0.1, 0.1, 0.1));

        //Material for black keys - reduced reflection
        Material blackKeyMaterial = new Material()
                .setKD(0.3).setKS(0.7).setShininess(120)
                .setkR(new Double3(0.05, 0.05, 0.05));

        //Material for the stage - glossy brown wood
        Material stageMaterial = new Material()
                .setKD(0.4).setKS(0.6).setShininess(80)
                .setkR(new Double3(0.3, 0.2, 0.1));

        //Material for walls - red velvet
        Material wallMaterial = new Material()
                .setKD(0.8).setKS(0.2).setShininess(10)
                .setkR(new Double3(0.05, 0.05, 0.05));

        //Material for colored glass - used in the chandelier
        Material coloredGlassMaterial = new Material()
                .setKD(0.2).setKS(0.8).setShininess(250)
                .setkT(new Double3(0.7, 0.8, 0.9))
                .setkR(new Double3(0.2, 0.2, 0.2));

        // Used for decorative elements like chandelier crystals
        Material crystalMaterial = new Material()
                .setKD(0.05).setKS(0.95).setShininess(400)
                .setkT(new Double3(0.95, 0.95, 0.98))
                .setkR(new Double3(0.3, 0.3, 0.3));

        //Material for shiny metal - used for the chandelier frame
        Material metalMaterial = new Material()
                .setKD(0.2).setKS(0.8).setShininess(150)
                .setkR(new Double3(0.6, 0.6, 0.6));

        // Used for the legs of the piano, giving a matte black finish
        Material legMaterial = new Material()
                .setKD(0.9).setKS(0.1).setShininess(10)
                .setkR(new Double3(0.0, 0.0, 0.0));

        //Material for the black keyboard base - matte black
        Material keyboardBaseMaterial = new Material()
                .setKD(0.6).setKS(0.4).setShininess(80)
                .setkR(new Double3(0.1, 0.1, 0.1));


        //building the stage with a wooden floor and velvet walls

        //setting up the stage with wooden floor and velvet walls
        for(int x = -2000; x < 2000; x += 200) {
            for(int z = -1500; z < 1500; z += 200) {
                //Triangle representing the stage floor
                scene.geometries.add(
                        new Triangle(
                                new Point(x, -200, z),
                                new Point(x + 200, -200, z),
                                new Point(x, -200, z + 200))
                                .setEmission(new Color(139, 90, 43))
                                .setMaterial(stageMaterial));
                //Base under the piano, creating a connection to the ground
                scene.geometries.add(
                        new Polygon(
                                new Point(-650, -201, -850),
                                new Point(650, -201, -850),
                                new Point(650, -201, -150),
                                new Point(-650, -201, -150))
                                .setEmission(new Color(30, 20, 10))
                                .setMaterial(stageMaterial));

                // Triangle representing the stage floor
                scene.geometries.add(
                        new Triangle(
                                new Point(x + 200, -200, z),
                                new Point(x + 200, -200, z + 200),
                                new Point(x, -200, z + 200))
                                .setEmission(new Color(160, 105, 50))
                                .setMaterial(stageMaterial));
            }
        }
        //Building the walls of the stage with red velvet texture

        double pianoYBase = -50;
        double pianoYTop = 50;

        //Defining the grand piano shape
        Point p1 = new Point(-550, pianoYBase, -250);
        Point p2 = new Point(550, pianoYBase, -250);
        Point p3 = new Point(550, pianoYBase, -450);
        Point p4 = new Point(450, pianoYBase, -750);
        Point p5 = new Point(200, pianoYBase, -850);
        Point p6 = new Point(-250, pianoYBase, -800);
        Point p7 = new Point(-550, pianoYBase, -600);

        Point[] basePoints = {p1, p2, p3, p4, p5, p6, p7};

        Point[] topPoints = new Point[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            topPoints[i] = basePoints[i].add(new Vector(0, pianoYTop - pianoYBase, 0));
        }

        // Adding the base of the grand piano with a glossy finish
        scene.geometries.add(
                new Polygon(basePoints)
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(pianoMaterial));

        // Adding the top surface of the grand piano
        scene.geometries.add(
                new Polygon(topPoints)
                        .setEmission(new Color(8, 8, 8))
                        .setMaterial(pianoMaterial));

        // Adding the sides of the grand piano with a glossy finish
        for (int i = 0; i < basePoints.length; i++) {
            scene.geometries.add(
                    new Polygon(
                            basePoints[i],
                            basePoints[(i + 1) % basePoints.length],
                            topPoints[(i + 1) % topPoints.length],
                            topPoints[i])
                            .setEmission(new Color(6, 6, 6))
                            .setMaterial(pianoMaterial));
        }

        //PIano legs

        for(int i = 0; i < 15; i++) {
            scene.geometries.add(
                    new Sphere(new Point(450, -200 + i * 10, -300), 35)
                            .setEmission(new Color(10, 10, 10))
                            .setMaterial(legMaterial));
        }

        for(int i = 0; i < 15; i++) {
            scene.geometries.add(
                    new Sphere(new Point(-450, -200 + i * 10, -300), 35)
                            .setEmission(new Color(10, 10, 10))
                            .setMaterial(legMaterial));
        }

        for(int i = 0; i < 15; i++) {
            scene.geometries.add(
                    new Sphere(new Point(100, -200 + i * 10, -780), 40)
                            .setEmission(new Color(12, 12, 12))
                            .setMaterial(legMaterial));
        }

        // Adding the grand piano lid

        //Adding a metallic rod to support the lid
        for(int i = 0; i < 20; i++) {
            scene.geometries.add(
                    new Sphere(new Point(0, 1400 + i * 10, -500), 8)
                            .setEmission(new Color(200, 180, 120))
                            .setMaterial(metalMaterial));
        }

        // Creating a chandelier with colored glass crystals
        for(int ring = 0; ring < 3; ring++) {
            int numCrystals = 6 + ring * 2;
            double radius = 150 + ring * 80;
            double height = 1300 - ring * 100;

            for(int i = 0; i < numCrystals; i++) {
                double angle = i * 2 * Math.PI / numCrystals;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle) - 500;

                // Adding a colored glass sphere
                scene.geometries.add(
                        new Sphere(new Point(x, height, z), 25)
                                .setEmission(new Color(150, 200, 255))
                                .setMaterial(coloredGlassMaterial));

                // Adding multiple crystal spheres at different heights
                for(int j = 0; j < 3; j++) {
                    scene.geometries.add(
                            new Sphere(new Point(x, height - 40 - j * 30, z), 8)
                                    .setEmission(new Color(255, 255, 255))
                                    .setMaterial(crystalMaterial));
                }
            }
        }

        // Adding the black keyboard base
        scene.geometries.add(
                new Polygon(
                        new Point(-530, 20, -240),
                        new Point(530, 20, -240),
                        new Point(530, 25, -140),
                        new Point(-530, 25, -140))
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(keyboardBaseMaterial));

        // Adding the sides of the black keyboard base
        scene.geometries.add(
                new Polygon(
                        new Point(-530, 20, -240),
                        new Point(-530, 25, -140),
                        new Point(-530, 15, -140),
                        new Point(-530, 10, -240))
                        .setEmission(new Color(8, 8, 8))
                        .setMaterial(keyboardBaseMaterial));

        scene.geometries.add(
                new Polygon(
                        new Point(530, 20, -240),
                        new Point(530, 10, -240),
                        new Point(530, 15, -140),
                        new Point(530, 25, -140))
                        .setEmission(new Color(8, 8, 8))
                        .setMaterial(keyboardBaseMaterial));

        //This is the front surface of the black keyboard base
        scene.geometries.add(
                new Polygon(
                        new Point(-530, 25, -140),
                        new Point(530, 25, -140),
                        new Point(530, 15, -140),
                        new Point(-530, 15, -140))
                        .setEmission(new Color(10, 10, 10))
                        .setMaterial(keyboardBaseMaterial));

        // This is the black strip under the keys
        scene.geometries.add(
                new Polygon(
                        new Point(-510, 15, -235),
                        new Point(510, 15, -235),
                        new Point(510, 25, -230),
                        new Point(-510, 25, -230))
                        .setEmission(new Color(3, 3, 3))
                        .setMaterial(pianoMaterial));

        // This is the black strip that sits on the keys
        scene.geometries.add(
                new Polygon(
                        new Point(-510, 15, -230),
                        new Point(510, 15, -230),
                        new Point(510, 25, -180),
                        new Point(-510, 25, -180))
                        .setEmission(new Color(4, 4, 4))
                        .setMaterial(pianoMaterial));

        // Adding white keys to the grand piano
        for (int i = 0; i < 52; i++) {
            int x = -450 + i * 18;

            scene.geometries.add(
                    new Polygon(
                            new Point(x, 25, -230),
                            new Point(x + 16, 25, -230),
                            new Point(x + 16, 35, -160),
                            new Point(x, 35, -160))
                            .setEmission(new Color(255, 255, 255))
                            .setMaterial(whiteKeyMaterial));

            // This is the top part of the white key
            scene.geometries.add(
                    new Polygon(
                            new Point(x, 35, -160),
                            new Point(x + 16, 35, -160),
                            new Point(x + 16, 45, -140),
                            new Point(x, 45, -140))
                            .setEmission(new Color(248, 248, 248))
                            .setMaterial(whiteKeyMaterial));

            // This is the front edge of the white key
            scene.geometries.add(
                    new Polygon(
                            new Point(x, 45, -140),
                            new Point(x + 16, 45, -140),
                            new Point(x + 16, 50, -140),
                            new Point(x, 50, -140))
                            .setEmission(new Color(250, 250, 250))
                            .setMaterial(whiteKeyMaterial));
        }

        //Adding black keys to the grand piano
        int[] blackKeyIndices = {0, 1, 3, 4, 5};
        for (int octave = 0; octave < 7; octave++) {
            for (int idx : blackKeyIndices) {
                int i = octave * 7 + idx;
                int x = -441 + i * 18;

                scene.geometries.add(
                        new Polygon(
                                new Point(x, 35, -230),
                                new Point(x + 10, 35, -230),
                                new Point(x + 10, 50, -180),
                                new Point(x, 50, -180))
                                .setEmission(new Color(15, 15, 15))
                                .setMaterial(blackKeyMaterial));
            }
        }



        // This is the back wall of the stage, made of red velvet
        for(int x = -2000; x < 2000; x += 300) {
            for(int y = -200; y < 1800; y += 300) {
                scene.geometries.add(
                        new Triangle(
                                new Point(x, y, -1500),
                                new Point(x + 300, y, -1500),
                                new Point(x, y + 300, -1500))
                                .setEmission(new Color(120, 20, 20))
                                .setMaterial(wallMaterial));
                scene.geometries.add(
                        new Triangle(
                                new Point(x + 300, y, -1500),
                                new Point(x + 300, y + 300, -1500),
                                new Point(x, y + 300, -1500))
                                .setEmission(new Color(100, 15, 15))
                                .setMaterial(wallMaterial));
            }
        }

        //Adding a bench for the piano with a padded top and wooden sides
        scene.geometries.add(
                new Polygon(
                        new Point(-120, -30, 200),
                        new Point(120, -30, 200),
                        new Point(120, -20, 320),
                        new Point(-120, -20, 320))
                        .setEmission(new Color(139, 69, 19))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        // Adding the sides of the bench surface
        scene.geometries.add(
                new Polygon(
                        new Point(-120, -30, 200),
                        new Point(-120, -20, 320),
                        new Point(-120, -50, 320),
                        new Point(-120, -60, 200))
                        .setEmission(new Color(120, 60, 16))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        scene.geometries.add(
                new Polygon(
                        new Point(120, -30, 200),
                        new Point(120, -60, 200),
                        new Point(120, -50, 320),
                        new Point(120, -20, 320))
                        .setEmission(new Color(120, 60, 16))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        // Adding the front surface of the bench
        scene.geometries.add(
                new Polygon(
                        new Point(-120, -60, 200),
                        new Point(120, -60, 200),
                        new Point(120, -30, 200),
                        new Point(-120, -30, 200))
                        .setEmission(new Color(110, 55, 14))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(50)));

        // Adding the legs of the bench
        for(int i = 0; i < 4; i++) {
            int x = (i % 2 == 0) ? -90 : 90;
            int z = (i < 2) ? 230 : 290;

            // Adding legs of the bench
            for(int j = 0; j < 6; j++) {
                scene.geometries.add(
                        new Sphere(new Point(x, -200 + j * 25, z), 15)
                                .setEmission(new Color(101, 67, 33))
                                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(30)));
            }
        }

        //Adding lighting to enhance the piano keys and stage

        // This sets a soft ambient light to the scene
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));

        // This is the main spotlight focused on the grand piano
        scene.lights.add(
                new SpotLight(new Color(1200, 1100, 900), new Point(0, 1500, 0), new Vector(0, -1, -0.2))
                        .setKl(0.00005).setKq(0.000002));

        // This spotlight focuses on the piano keys
        scene.lights.add(
                new SpotLight(new Color(800, 750, 650), new Point(0, 800, -150), new Vector(0, -1, -0.1))
                        .setKl(0.0001).setKq(0.000003));

        //This spotlight illuminates the right side of the stage
        scene.lights.add(
                new SpotLight(new Color(600, 400, 200), new Point(1500, 800, 500), new Vector(-1, -0.8, -1))
                        .setKl(0.0001).setKq(0.000005));

        // This spotlight illuminates the left side of the stage
        scene.lights.add(
                new SpotLight(new Color(400, 500, 700), new Point(-1500, 800, 500), new Vector(1, -0.8, -1))
                        .setKl(0.0001).setKq(0.000005));

        // This spotlight highlights the back wall of the stage
        scene.lights.add(
                new SpotLight(new Color(300, 200, 400), new Point(0, 500, -1200), new Vector(0, 0, -1))
                        .setKl(0.0002).setKq(0.00001));

        // This spotlight illuminates the chandelier from above
        scene.lights.add(
                new SpotLight(new Color(800, 600, 200), new Point(1200, 100, -800), new Vector(0, 0, 1))
                        .setKl(0.0003).setKq(0.00002));

        // This spotlight illuminates the chandelier from the left side
        scene.lights.add(
                new SpotLight(new Color(800, 600, 200), new Point(-1200, 100, -800), new Vector(0, 0, 1))
                        .setKl(0.0003).setKq(0.00002));

//        // This spotlight highlights the decorative glass elements
//        scene.lights.add(
//                new SpotLight(new Color(400, 450, 500), new Point(-800, 600, 600), new Vector(-1, -1, -1))
//                        .setKl(0.0002).setKq(0.000008));

        // Setting up the camera for the grand piano scene
        cameraBuilder
                .setLocation(new Point(800, 400, 800))
                .setDirection(new Point(-200, -50, -400), Vector.AXIS_Y)
                .setVpDistance(1000)
                .setVpSize(2500, 2500)
                .setResolution(1000, 1000)
                .build()
                .renderImage()
                .writeToImage("grand_piano_on_stage");
    }
    }