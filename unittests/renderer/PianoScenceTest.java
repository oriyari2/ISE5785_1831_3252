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
public class PianoScenceTest {
    PianoScenceTest(){};
    /** Scene for the tests */
    private final Scene          scene         = new Scene("Test scene");
    /** Camera builder for the tests with triangles */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(scene, RayTracerType.SIMPLE);



}
