package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UtilityTool {

    // Scales image to preferred width and height
    public BufferedImage scaleImage(BufferedImage original, int width, int height){

        // New BufferedImage with the specified width and height, and the same type as the original image.
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());

        // Get the Graphics2D object to draw the scaled version of the original image.
        Graphics2D g2 = scaledImage.createGraphics();

        // Draw the original image scaled to the new width and height.
        g2.drawImage(original, 0, 0, width, height, null);

        g2.dispose();

        return scaledImage; // Return the new scaled image
    }
}
