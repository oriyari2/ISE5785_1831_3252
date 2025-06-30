package renderer;

import primitives.*;
import scene.Scene;

import java.util.stream.IntStream;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ArrayList; // Added for convenience with lists

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The Camera class represents a virtual camera in a 3D scene.
 * It defines the camera's position, orientation, and view plane properties.
 * It also includes a builder for constructing a camera with specific parameters.
 */
public class Camera implements Cloneable {
    private Vector vTo = null; // The camera's forward direction vector
    private Vector vUp = null; // The camera's upward direction vector
    private Vector vRight = null; // The camera's rightward direction vector
    private double distance = 0.0; // The distance from the camera to the view plane
    private double width = 0.0; // The width of the view plane
    private double height = 0.0; // The height of the view plane
    private Point p0 = null; // The position of the camera
    private Point pcenter = null; // The center point of the view plane
    private ImageWriter imageWriter = null; // The image writer for rendering the scene
    private RayTracerBase rayTracer = null; // The ray tracer for rendering the scene
    private int nX = 1; // The number of pixels in the x direction
    private int nY = 1; // The number of pixels in the y direction

    /** Amount of threads to use fore rendering image by the camera */
    private int threadsCount = 0;
    /**
     * Amount of threads to spare for Java VM threads:<br>
     * Spare threads if trying to use all the cores
     */
    private static final int SPARE_THREADS = 2;
    /**
     * Debug print interval in seconds (for progress percentage) <br>
     * if it is zero - there is no progress output
     */
    private double printInterval = 0;
    /**
     * Pixel manager for supporting:
     * <ul>
     * <li>multi-threading</li>
     * <li>debug print of progress percentage in Console window/tab</li>
     *</ul>
     */
    private PixelManager pixelManager;

    // --- Anti-Aliasing additions ---
    /**
     * The super-sampling level per dimension.
     * A value of 1 means 1x1 rays per pixel (no anti-aliasing).
     * A value of N means N x N rays per pixel.
     */
    private int superSamplingLevel = 1; // Default to 1 (no AA)

    /**
     * Enum to define the sampling method for anti-aliasing.
     * CENTER: A single ray at the pixel center (default, no super-sampling).
     * GRID: Uniform grid sampling within the pixel area.
     * ADAPTIVE: Adaptive supersampling based on color changes.
     */
    public enum SamplingMethod {
        CENTER, // A single ray at the exact center of the pixel (no super-sampling benefit)
        GRID,    // Uniform grid sampling within the pixel area
        ADAPTIVE // Adaptive supersampling based on color changes
    }

    /** The sampling method to use for anti-aliasing */
    private SamplingMethod samplingMethod = SamplingMethod.CENTER; // Default to CENTER

    /** Whether to explicitly include the original central ray in the anti-aliasing sample set. */
    private boolean includeOriginalRayInAA = true; // Default to true

    /** Maximum recursion level for adaptive supersampling */
    private int adaptiveMaxLevel = 3; // Default recursion depth
    /** Color difference threshold for adaptive supersampling subdivision */
    private double adaptiveColorThreshold = 50.0; // Default color difference threshold (e.g., Euclidean distance in RGB)


    /**
     * Private constructor to prevent direct instantiation.
     * Use the Builder class to create a Camera object.
     */
    private Camera() {
    }

    /**
     * Returns a builder for constructing a Camera object.
     *
     * @return A new Builder instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera to a specific point on the view plane.
     * This method always constructs a single ray to the center of the specified pixel.
     * This is useful for debugging or when anti-aliasing is not desired for a specific ray.
     *
     * @param nX The number of pixels along the x-axis (width).
     * @param nY The number of pixels along the y-axis (height).
     * @param j  The pixel index along the x-axis.
     * @param i  The pixel index along the y-axis.
     * @return A Ray object representing the ray from the camera to the target pixel.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Calculate the pixel's width and height
        double pixelWidth = width / nX;
        double pixelHeight = height / nY;

        // Calculate the center of the pixel relative to the center of the view plane
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth; // Horizontal offset
        double yI = (i - (nY - 1) / 2.0) * pixelHeight; // Vertical offset

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
    private Ray constructSingleRayInternal(double xOffset, double yOffset) {
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
        }

        return rays;
    }


    /**
     * Optimized adaptive sampling that uses corner samples to determine if subdivision is needed.
     * This method significantly reduces the number of rays traced by sampling strategically.
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
     * This function renders image's pixel color map from the scene
     * included in the ray tracer object
     * @return the camera object itself
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
     * This method incorporates the anti-aliasing logic.
     *
     * @param j The pixel column index.
     * @param i The pixel row index.
     */
    private void castRaysAndAverage(int j, int i) {
        // Construct all rays for the current pixel (including sub-pixel rays for AA)
        List<Ray> rays = constructRays(nX, nY, j, i);
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
     * Prints a grid on the image.
     * This method adds a grid pattern to the rendered image for visual reference.
     *
     * @param interval The interval between grid lines.
     * @param color    The color of the grid lines.
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
     * Writes the rendered image to a file.
     * This method saves the generated image to a specified file name.
     *
     * @param fileName The name of the file to save the image.
     * @return The Camera object itself for method chaining.
     */
    public Camera writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
        return this;
    }

    /**
     * Render image using multi-threading by parallel streaming.
     * Calls the new {@code castRaysAndAverage} method for each pixel.
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRaysAndAverage(j, i)));
        return this;
    }

    /**
     * Render image without multi-threading.
     * Calls the new {@code castRaysAndAverage} method for each pixel.
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRaysAndAverage(j, i);
        return this;
    }

    /**
     * Render image using multi-threading by creating and running raw threads.
     * Calls the new {@code castRaysAndAverage} method for each pixel.
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        // Ensure threadsCount is not negative due to decrement in loop condition
        int currentThreads = threadsCount; // Store initial value
        while (currentThreads-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRaysAndAverage(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    /**
     * Builder class to construct a Camera object with specific parameters.
     */
    public static class Builder {
        private final Camera camera = new Camera();
        private Point target = null; // Target point the camera is looking at


        /**
         * Sets the direction vectors of the camera.
         *
         * @param vTo The forward direction vector.
         * @param vUp The upward direction vector.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the direction vectors are not orthogonal.
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
         * Sets the direction of the camera to look at a target point.
         *
         * @param target The point the camera is looking at.
         * @return The Builder instance.
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
         * @param target The point the camera is looking at.
         * @param vUp    The upward direction vector.
         * @return The Builder instance.
         */
        public Builder setDirection(Point target, Vector vUp) {
            this.target = target;
            camera.vUp = vUp.normalize();
            camera.vTo = null; // Will be calculated in validation
            camera.vRight = null; // Will be calculated in validation
            return this;
        }

        /**
         * Sets the location of the camera.
         *
         * @param p0 The position of the camera.
         * @return The Builder instance.
         */
        public Builder setLocation(Point p0) {
            // Set the camera's position
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance The distance to the view plane.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the distance is not positive.
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
         * Sets the size of the view plane.
         *
         * @param width  The width of the view plane.
         * @param height The height of the view plane.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the width or height is not positive.
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
         * Sets the ray tracer for rendering the scene based on the scene and tracer type.
         * This method is kept for backward compatibility with existing tests.
         * Internally creates a SimpleRayTracer.
         *
         * @param scene      The scene to be rendered.
         * @param tracerType The type of ray tracer to use (currently only SIMPLE is directly supported here).
         * @return The Builder instance.
         * @throws IllegalArgumentException if scene is null or tracerType is not SIMPLE.
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
         * @param rayTracer The instantiated RayTracerBase object to use.
         * @return The Builder instance.
         * @throws IllegalArgumentException if the rayTracer is null.
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            if (rayTracer == null) {
                throw new IllegalArgumentException("RayTracer cannot be null");
            }
            camera.rayTracer = rayTracer;
            return this;
        }

        /**
         * Sets the resolution of the view plane (number of pixels in the x and y directions).
         *
         * @param nx The number of pixels in the x direction.
         * @param ny The number of pixels in the y direction.
         * @return The Builder instance.
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


        /**
         * Set multi-threading <br>
         * Parameter value meaning:
         * <ul>
         * <li>-2 - number of threads is number of logical processors less 2</li>
         * <li>-1 - stream processing parallelization (implicit multi-threading) is used</li>
         * <li>0  - multi-threading is not activated</li>
         * <li>1 and more - literally number of threads</li>     *
         * </ul>
         * @param  threads number of threads
         * @return         builder object itself
         * */
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
         * Set debug printing interval. If it's zero - there won't be printing at all
         * @param  interval printing interval in %
         * @return          builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            camera.printInterval = interval;
            return this;
        }

        // --- Anti-Aliasing Builder methods ---
        /**
         * Sets the super-sampling level for anti-aliasing.
         * A level of 1 means no super-sampling (1 ray per pixel).
         * A level of N means N x N rays per pixel for anti-aliasing.
         *
         * @param level The super-sampling level (must be positive).
         * @return The Builder instance.
         * @throws IllegalArgumentException if the level is less than 1.
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
         * @param method The {@link SamplingMethod} to use (e.g., CENTER, GRID, ADAPTIVE).
         * @return The Builder instance.
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
         * @param include True to include, false to exclude.
         * @return The Builder instance.
         */
        public Builder setIncludeOriginalRayInAA(boolean include) {
            camera.includeOriginalRayInAA = include;
            return this;
        }

        /**
         * Sets the maximum recursion level for adaptive supersampling.
         *
         * @param level The maximum recursion depth (must be positive).
         * @return The Builder instance.
         * @throws IllegalArgumentException if the level is less than 1.
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
         * @param threshold The color difference threshold (must be non-negative).
         * @return The Builder instance.
         * @throws IllegalArgumentException if the threshold is negative.
         */
        public Builder setAdaptiveColorThreshold(double threshold) {
            if (threshold < 0) {
                throw new IllegalArgumentException("Adaptive color threshold cannot be negative");
            }
            camera.adaptiveColorThreshold = threshold;
            return this;
        }

        /**
         * Builds and returns the constructed Camera object.
         * This method ensures immutability by cloning the Camera instance.
         *
         * @return The constructed Camera object.
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
         *
         * @param camera The Camera object to validate.
         * @throws MissingResourceException if required parameters are missing or invalid.
         * @throws IllegalStateException if the ImageWriter could not be initialized due to missing scene/tracer.
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

            // Ensure RayTracer is set
            if (camera.rayTracer == null) {
                throw new MissingResourceException("Missing rendering data", "Camera", "RayTracer is not set");
            }
            // ImageWriter must be initialized after resolution and rayTracer are set
            if (camera.imageWriter == null) {
                throw new IllegalStateException("ImageWriter not initialized. Call setResolution after setRayTracer.");
            }

            // Clear the target to avoid retaining unnecessary state in the builder after validation
            target = null;
        }
    }
}