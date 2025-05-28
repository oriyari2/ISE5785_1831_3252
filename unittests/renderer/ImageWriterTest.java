package renderer;

import org.junit.jupiter.api.Test;
import primitives.*;

import static primitives.Util.isZero;

/**
 * Unit tests for the ImageWriter class.
 * This test creates a simple image with a grid pattern and saves it to a file.
 */
public class ImageWriterTest {

    Color YELLOW = new Color(java.awt.Color.YELLOW);
    Color RED = new Color(java.awt.Color.RED);

    @Test
    void testYellowImageWriter() {
        int nx = 800;
        int ny = 500;
        int interval = 50;
        String filePath = ""; // Define the file path

        // Create an ImageWriter object
        ImageWriter imageWriter = new ImageWriter(filePath, nx, ny);

        // Fill the entire image with yellow color
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                imageWriter.writePixel(i, j, YELLOW);
            }
        }

        // Draw horizontal red grid lines
        for (int row = 0; row < ny; row += interval) {
            for (int col = 0; col < nx; col++) {
                imageWriter.writePixel(col, row, RED);
            }
        }

        // Draw vertical red grid lines
        for (int col = 0; col < nx; col += interval) {
            for (int row = 0; row < ny; row++) {
                imageWriter.writePixel(col, row, RED);
            }
        }

        // Save the image to a file
        imageWriter.writeToImage("yellowFirstImage");
    }
}

