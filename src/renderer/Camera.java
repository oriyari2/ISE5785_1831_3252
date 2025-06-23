package renderer;

import primitives.*;
import scene.Scene;
import java.util.stream.IntStream;
import java.util.LinkedList;

import java.util.MissingResourceException;

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
    private int              threadsCount     = 0;
    /**
     * Amount of threads to spare for Java VM threads:<br>
     * Spare threads if trying to use all the cores
     */
    private static final int SPARE_THREADS    = 2;
    /**
     Debug print interval in seconds (for progress percentage) <br>
     if it is zero - there is no progress output
     */
    private double           printInterval    = 0;
    /**
      * Pixel manager for supporting:
     * <ul>
          * <li>multi-threading</li>
          * <li>debug print of progress percentage in Console window/tab</li>
     *</ul>
     */
    private PixelManager pixelManager;


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
     *  This function renders image's pixel color map from the scene
     *  included in the ray tracer object
     *  @return the camera object itself
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
     * Casts a ray from the camera to a specific pixel on the view plane.
     *
     * @param j
     * @param i
     */
    private void castRay(int j, int i) {
        // Calculate the size of each pixel in the view plane
        Ray ray = constructRay(nX, nY, j, i);
        // Perform ray tracing and set the pixel color in the image writer
        Color color = rayTracer.traceRay(ray);
        if (color == null) {
            // If the color is null, set it to black
            color = Color.BLACK;
        }
        // Set the pixel color in the image writer
        imageWriter.writePixel(j, i, color);
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
    public Camera writeToImage(String fileName){
        imageWriter.writeToImage(fileName);
        return this;
    }

    /**
     *  Render image using multi-threading by parallel streaming
     *  @return the camera object itself
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }
    /**
     *  Render image without multi-threading
     *  @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRay(j, i);
        return this;
    }
    /**
     * Render image using multi-threading by creating and running raw threads
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        }
        catch (InterruptedException ignored) {}
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
            // Ensure the direction vectors are orthogonal
            if (!isZero(vTo.dotProduct(vUp))) {
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
         * Sets the ray tracer for rendering the scene.
         *
         * @param scene      The scene to be rendered.
         * @param tracerType The type of ray tracer to use.
         * @return The Builder instance.
         */

        public Builder setRayTracer(Scene scene, RayTracerType tracerType)
        {
            // Set the ray tracer for rendering the scene
            if(tracerType == RayTracerType.SIMPLE)
                camera.rayTracer = new SimpleRayTracer(scene);
            else
                camera.rayTracer = null;
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
            }
            catch (MissingResourceException ignored) {
                return null;
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Validates the camera parameters and sets default values if necessary.
         *
         * @param camera The Camera object to validate.
         * @throws MissingResourceException if required parameters are missing or invalid.
         */
        private void validate(Camera camera) throws MissingResourceException {
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
            if (!isOrthogonal(camera.vTo, camera.vUp)) {
                camera.vUp = camera.vTo.crossProduct(camera.vUp).crossProduct(camera.vTo).normalize();
            }

            // Calculate the rightward direction vector (vRight) as the cross product of vTo and vUp
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            // Calculate the center of the view plane based on the camera position and vTo
            camera.pcenter = camera.p0.add(camera.vTo.scale(camera.distance));

            // Clear the target to avoid retaining unnecessary state
            target = null;
        }

        /**
         * Checks if two vectors are orthogonal.
         *
         * @param v1 The first vector.
         * @param v2 The second vector.
         * @return true if the vectors are orthogonal, false otherwise.
         */
        private boolean isOrthogonal(Vector v1, Vector v2) {
            return isZero(v1.dotProduct(v2));
        }

        /**
         * Sets the resolution of the view plane (number of pixels in the x and y directions).
         *
         * @param nx The number of pixels in the x direction.
         * @param ny The number of pixels in the y direction.
         * @return The Builder instance.
         */
        public Builder setResolution(int nx, int ny) {
            if(alignZero(nx) <= 0 || alignZero(ny) <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }
            camera.nX = nx;
            camera.nY = ny;
            camera.imageWriter = new ImageWriter(camera.rayTracer.scene.name ,nx, ny);
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
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            }
            else
                camera.threadsCount = threads;
            return this;
        }
        /**
         *  Set debug printing interval. If it's zero - there won't be printing at all
         * @param  interval printing interval in %
         * @return          builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            camera.printInterval = interval;
            return this;
        }
    }

}
