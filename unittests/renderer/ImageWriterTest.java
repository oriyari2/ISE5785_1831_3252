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

        // Fill the image with yellow color
        for (int j = 0; j < nx; j++) {
            for (int i = 0; i < ny; i++) {
                // Draw a grid of red lines
                if (isZero(i % interval) || isZero(j % interval)) {
                    imageWriter.writePixel(j, i, RED);
                } else {
                    // Fill the rest of the image with yellow color
                    imageWriter.writePixel(j, i, YELLOW);
                }
            }
        }
        // Save the image to a file
        imageWriter.writeToImage("yellowFirstImage");
    }
}

