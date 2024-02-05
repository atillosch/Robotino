package org.acme;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.objdetect.CascadeClassifier;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RobotinoMoveTowardsRedObject {

    public static void main(String[] args) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the host, protocol, and port
            String apiUrl = "http://192.168.0.1/data/omnidrive";

            // specify the initial velocity values for forward motion
            double vx = 0.5;  // x-velocity in m/s
            double vy = 0.0;  // y-velocity in m/s
            double omega = 0.0;  // rotational velocity in rad/s

            // create the JSON payload
            String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

            // create the HTTP PUT request
            HttpPut putRequest = new HttpPut(apiUrl);
            putRequest.setHeader("Content-Type", "application/json");

            // create the camera capture object
            VideoCapture videoCapture = new VideoCapture(0);

            // set properties for video capture
            videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

            // start capturing video from the default camera
            Mat webcamMatImage = new Mat();
            MatOfByte mem = new MatOfByte();

            while (true) {
                // Capture frame from camera
                videoCapture.read(webcamMatImage);
                Imgcodecs.imencode(".bmp", webcamMatImage, mem);

                // Convert the Mat to Image
                Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

                // Create a buffered image with a format that's compatible with the screen
                BufferedImage bufferedImage = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);

                // Draw the image on to the buffered image
                Graphics2D bGr = bufferedImage.createGraphics();
                bGr.drawImage(im, 0, 0, null);
                bGr.dispose();

                // Process the image to find red pixels
                if (containsRed(bufferedImage)) {
                    // If red pixels are found, move the robot towards the red object
                    vx = 0.5;  // Adjust the velocity values as needed
                } else {
                    // If no red pixels are found, stop the robot
                    vx = 0.0;
                }

                // Update the JSON payload
                jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

                // Update the HTTP PUT request with the new payload
                putRequest.setEntity(new StringEntity(jsonPayload));

                // Execute the request
                CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
                HttpEntity entity = httpResponse.getEntity();

                // print the response body if available
                if (entity != null) {
                    System.out.println(EntityUtils.toString(entity));
                }

                // Release resources
                httpResponse.close();
                Thread.sleep(100); // Add a short delay to avoid overloading the robot
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean containsRed(BufferedImage image) {
        // Process the image to find red pixels
        int redThreshold = 150; // Adjust the threshold as needed
        int redCount = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                if (red > redThreshold && green < 100 && blue < 100) {
                    redCount++;
                }
            }
        }

        return redCount > 100; // Adjust the threshold as needed
    }
}
