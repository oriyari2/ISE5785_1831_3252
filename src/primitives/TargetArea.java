package primitives;

import renderer.Camera; // Import Camera for SamplingMethod enum
import java.util.LinkedList;
import java.util.List;
import static primitives.Util.isZero;

/**
 * The TargetArea class represents a 2D rectangular area in 3D space,
 * used for generating sample points for anti-aliasing or other effects
 * where a beam of rays is required.
 * It establishes a local 2D coordinate system within the 3D space.
 */
public class TargetArea {
    private final Point referencePoint; // The central 3D point of the target area
    private final Vector uDirection;    // Local X-axis of the target area (orthogonal to primary direction)
    private final Vector vDirection;    // Local Y-axis of the target area (orthogonal to primary direction and uDirection)
    private final double width;         // Width of the target area
    private final double height;        // Height of the target area

    /**
     * Constructs a TargetArea.
     * It creates a local 2D coordinate system (uDirection, vDirection)
     * perpendicular to the given primaryDirection, with referencePoint as origin.
     *
     * @param referencePoint   The 3D center point of the target area.
     * @param primaryDirection The primary direction vector (e.g., ray direction, camera vTo)
     * to which the target area should be perpendicular. Must be normalized.
     * @param width            The width of the target area.
     * @param height           The height of the target area.
     * @throws IllegalArgumentException if primaryDirection is a zero vector or if width/height are non-positive.
     */
    public TargetArea(Point referencePoint, Vector primaryDirection, double width, double height) {
        if (primaryDirection == null || isZero(primaryDirection.lengthSquared())) {
            throw new IllegalArgumentException("Primary direction vector cannot be null or zero.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Target area width and height must be positive.");
        }

        this.referencePoint = referencePoint;
        this.width = width;
        this.height = height;

        // Determine orthogonal vectors uDirection and vDirection for the 2D plane.
        // This is done by finding two vectors orthogonal to the primaryDirection
        // and to each other, forming a local coordinate system.
        // This handles edge cases where primaryDirection is aligned with standard axes.
        Vector tempVector;
        if (isZero(primaryDirection.dotProduct(Vector.AXIS_Y))) { // If primaryDirection is orthogonal to AXIS_Y
            tempVector = Vector.AXIS_Y;
        } else if (isZero(primaryDirection.dotProduct(Vector.AXIS_X))) { // If primaryDirection is orthogonal to AXIS_X
            tempVector = Vector.AXIS_X;
        } else { // Use AXIS_X as a general-purpose helper
            tempVector = Vector.AXIS_X;
        }

        // First orthogonal vector (uDirection)
        this.uDirection = primaryDirection.crossProduct(tempVector).normalize();

        // Second orthogonal vector (vDirection)
        this.vDirection = primaryDirection.crossProduct(this.uDirection).normalize();
    }

    /**
     * Generates a list of 3D sample points within the target area based on the
     * specified super-sampling level, sampling method, and whether to include
     * the central point.
     *
     * @param superSamplingLevel  The number of samples per dimension (e.g., 3 for 3x3 grid).
     * @param samplingMethod      The method for generating samples (e.g., GRID, CENTER).
     * @param includeOriginalRay  If true, the exact center point is always included,
     * even for GRID sampling.
     * @return A list of 3D sample points on the target area.
     */
    public List<Point> generateSamplePoints(
            int superSamplingLevel,
            Camera.SamplingMethod samplingMethod,
            boolean includeOriginalRay) {
        List<Point> samplePoints = new LinkedList<>();

        if (superSamplingLevel < 1) {
            throw new IllegalArgumentException("Super-sampling level must be at least 1.");
        }

        // If superSamplingLevel is 1 or samplingMethod is CENTER, only add the central point.
        if (superSamplingLevel == 1 || samplingMethod == Camera.SamplingMethod.CENTER) {
            samplePoints.add(referencePoint);
            return samplePoints;
        }

        // --- GRID sampling ---
        // Calculate the dimensions of each sub-square within the target area
        double subWidth = width / superSamplingLevel;
        double subHeight = height / superSamplingLevel;

        // Iterate over a grid of sub-squares
        for (int row = 0; row < superSamplingLevel; row++) {
            for (int col = 0; col < superSamplingLevel; col++) {
                // Calculate the center of the current sub-square relative to the target area's center (0,0)
                // (col - (superSamplingLevel - 1) / 2.0) * subWidth: moves from left edge to right edge of the grid
                // (row - (superSamplingLevel - 1) / 2.0) * subHeight: moves from top edge to bottom edge of the grid
                double xOffset = (col - (superSamplingLevel - 1) / 2.0) * subWidth;
                double yOffset = (row - (superSamplingLevel - 1) / 2.0) * subHeight;

                // Map the 2D offset to a 3D point in the scene
                // referencePoint + xOffset * uDirection + yOffset * vDirection
                Point samplePoint = referencePoint;
                if (!isZero(xOffset)) {
                    samplePoint = samplePoint.add(uDirection.scale(xOffset));
                }
                if (!isZero(yOffset)) {
                    samplePoint = samplePoint.add(vDirection.scale(yOffset));
                }
                samplePoints.add(samplePoint);
            }
        }

        // If includeOriginalRay is true and it's GRID sampling,
        // we might have already included it if one of the grid points
        // happens to be exactly the center. For robustness, if not, add it.
        // However, with proper centering, the central point *is* one of the grid points
        // when superSamplingLevel is odd. If even, the grid points are slightly off-center.
        // To be explicit, if the intent is *always* to have the original ray,
        // we could add a check:
        if (includeOriginalRay && superSamplingLevel % 2 == 0) { // If superSamplingLevel is even, the exact center is not a grid point
            samplePoints.add(0, referencePoint); // Add to the beginning to make it predictable
        }


        return samplePoints;
    }
}
