package renderer;

import primitives.*;
import scene.Scene;

import java.util.stream.IntStream;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ArrayList;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The Camera class represents a virtual camera in a 3D scene.
 * It defines the camera's position, orientation, and view plane properties.
 * It also includes a builder for constructing a camera with specific parameters.
 *
 * <p>This class provides comprehensive functionality for:
 * <ul>
 *   <li>Basic camera positioning and orientation</li>
 *   <li>Anti-aliasing with multiple sampling methods (CENTER, GRID, ADAPTIVE, JITTERED)</li>
 *   <li>Depth of Field (DOF) effects with aperture simulation</li>
 *   <li>Multi-threaded rendering support</li>
 *   <li>Pixel management and progress tracking</li>
 * </ul>
 *
 * <p>The camera uses a builder pattern for construction, ensuring proper validation
 * and immutability of the final camera instance.
 *
 * @author [Your Name]
 * @version 1.0
 * @since 1.0
 */
public class Camera implements Cloneable {
    // ============================
//       Camera Settings
// ============================

    /** Camera position point in 3D space */
    private Point p0 = null;                          // Camera position
    /** Forward direction vector (where camera is looking) */
    private Vector vTo = null;                        // Forward direction vector
    /** Upward direction vector (camera's up direction) */
    private Vector vUp = null;                        // Upward direction vector
    /** Rightward direction vector (camera's right direction) */
    private Vector vRight = null;                     // Rightward direction vector
    /** Distance from camera to the view plane */
    private double distance = 0.0;                    // Distance to view plane
    /** Width of the view plane */
    private double width = 0.0;                       // View plane width
    /** Height of the view plane */
    private double height = 0.0;                      // View plane height
    /** Center point of the view plane */
    private Point pcenter = null;                     // Center point of view plane

// ============================
//       Image & Scene
// ============================

    /** Image writer for rendering output */
    private ImageWriter imageWriter = null;           // Image writer for rendering
    /** Ray tracer for rendering the scene */
    private RayTracerBase rayTracer = null;           // Ray tracer for rendering the scene
    /** Number of pixels in X direction (image width) */
    private int nX = 1;                                // Number of pixels in X direction
    /** Number of pixels in Y direction (image height) */
    private int nY = 1;                                // Number of pixels in Y direction

// ============================
//    Multi-Threading & Debug
// ============================

    /** Number of threads to use for rendering */
    private int threadsCount = 0;                     // Number of threads to use
    /** Number of threads reserved for JVM operations */
    private static final int SPARE_THREADS = 2;       // Threads reserved for JVM
    /** Print progress every X seconds (0 = disabled) */
    private double printInterval = 0;                 // Print progress every X seconds (0 = off)
    /** Pixel manager for multi-threading and debug tracking */
    private PixelManager pixelManager;                // Pixel manager for multi-threading and debug

// ============================
//     Anti-Aliasing (AA)
// ============================

    /** Super-sampling level (NxN rays per pixel) */
    private int superSamplingLevel = 1;               // Super-sampling level (NxN rays per pixel)
    /** Whether to include central ray in AA samples */
    private boolean includeOriginalRayInAA = true;    // Include central ray in AA samples
    /** Maximum recursion depth for adaptive AA */
    private int adaptiveMaxLevel = 3;                 // Max recursion depth for adaptive AA
    /** Color difference threshold for adaptive AA subdivision */
    private double adaptiveColorThreshold = 10.0;     // Color diff threshold for adaptive AA

// ============================
//     Sampling Strategy
// ============================

    /**
     * Enumeration of available sampling methods for anti-aliasing.
     * Each method provides different trade-offs between quality and performance.
     */
    public enum SamplingMethod {                      // Sampling strategies for AA
        /** Single ray at pixel center - fastest, no anti-aliasing */
        CENTER,     // Single ray at pixel center
        /** Uniform grid sampling - good quality, moderate performance */
        GRID,       // Uniform grid sampling
        /** Adaptive supersampling - best quality, variable performance */
        ADAPTIVE,   // Adaptive supersampling
        /** Grid with jittered rays - good for reducing aliasing artifacts */
        JITTERED    // Grid with jittered rays
    }

    /** Current sampling method being used */
    private SamplingMethod samplingMethod =           // Sampling method to use
            SamplingMethod.CENTER;

// ============================
//         Jittering
// ============================

    /** Jitter magnitude (0.0–1.0 of sub-pixel size) */
    private double jitterMagnitude = 0.5;             // Jitter magnitude (0.0–1.0 of sub-pixel size)
    /** Random generator for jitter calculations */
    private java.util.Random jitterRandom =           // Random generator for jitter
            new java.util.Random();
    /** Optional seed for reproducible jitter patterns */
    private Long jitterSeed = null;                   // Optional seed for reproducible jitter

// ============================
//      Depth of Field (DOF)
// ============================

    /** Whether Depth of Field effect is enabled */
    private boolean depthOfFieldEnabled = false;      // Whether DOF is enabled
    /** Distance from camera to focal plane */
    private double focalDistance = 10.0;              // Distance from camera to focal plane
    /** Aperture radius (0 = no blur, pinhole camera) */
    private double apertureRadius = 0.0;              // Aperture radius (0 = no blur)
    /** Number of rays per pixel for DOF sampling */
    private int depthOfFieldSamples = 1;              // Number of rays per pixel for DOF


    /**
     * ================================
     * 1. Constructors & Builder Setup
     * ================================
     * Responsible for constructing and configuring the camera.
     */
    /**
     * Private constructor to prevent direct instantiation.
     * Use the Builder class to create a Camera object.
     */
    private Camera() {
    }

    /**
     * Returns a builder for constructing a Camera object.
     * This is the preferred way to create camera instances.
     *
     * @return A new Builder instance for camera construction
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Builder class to construct a Camera object with specific parameters.
     * This class implements the Builder pattern to ensure proper camera configuration
     * and validation before creating the final immutable Camera instance.
     *
     * <p>Usage example:
     * <pre>{@code
     * Camera camera = Camera.getBuilder()
     *     .setLocation(new Point(0, 0, 0))
     *     .setDirection(Vector.AXIS_Z, Vector.AXIS_Y)
     *     .setVpDistance(10)
     *     .setVpSize(200, 200)
     *     .setRayTracer(rayTracer)
     *     .setResolution(500, 500)
     *     .build();
     * }</pre>
     */
    public static class Builder {
        /** The camera instance being built */
        private final Camera camera = new Camera();
        /** Target point the camera is looking at (alternative to direction vectors) */
        private Point target = null; // Target point the camera is looking at

        // --------------------------------------------------
        // Direction & Position
        // --------------------------------------------------
        /**
         * Sets the direction vectors of the camera.
         * The vectors must be orthogonal to each other.
         *
         * @param vTo The forward direction vector (where camera looks)
         * @param vUp The upward direction vector (camera's up direction)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the direction vectors are not orthogonal
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            // Ensure the direction vectors are orthogonal using the new static method in Vector
            if (!Vector.isOrthogonal(vTo, vUp)) {
                throw new IllegalArgumentException("Direction vectors must be orthogonal");
            }
            this.target = null; // Will be calculated in validation
            // Normalize and set the direction vectors
            camera.vUp = vUp.normalize();
            camera.vTo = vTo.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp);
            return this;
        }

        /**
         * Sets the direction of the camera to look at a specific target point.
         * The up vector will be set to a default value.
         *
         * @param target The point the camera should look at
         * @return The Builder instance for method chaining
         */
        public Builder setDirection(Point target) {
            this.target = target;
            camera.vTo = null; // Will be calculated in validation
            camera.vUp = null; // Will be calculated in validation
            camera.vRight = null; // Will be calculated in validation
            return this;
        }

        /**
         * Sets the direction of the camera to look at a target point with a specific upward direction vector.
         *
         * @param target The point the camera should look at
         * @param vUp    The upward direction vector
         * @return The Builder instance for method chaining
         */
        public Builder setDirection(Point target, Vector vUp) {
            this.target = target;
            camera.vUp = vUp.normalize();
            camera.vTo = null; // Will be calculated in validation
            camera.vRight = null; // Will be calculated in validation
            return this;
        }

        /**
         * Sets the location (position) of the camera in 3D space.
         *
         * @param p0 The position point of the camera
         * @return The Builder instance for method chaining
         */
        public Builder setLocation(Point p0) {
            // Set the camera's position
            camera.p0 = p0;
            return this;
        }

        // --------------------------------------------------
        // View Plane Setup
        // --------------------------------------------------
        /**
         * Sets the distance from the camera to the view plane.
         * This distance determines the field of view along with the view plane size.
         *
         * @param distance The distance to the view plane (must be positive)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the distance is not positive
         */
        public Builder setVpDistance(double distance) {
            // Ensure the distance is positive
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            // Set the distance
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the size of the view plane in world coordinates.
         * This determines the field of view of the camera.
         *
         * @param width  The width of the view plane (must be positive)
         * @param height The height of the view plane (must be positive)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the width or height is not positive
         */
        public Builder setVpSize(double width, double height) {
            // Ensure the width and height are positive
            if (alignZero(width) <= 0 || alignZero(height) <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            // Set the view plane size
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the resolution of the rendered image (number of pixels in x and y directions).
         * This method also initializes the ImageWriter with the scene name.
         *
         * @param nx The number of pixels in the x direction (width)
         * @param ny The number of pixels in the y direction (height)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if nx or ny is not positive
         * @throws IllegalStateException if RayTracer and Scene are not set before calling this method
         */
        public Builder setResolution(int nx, int ny) {
            if (alignZero(nx) <= 0 || alignZero(ny) <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
            camera.nX = nx;
            camera.nY = ny;
            // imageWriter initialization moved here, as it depends on resolution
            // It assumes scene.name is available; if not, pass it as a parameter to setResolution
            if (camera.rayTracer == null || camera.rayTracer.scene == null || camera.rayTracer.scene.name == null) {
                throw new IllegalStateException("RayTracer and Scene must be set before setting resolution for ImageWriter.");
            }
            camera.imageWriter = new ImageWriter(camera.rayTracer.scene.name, nx, ny);
            return this;
        }

        // --------------------------------------------------
        // Ray Tracer Setup
        // --------------------------------------------------
        /**
         * Sets the ray tracer for rendering the scene based on the scene and tracer type.
         * This method is kept for backward compatibility with existing tests.
         * Internally creates a SimpleRayTracer.
         *
         * @param scene      The scene to be rendered (must not be null)
         * @param tracerType The type of ray tracer to use (currently only SIMPLE is supported)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if scene is null or tracerType is not SIMPLE
         */
        public Builder setRayTracer(Scene scene, RayTracerType tracerType) {
            if (scene == null) {
                throw new IllegalArgumentException("Scene cannot be null when setting ray tracer by type.");
            }
            if (tracerType == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                // You can add logic for other tracer types here if you implement them,
                // or throw an exception if unsupported.
                throw new IllegalArgumentException("Unsupported RayTracerType: " + tracerType);
            }
            return this;
        }

        /**
         * Sets the ray tracer for rendering the scene.
         * This method accepts an already instantiated RayTracerBase object,
         * decoupling the Camera.Builder from the concrete implementation details
         * of different ray tracers. This is the preferred method for new code.
         *
         * @param rayTracer The instantiated RayTracerBase object to use (must not be null)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the rayTracer is null
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            if (rayTracer == null) {
                throw new IllegalArgumentException("RayTracer cannot be null");
            }
            camera.rayTracer = rayTracer;
            return this;
        }

        // --------------------------------------------------
        // Multithreading & Debug
        // --------------------------------------------------
        /**
         * Set multi-threading configuration for rendering.
         * <p>Parameter value meaning:
         * <ul>
         * <li>-2 - number of threads is number of logical processors less 2</li>
         * <li>-1 - stream processing parallelization (implicit multi-threading) is used</li>
         * <li>0  - multi-threading is not activated</li>
         * <li>1 and more - literally number of threads</li>
         * </ul>
         *
         * @param threads number of threads to use for rendering
         * @return builder object itself for method chaining
         * @throws IllegalArgumentException if threads parameter is less than -2
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            } else
                camera.threadsCount = threads;
            return this;
        }

        /**
         * Set debug printing interval for rendering progress.
         * If it's zero - there won't be printing at all.
         *
         * @param interval printing interval in seconds (must be non-negative)
         * @return builder object itself for method chaining
         * @throws IllegalArgumentException if interval parameter is negative
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            camera.printInterval = interval;
            return this;
        }

        // --------------------------------------------------
        // Anti-Aliasing Configuration
        // --------------------------------------------------
        /**
         * Sets the super-sampling level for anti-aliasing.
         * A level of 1 means no super-sampling (1 ray per pixel).
         * A level of N means N x N rays per pixel for anti-aliasing.
         *
         * @param level The super-sampling level (must be at least 1)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the level is less than 1
         */
        public Builder setSuperSamplingLevel(int level) {
            if (level < 1) {
                throw new IllegalArgumentException("Super-sampling level must be at least 1");
            }
            camera.superSamplingLevel = level;
            return this;
        }

        /**
         * Sets the sampling method to be used for anti-aliasing.
         *
         * @param method The {@link SamplingMethod} to use (e.g., CENTER, GRID, ADAPTIVE)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if method is null
         */
        public Builder setSamplingMethod(SamplingMethod method) {
            if (method == null) {
                throw new IllegalArgumentException("Sampling method cannot be null");
            }
            camera.samplingMethod = method;
            return this;
        }

        /**
         * Sets whether the original central ray should be included in the anti-aliasing sample set.
         * True means the central ray is part of the average; False means it's excluded.
         *
         * @param include True to include the central ray, false to exclude it
         * @return The Builder instance for method chaining
         */
        public Builder setIncludeOriginalRayInAA(boolean include) {
            camera.includeOriginalRayInAA = include;
            return this;
        }

        /**
         * Sets the maximum recursion level for adaptive supersampling.
         * Higher levels allow more detailed subdivision but increase computation time.
         *
         * @param level The maximum recursion depth (must be at least 1)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the level is less than 1
         */
        public Builder setAdaptiveMaxLevel(int level) {
            if (level < 1) {
                throw new IllegalArgumentException("Adaptive max level must be at least 1");
            }
            camera.adaptiveMaxLevel = level;
            return this;
        }

        /**
         * Sets the color difference threshold for adaptive supersampling subdivision.
         * If the color difference between samples within a sub-pixel region is below
         * this threshold, no further subdivision occurs.
         *
         * @param threshold The color difference threshold (must be non-negative)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the threshold is negative
         */
        public Builder setAdaptiveColorThreshold(double threshold) {
            if (threshold < 0) {
                throw new IllegalArgumentException("Adaptive color threshold cannot be negative");
            }
            camera.adaptiveColorThreshold = threshold;
            return this;
        }

        // --------------------------------------------------
        // Jitter Configuration
        // --------------------------------------------------
        /**
         * Sets the jitter magnitude for jittered sampling.
         * The jitter magnitude determines how much random offset is applied to each sample point.
         * A value of 0.0 means no jitter (equivalent to regular grid sampling).
         * A value of 1.0 means maximum jitter (samples can be offset by up to half the sub-pixel size).
         *
         * @param magnitude The jitter magnitude as a fraction (must be between 0.0 and 1.0)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the magnitude is not between 0.0 and 1.0
         */
        public Builder setJitterMagnitude(double magnitude) {
            if (magnitude < 0.0 || magnitude > 1.0) {
                throw new IllegalArgumentException("Jitter magnitude must be between 0.0 and 1.0");
            }
            camera.jitterMagnitude = magnitude;
            return this;
        }

        /**
         * Sets the random seed for jittered sampling to ensure reproducible results.
         * If no seed is set, the jitter pattern will be different each time.
         *
         * @param seed The random seed to use for jitter generation
         * @return The Builder instance for method chaining
         */
        public Builder setJitterSeed(long seed) {
            camera.jitterSeed = seed;
            camera.jitterRandom = new java.util.Random(seed);
            return this;
        }

        /**
         * Enables jittered sampling with default settings.
         * This is a convenience method that sets the sampling method to JITTERED
         * and configures reasonable default values for jitter parameters.
         *
         * @return The Builder instance for method chaining
         */
        public Builder enableJitteredSampling() {
            camera.samplingMethod = SamplingMethod.JITTERED;
            camera.jitterMagnitude = 0.5; // 50% jitter magnitude
            return this;
        }

        /**
         * Enables jittered sampling with custom jitter magnitude.
         * This is a convenience method that sets the sampling method to JITTERED
         * and sets the specified jitter magnitude.
         *
         * @param magnitude The jitter magnitude as a fraction (must be between 0.0 and 1.0)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if the magnitude is not between 0.0 and 1.0
         */
        public Builder enableJitteredSampling(double magnitude) {
            if (magnitude < 0.0 || magnitude > 1.0) {
                throw new IllegalArgumentException("Jitter magnitude must be between 0.0 and 1.0");
            }
            camera.samplingMethod = SamplingMethod.JITTERED;
            camera.jitterMagnitude = magnitude;
            return this;
        }

        // --------------------------------------------------
        // Depth of Field Configuration
        // --------------------------------------------------
        /**
         * Enables or disables the Depth of Field effect.
         * When enabled, objects at different distances from the focal plane will appear blurred.
         *
         * @param enabled True to enable DOF effect, false to disable
         * @return The Builder instance for method chaining
         */
        public Builder setDepthOfField(boolean enabled) {
            camera.depthOfFieldEnabled = enabled;
            return this;
        }

        /**
         * Sets the focal distance for Depth of Field effect.
         * Objects at this distance from the camera will be in sharp focus.
         * Objects closer or farther will appear increasingly blurred.
         *
         * @param distance The focal distance (must be positive)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if distance is not positive
         */
        public Builder setFocalDistance(double distance) {
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("Focal distance must be positive");
            }
            camera.focalDistance = distance;
            return this;
        }

        /**
         * Sets the aperture radius for Depth of Field effect.
         * Larger radius creates stronger blur effect for out-of-focus objects.
         * A radius of 0 means pinhole camera with no DOF effect.
         *
         * @param radius The aperture radius (0 means pinhole camera, no DOF)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if radius is negative
         */
        public Builder setApertureRadius(double radius) {
            if (radius < 0) {
                throw new IllegalArgumentException("Aperture radius cannot be negative");
            }
            camera.apertureRadius = radius;
            return this;
        }

        /**
         * Sets the number of rays to sample for Depth of Field effect.
         * More samples create smoother blur but increase rendering time.
         * A value of 1 effectively disables DOF sampling.
         *
         * @param samples The number of DOF samples (must be at least 1)
         * @return The Builder instance for method chaining
         * @throws IllegalArgumentException if samples is less than 1
         */
        public Builder setDepthOfFieldSamples(int samples) {
            if (samples < 1) {
                throw new IllegalArgumentException("DOF samples must be at least 1");
            }
            camera.depthOfFieldSamples = samples;
            return this;
        }

        /**
         * Convenience method to enable Depth of Field with all parameters at once.
         * This method sets all DOF parameters and enables the effect in one call.
         *
         * @param focalDistance The distance to the focal plane
         * @param apertureRadius The radius of the aperture
         * @param samples The number of rays to sample
         * @return The Builder instance for method chaining
         */
        public Builder enableDepthOfField(double focalDistance, double apertureRadius, int samples) {
            return setDepthOfField(true)
                    .setFocalDistance(focalDistance)
                    .setApertureRadius(apertureRadius)
                    .setDepthOfFieldSamples(samples);
        }

        // --------------------------------------------------
        // Final Build
        // --------------------------------------------------
        /**
         * Builds and returns the constructed Camera object.
         * This method validates all parameters and ensures immutability by cloning the Camera instance.
         * If validation fails, an error message is printed and null is returned.
         *
         * @return The constructed Camera object, or null if validation fails
         */
        public Camera build() {
            try {
                validate(camera);
                // Clone the camera to ensure immutability
                return (Camera) camera.clone();
            } catch (MissingResourceException | IllegalStateException e) {
                System.err.println("Camera build error: " + e.getMessage());
                return null;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Camera cloning failed", e);
            }
        }

        /**
         * Validates the camera parameters and sets default values if necessary.
         * This method ensures all required parameters are set and calculates derived values.
         *
         * @param camera The Camera object to validate
         * @throws MissingResourceException if required parameters are missing or invalid
         * @throws IllegalStateException if the ImageWriter could not be initialized due to missing scene/tracer
         */
        private void validate(Camera camera) throws MissingResourceException, IllegalStateException {
            // Ensure the view plane size is set
            if (isZero(camera.width) || isZero(camera.height)) {
                throw new MissingResourceException("Missing rendering data", "Camera", "View plane size is not set");
            }

            // Default the camera position to the origin if not set
            if (camera.p0 == null) {
                camera.p0 = Point.ZERO;
            }

            // Ensure the distance to the view plane is positive
            if (isZero(camera.distance)) {
                throw new MissingResourceException("Missing rendering data", "Camera", "Distance to view plane is not set");
            }

            // Ensure the camera is not located at the target point
            if (target != null && target.equals(camera.p0)) {
                throw new MissingResourceException("Missing rendering data", "Camera", "Camera cannot be at the target point");
            }

            // If a target is provided, calculate the forward direction vector (vTo)
            if (target != null) {
                camera.vTo = target.subtract(camera.p0).normalize();

                // If vUp is not set assign a default orthogonal vector
                if (camera.vUp == null) {
                    camera.vUp = Vector.AXIS_Y;
                }
            }

            // Default vTo to the Z-axis if not set
            if (camera.vTo == null) {
                camera.vTo = Vector.AXIS_Z;
            }

            // Default vUp to the Y-axis if not set
            if (camera.vUp == null) {
                camera.vUp = Vector.AXIS_Y;
            }

            // Ensure vTo and vUp are orthogonal by recalculating vUp if necessary
            // This is a robust way to ensure orthogonality if initial vUp is not perfectly orthogonal
            if (!Vector.isOrthogonal(camera.vTo, camera.vUp)) { // Using the new static method
                camera.vUp = camera.vTo.crossProduct(camera.vUp).crossProduct(camera.vTo).normalize();
            }

            // Calculate the rightward direction vector (vRight) as the cross product of vTo and vUp
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            // Calculate the center of the view plane based on the camera position and vTo
            camera.pcenter = camera.p0.add(camera.vTo.scale(camera.distance));

            // RayTracer and ImageWriter validation - only if needed for rendering
            // For basic camera operations like constructRay, these are not required
            if (camera.rayTracer != null) {
                // ImageWriter must be initialized after resolution and rayTracer are set
                if (camera.imageWriter == null && camera.nX > 1 && camera.nY > 1) {
                    // Try to initialize ImageWriter if resolution is set
                    if (camera.rayTracer.scene != null && camera.rayTracer.scene.name != null) {
                        camera.imageWriter = new ImageWriter(camera.rayTracer.scene.name, camera.nX, camera.nY);
                    }
                }
            }

            // Validate DOF parameters only if DOF is enabled
            if (camera.depthOfFieldEnabled) {
                if (camera.focalDistance <= 0) {
                    throw new MissingResourceException("Missing rendering data", "Camera", "Focal distance must be positive when DOF is enabled");
                }
                if (camera.apertureRadius < 0) {
                    throw new MissingResourceException("Missing rendering data", "Camera", "Aperture radius cannot be negative");
                }
                if (camera.depthOfFieldSamples < 1) {
                    throw new MissingResourceException("Missing rendering data", "Camera", "DOF samples must be at least 1");
                }
            }

            // Clear the target to avoid retaining unnecessary state in the builder after validation
            target = null;

            // Initialize jitter random if seed is provided
            if (camera.jitterSeed != null) {
                camera.jitterRandom = new java.util.Random(camera.jitterSeed);
            }
        }    }

    /**
     * ==================================
     * 2. Core Ray Construction
     * ==================================
     * Constructs rays from the camera to a pixel, with or without AA.
     */

    /**
     * Constructs a ray from the camera to a specific point on the view plane.
     * This method always constructs a single ray to the center of the specified pixel.
     * This is useful for debugging or when anti-aliasing is not desired for a specific ray.
     *
     * @param nX The number of pixels along the x-axis (width)
     * @param nY The number of pixels along the y-axis (height)
     * @param j  The pixel index along the x-axis (column)
     * @param i  The pixel index along the y-axis (row)
     * @return A Ray object representing the ray from the camera to the target pixel.
     */
    /**
     * Constructs a single ray from the camera position through a specific pixel in the view plane.
     * This method calculates the exact position of the pixel center and creates a ray from the camera
     * to that point, taking into account the camera's orientation and view plane dimensions.
     *
     * @param nX The total number of pixels along the x-axis (image width).
     * @param nY The total number of pixels along the y-axis (image height).
     * @param j The pixel index along the x-axis (column), starting from 0.
     * @param i The pixel index along the y-axis (row), starting from 0.
     * @return A Ray object representing the ray from the camera position to the pixel center.
     */
    /**
     * Constructs a single ray from the camera position through a specific pixel in the view plane.
     * This method calculates the exact position of the pixel center and creates a ray from the camera
     * to that point, taking into account the camera's orientation and view plane dimensions.
     *
     * @param nX The total number of pixels along the x-axis (image width).
     * @param nY The total number of pixels along the y-axis (image height).
     * @param j The pixel index along the x-axis (column), starting from 0.
     * @param i The pixel index along the y-axis (row), starting from 0.
     * @return A Ray object representing the ray from the camera position to the pixel center.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Calculate the pixel's width and height
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        // Calculate the center of the pixel relative to the center of the view plane
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth; // Horizontal offset
        double yI = (i - (nY - 1) / 2.0) * pixelHeight; // Vertical offset

        // If DOF is enabled, create a DOF ray for this single pixel
        if (depthOfFieldEnabled && apertureRadius > 0) {
            return constructDepthOfFieldRay(xJ, yI);
        }

        // Start from the center of the view plane
        Point pIJ = pcenter;

        // Adjust the point horizontally by scaling the rightward vector
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }

        // Adjust the point vertically by scaling the upward vector (negative for correct orientation)
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(-yI));
        }

        // Return a ray from the camera position to the calculated point on the view plane
        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Private helper method to construct a single ray from the camera
     * to a specific point in the view plane, relative to the view plane center.
     * This is used internally by constructRays for each sub-pixel.
     *
     * @param xOffset The horizontal offset from the view plane center.
     * @param yOffset The vertical offset from the view plane center.
     * @return A Ray object representing the ray from the camera to the target point.
     */
    /**
     * Private helper method to construct a single ray from the camera
     * to a specific point in the view plane, relative to the view plane center.
     * This is used internally by constructRays for each sub-pixel.
     *
     * @param xOffset The horizontal offset from the view plane center.
     * @param yOffset The vertical offset from the view plane center.
     * @return A Ray object representing the ray from the camera to the target point.
     */
    private Ray constructSingleRayInternal(double xOffset, double yOffset) {
        // If DOF is enabled, create a DOF ray
        if (depthOfFieldEnabled && apertureRadius > 0) {
            return constructDepthOfFieldRay(xOffset, yOffset);
        }

        // Start from the center of the view plane
        Point pIJ = pcenter;

        // Adjust the point horizontally by scaling the rightward vector
        if (!isZero(xOffset)) {
            pIJ = pIJ.add(vRight.scale(xOffset));
        }

        // Adjust the point vertically by scaling the upward vector (negative for correct orientation)
        if (!isZero(yOffset)) {
            pIJ = pIJ.add(vUp.scale(-yOffset));
        }

        // Return a ray from the camera position to the calculated point on the view plane
        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Constructs a list of rays for a given pixel, implementing anti-aliasing
     * by generating multiple rays within the pixel area.
     * This version includes an optimized adaptive sampling implementation.
     *
     * @param nX The total number of pixels along the x-axis (image width).
     * @param nY The total number of pixels along the y-axis (image height).
     * @param j The pixel index along the x-axis (column).
     * @param i The pixel index along the y-axis (row).
     * @return A List of Ray objects to be traced for anti-aliasing.
     */
    public List<Ray> constructRays(int nX, int nY, int j, int i) {
        List<Ray> rays = new LinkedList<>();

        // Calculate the pixel's dimensions
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        // Calculate the center of the current pixel relative to the view plane center
        double xJ_pixelCenter = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI_pixelCenter = (i - (nY - 1) / 2.0) * pixelHeight;

        // Define the pixel boundaries in view plane coordinates relative to pcenter
        double xMinPixel = xJ_pixelCenter - pixelWidth / 2.0;
        double xMaxPixel = xJ_pixelCenter + pixelWidth / 2.0;
        double yMinPixel = yI_pixelCenter - pixelHeight / 2.0;
        double yMaxPixel = yI_pixelCenter + pixelHeight / 2.0;

        // Generate rays based on sampling method
        switch (samplingMethod) {
            case CENTER:
                rays.add(constructSingleRayInternal(xJ_pixelCenter, yI_pixelCenter));
                break;
            case GRID:
                // Calculate sub-pixel dimensions
                double subPixelWidth = pixelWidth / superSamplingLevel;
                double subPixelHeight = pixelHeight / superSamplingLevel;

                // Generate grid of rays
                for (int row = 0; row < superSamplingLevel; row++) {
                    for (int col = 0; col < superSamplingLevel; col++) {
                        // Calculate offset within the pixel (relative to pixel's top-left corner)
                        // Adjusting to center of sub-pixel
                        double xOffset = xMinPixel + (col + 0.5) * subPixelWidth;
                        double yOffset = yMinPixel + (row + 0.5) * subPixelHeight;

                        rays.add(constructSingleRayInternal(xOffset, yOffset));
                    }
                }
                break;
            case ADAPTIVE:
                // Start adaptive sampling with initial corner samples
                adaptiveSampling(xMinPixel, xMaxPixel, yMinPixel, yMaxPixel, 0, rays);
                break;
            case JITTERED:
                // Calculate sub-pixel dimensions
                double jitterSubPixelWidth = pixelWidth / superSamplingLevel;
                double jitterSubPixelHeight = pixelHeight / superSamplingLevel;

                // Generate jittered grid of rays
                for (int row = 0; row < superSamplingLevel; row++) {
                    for (int col = 0; col < superSamplingLevel; col++) {
                        // Calculate base position at center of sub-pixel
                        double baseX = xMinPixel + (col + 0.5) * jitterSubPixelWidth;
                        double baseY = yMinPixel + (row + 0.5) * jitterSubPixelHeight;

                        // Calculate maximum jitter distance (fraction of sub-pixel size)
                        double maxJitterX = jitterSubPixelWidth * jitterMagnitude * 0.5;
                        double maxJitterY = jitterSubPixelHeight * jitterMagnitude * 0.5;

                        // Generate random jitter within the allowed range
                        double jitterX = (jitterRandom.nextDouble() - 0.5) * 2 * maxJitterX;
                        double jitterY = (jitterRandom.nextDouble() - 0.5) * 2 * maxJitterY;

                        // Apply jitter to base position
                        double finalX = baseX + jitterX;
                        double finalY = baseY + jitterY;

                        // Clamp to ensure the jittered point stays within the original pixel bounds
                        finalX = Math.max(xMinPixel, Math.min(xMaxPixel, finalX));
                        finalY = Math.max(yMinPixel, Math.min(yMaxPixel, finalY));

                        rays.add(constructSingleRayInternal(finalX, finalY));
                    }
                }
                break;
        }

        return rays;
    }

/**
 * ==================================
 * 3. Depth of Field Support
 * ==================================
 * Constructs a set of rays to simulate camera focus and blur
 * based on aperture and focal plane distance.
 */

    /**
     * Constructs a single Depth of Field ray from a random point on the aperture
     * through the focal point corresponding to the given pixel offset.
     *
     * @param xOffset The horizontal offset of the pixel from view plane center.
     * @param yOffset The vertical offset of the pixel from view plane center.
     * @return A single DOF ray from aperture to focal point.
     */
    private Ray constructDepthOfFieldRay(double xOffset, double yOffset) {
        // Calculate the focal point for this pixel position
        Point focalPoint = calculateFocalPoint(xOffset, yOffset);

        // Generate a random point on the aperture
        Point aperturePoint = generateAperturePoint();

        // Create ray from aperture point through focal point
        Vector direction = focalPoint.subtract(aperturePoint);
        return new Ray(aperturePoint, direction);
    }

    /**
     * Calculates the focal point where rays should converge for sharp focus.
     * The focal point is calculated by projecting the view plane point onto the focal plane.
     *
     * @param xOffset The horizontal offset from view plane center.
     * @param yOffset The vertical offset from view plane center.
     * @return The focal point on the focal plane.
     */
    private Point calculateFocalPoint(double xOffset, double yOffset) {
        // Calculate point on view plane
        Point viewPlanePoint = pcenter;

        if (!isZero(xOffset)) {
            viewPlanePoint = viewPlanePoint.add(vRight.scale(xOffset));
        }
        if (!isZero(yOffset)) {
            viewPlanePoint = viewPlanePoint.add(vUp.scale(-yOffset));
        }

        // Calculate direction from camera center to view plane point
        Vector rayDirection = viewPlanePoint.subtract(p0).normalize();

        // Find intersection of this ray with the focal plane
        // Focal plane is at distance 'focalDistance' from camera along vTo direction
        Point focalPlaneCenter = p0.add(vTo.scale(focalDistance));

        // Calculate intersection parameter t
        // Ray equation: p0 + t * rayDirection
        // Plane equation: dot(point - focalPlaneCenter, vTo) = 0
        // Solving: dot(p0 + t * rayDirection - focalPlaneCenter, vTo) = 0
        Vector toPlaneCenter = focalPlaneCenter.subtract(p0);
        double numerator = toPlaneCenter.dotProduct(vTo);
        double denominator = rayDirection.dotProduct(vTo);

        // Avoid division by zero (ray parallel to focal plane)
        if (isZero(denominator)) {
            // Ray is parallel to focal plane, return a point on the focal plane
            return focalPlaneCenter.add(vRight.scale(xOffset)).add(vUp.scale(-yOffset));
        }

        double t = numerator / denominator;
        return p0.add(rayDirection.scale(t));
    }

    /**
     * Generates a random point on the circular aperture window.
     * The aperture is centered at the camera position with radius apertureRadius.
     * Uses uniform distribution within the circle.
     *
     * @return A random Point on the aperture disk. Returns camera position if aperture radius is 0.
     */
    private Point generateAperturePoint() {
        if (apertureRadius <= 0) {
            return p0;
        }

        // Generate uniform random point in unit circle using polar coordinates
        double angle = 2.0 * Math.PI * jitterRandom.nextDouble();
        double radius = apertureRadius * Math.sqrt(jitterRandom.nextDouble());

        // Convert to Cartesian coordinates in camera's right-up plane
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);

        // Position relative to camera using right and up vectors
        Vector apertureOffset = vRight.scale(x).add(vUp.scale(y));
        return p0.add(apertureOffset);
    }

/**
 * ==================================
 * 4. Sampling & Anti-Aliasing
 * ==================================
 * Adaptive super sampling to improve edge smoothness and image quality.
 */
    /**
     * Optimized adaptive sampling that uses corner samples to determine if subdivision is needed.
     * This method significantly reduces the number of rays traced by sampling strategically.
     * Uses recursive subdivision based on color variance between corner samples.
     *
     * @param xMin Minimum x-coordinate of the current sub-pixel area.
     * @param xMax Maximum x-coordinate of the current sub-pixel area.
     * @param yMin Minimum y-coordinate of the current sub-pixel area.
     * @param yMax Maximum y-coordinate of the current sub-pixel area.
     * @param level The current recursion level.
     * @param rays The list to accumulate final rays.
     */
    private void adaptiveSampling(double xMin, double xMax, double yMin, double yMax, int level, List<Ray> rays) {
        // Base case: Maximum recursion level reached
        if (level >= adaptiveMaxLevel) {
            // Add the center ray of this sub-pixel area
            rays.add(constructSingleRayInternal((xMin + xMax) / 2.0, (yMin + yMax) / 2.0));
            return;
        }

        // Sample the four corners of the current area
        Ray topLeft = constructSingleRayInternal(xMin, yMin);
        Ray topRight = constructSingleRayInternal(xMax, yMin);
        Ray bottomLeft = constructSingleRayInternal(xMin, yMax);
        Ray bottomRight = constructSingleRayInternal(xMax, yMax);

        // Trace rays to get colors
        Color colorTL = rayTracer.traceRay(topLeft);
        Color colorTR = rayTracer.traceRay(topRight);
        Color colorBL = rayTracer.traceRay(bottomLeft);
        Color colorBR = rayTracer.traceRay(bottomRight);

        // Handle null colors (treat as black)
        if (colorTL == null) colorTL = Color.BLACK;
        if (colorTR == null) colorTR = Color.BLACK;
        if (colorBL == null) colorBL = Color.BLACK;
        if (colorBR == null) colorBR = Color.BLACK;

        // Calculate maximum color difference between corners
        double maxDifference = 0.0;
        maxDifference = Math.max(maxDifference, colorTL.perceptualDistance(colorTR));
        maxDifference = Math.max(maxDifference, colorTL.perceptualDistance(colorBL));
        maxDifference = Math.max(maxDifference, colorTL.perceptualDistance(colorBR));
        maxDifference = Math.max(maxDifference, colorTR.perceptualDistance(colorBL));
        maxDifference = Math.max(maxDifference, colorTR.perceptualDistance(colorBR));
        maxDifference = Math.max(maxDifference, colorBL.perceptualDistance(colorBR));

        // If color variation is below threshold, no need to subdivide
        if (maxDifference <= adaptiveColorThreshold) {
            // Add only the center ray to represent this area
            rays.add(constructSingleRayInternal((xMin + xMax) / 2.0, (yMin + yMax) / 2.0));
            return;
        }

        // Subdivide into four quadrants
        double midX = (xMin + xMax) / 2.0;
        double midY = (yMin + yMax) / 2.0;

        // Recursively sample each quadrant
        adaptiveSampling(xMin, midX, yMin, midY, level + 1, rays);     // Top-left
        adaptiveSampling(midX, xMax, yMin, midY, level + 1, rays);     // Top-right
        adaptiveSampling(xMin, midX, midY, yMax, level + 1, rays);     // Bottom-left
        adaptiveSampling(midX, xMax, midY, yMax, level + 1, rays);     // Bottom-right
    }

/**
 * ==================================
 * 5. Rendering Logic
 * ==================================
 * Main rendering methods for creating the final image.
 * Supports single-threaded, multi-threaded, and stream-based approaches.
 */
    /**
     * Renders the complete image by tracing rays through each pixel in the view plane.
     * This function creates a pixel color map from the scene included in the ray tracer object.
     * Supports multiple threading modes: single-threaded, multi-threaded, and stream-based parallel processing.
     *
     * @return The camera object itself for method chaining.
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Casts multiple rays for a given pixel, averages their colors, and sets the pixel.
     * This method incorporates the anti-aliasing and DOF logic by constructing multiple rays per pixel
     * and averaging their traced colors to produce the final pixel color.
     *
     * @param j The pixel column index (x-coordinate).
     * @param i The pixel row index (y-coordinate).
     */
    private void castRay(int j, int i) {
        List<Ray> rays;

        // Handle DOF with multiple samples per pixel
        if (depthOfFieldEnabled && depthOfFieldSamples > 1 && apertureRadius > 0) {
            rays = new ArrayList<>();

            // Calculate pixel center offset
            double pixelWidth = width / nX;
            double pixelHeight = height / nY;
            double xJ_pixelCenter = (j - (nX - 1) / 2.0) * pixelWidth;
            double yI_pixelCenter = (i - (nY - 1) / 2.0) * pixelHeight;

            // Generate multiple DOF rays for the same pixel position
            for (int sample = 0; sample < depthOfFieldSamples; sample++) {
                rays.add(constructDepthOfFieldRay(xJ_pixelCenter, yI_pixelCenter));
            }
        } else {
            // Use standard ray construction (includes AA if enabled)
            rays = constructRays(nX, nY, j, i);
        }

        Color finalColor = Color.BLACK; // Initialize accumulated color to black

        // Trace each ray and sum their colors
        for (Ray ray : rays) {
            Color color = rayTracer.traceRay(ray);
            if (color == null) {
                // If tracing a ray yields no color (e.g., missed all objects), treat as black
                color = Color.BLACK;
            }
            finalColor = finalColor.add(color);
        }

        // Average the accumulated color by the number of rays traced for this pixel
        finalColor = finalColor.reduce(rays.size());

        // Write the averaged color to the image writer
        imageWriter.writePixel(j, i, finalColor);
        pixelManager.pixelDone();
    }

    /**
     * Renders the image using multi-threading by parallel streaming.
     * This method uses Java 8 parallel streams to distribute pixel processing across multiple threads.
     * Each pixel is processed by calling the castRaysAndAverage method.
     *
     * @return The camera object itself for method chaining.
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders the image without multi-threading using a simple sequential approach.
     * This method processes each pixel one by one in a nested loop structure.
     * Each pixel is processed by calling the castRaysAndAverage method.
     *
     * @return The camera object itself for method chaining.
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRay(j, i);
        return this;
    }

    /**
     * Renders the image using multi-threading by creating and managing raw threads.
     * This method creates a specified number of worker threads that process pixels concurrently
     * using a shared pixel manager for work distribution.
     * Each pixel is processed by calling the castRaysAndAverage method.
     *
     * @return The camera object itself for method chaining.
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        // Ensure threadsCount is not negative due to decrement in loop condition
        int currentThreads = threadsCount; // Store initial value
        while (currentThreads-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {
        }
        return this;
    }

/**
 * ==================================
 * 6. Output & Visualization
 * ==================================
 * Utilities to export and visualize rendering results.
 */
    /**
     * Prints a grid pattern on the rendered image for visual reference and debugging purposes.
     * This method overlays a grid of lines at specified intervals across the entire image.
     * Grid lines are drawn at pixel positions that are multiples of the interval parameter.
     *
     * @param interval The spacing between grid lines in pixels. Must be positive.
     * @param color The color of the grid lines to be drawn.
     * @return The Camera object itself for method chaining.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the rendered image to a file with the specified filename.
     * This method saves the complete rendered image that has been generated through the rendering process.
     * The actual file format and location depend on the imageWriter implementation.
     *
     * @param fileName The name of the file to save the image to. Should include appropriate file extension.
     * @return The Camera object itself for method chaining.
     */
    public Camera writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
        return this;
    }
}