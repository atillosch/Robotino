package org.acme;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BisBlau {

    static {
        System.load("C:\\Users\\Atilla Coskun\\Documents\\ADV\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    private static final String ROBOTINO_IP = "192.168.0.1";
    private static final int PORT = 12080;

    public static void main(String[] args) {

        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String apiUrl = "http://192.168.0.1/data/omnidrive";
            double vx = 0.5;
            double vy = 0.0;
            double omega = 0.0;
            String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

            while (true) {
                try {
                    byte[] imageData = getCameraImageData();
                    Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
                    if (image.empty()) {
                        System.out.println("Error: Unable to decode image data.");
                        continue;
                    }

                    if (isBlueObjectDetected(image)) {
                        // Stop the robot if a blue object is detected
                        vx = 0.0;
                        vy = 0.0;
                        omega = 0.0;
                        jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);
                        break; // Exit the loop
                    }

                    HttpPut putRequest = new HttpPut(apiUrl);
                    putRequest.setHeader("Content-Type", "application/json");
                    putRequest.setEntity(new StringEntity(jsonPayload));

                    CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
                    HttpEntity entity = httpResponse.getEntity();

                    if (entity != null) {
                        System.out.println(EntityUtils.toString(entity));
                    }

                    Thread.sleep(200);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] getCameraImageData() throws IOException {
        String cameraUrl = "http://" + ROBOTINO_IP + "/cam0";
        URL url = new URL(cameraUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        byte[] imageData = connection.getInputStream().readAllBytes();
        connection.disconnect();

        return imageData;
    }

    private static boolean isBlueObjectDetected(Mat image) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        Scalar lowerBlue = new Scalar(90, 50, 50); // Untere Grenze für Blau im HSV-Farbraum
        Scalar upperBlue = new Scalar(130, 255, 255); // Obere Grenze für Blau im HSV-Farbraum

        Mat maskBlue = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, maskBlue);

        return Core.countNonZero(maskBlue) > 0;
    }
}
