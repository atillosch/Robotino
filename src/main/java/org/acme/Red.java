package org.acme;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class Red {

    public static void main(String[] args) {
        try {
            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the Robotino's IP address
            String robotinoIP = "http://192.168.0.1";

            // get camera image from XF4 port
            HttpGet cameraRequest = new HttpGet(robotinoIP + "/cam0");
            byte[] cameraImageData = httpclient.execute(cameraRequest, response -> EntityUtils.toByteArray(response.getEntity()));

            // process camera image to detect red object
            boolean isRedObjectDetected = processCameraImage(cameraImageData);

            if (isRedObjectDetected) {
                // specify the velocity values for forward motion towards the red object
                double vx = 0.5;  // x-velocity in m/s
                double vy = 0.0;  // y-velocity in m/s
                double omega = 0.0;  // rotational velocity in rad/s

                // create the JSON payload
                String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

                // create the HTTP PUT request for moving forward
                HttpPut putRequest = new HttpPut(robotinoIP + "/data/omnidrive");
                putRequest.setHeader("Content-Type", "application/json");
                putRequest.setEntity(new StringEntity(jsonPayload));

                System.out.println("Executing request to " + robotinoIP + "/data/omnidrive");

                // execute the request
                CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
                // ... rest of the code to handle the response

            } else {
                System.out.println("Red object detected. Robotino will move forward.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean processCameraImage(byte[] imageData) {
        try {
            // Convert byte array to BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);
    
            // Implement image processing logic to detect red object
            // Example: Check if around 30% of the image contains red pixels
    
            int redPixelCount = 0;
            int totalPixelCount = image.getWidth() * image.getHeight();
    
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
    
                    // Check if the pixel is red
                    if (red > 50) {  // adjust this threshold based on your requirements
                        redPixelCount++;
                    }
                }
            }
    
            // Calculate the percentage of red pixels
            double redPixelPercentage = (double) redPixelCount / totalPixelCount * 100;
    
            // Return true if around 30% of the pixels are red
            return redPixelPercentage > 30;
    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
}