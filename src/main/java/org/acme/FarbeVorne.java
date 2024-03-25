package org.acme;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class FarbeVorne {

    /**
     * Adjusted version to make the robot move forward when red color is detected
     *
     * @param image
     *            image to process
     * @param maxDist
     *            max distance from red color
     * @return true if red color is detected, false otherwise
     */
    public static boolean moveForwardOnRed(BufferedImage image, double maxDist) {
        float dh, dv;
        float wds, wdh, wdv;
        double curDist;

        float[] hsvColor = new float[] { 0.0f, 1.0f, 1.0f }; // Red color in HSV

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color pixelColor = new Color(image.getRGB(x, y));
                float[] hsv = Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), null);

                dh = Math.abs(hsv[0] - hsvColor[0]);
                dv = Math.abs(hsv[2] - hsvColor[2]);

                wds = Math.abs(hsv[1] - hsvColor[1]);
                wdh = dh / 360.0f; // assuming H is in degrees
                wdv = dv;

                curDist = Math.sqrt(wdh * wdh + wds * wds + wdv * wdv);

                if (curDist <= maxDist) {
                    // Red color detected, move forward (replace this with your robot control code)
                    System.out.println("Red color detected, move forward!");
                    return true;
                }
            }
        }

        // No red color detected
        return false;
    }

    // ... (Rest of the code remains unchanged)

}
