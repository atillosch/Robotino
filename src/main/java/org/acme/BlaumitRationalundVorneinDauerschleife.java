package org.acme;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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

public class BlaumitRationalundVorneinDauerschleife {
    static {
        System.load("C:\\Users\\Atilla Coskun\\Documents\\ADV\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    private static final String ROBOTINO_IP = "192.168.0.1";
    private static final int PORT = 12080;

    public static void main(String[] args) {
        BlaumitRationalundVorneinDauerschleife robot = new BlaumitRationalundVorneinDauerschleife();
        robot.start();
    }

    private void start() {
        while (true) {
            try {
                byte[] imageData = getCameraImageData();
                Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
                if (image.empty()) {
                    System.out.println("Error: Unable to decode image data.");
                    continue;
                }

                boolean foundBlue = processAndDisplayImage(image);

                if (foundBlue) {
                    // Wenn blaue Objekte gefunden wurden, führe vorwärts Bewegung aus
                    forwardMovement();
                } else {
                    // Wenn keine blauen Objekte gefunden wurden, führe Drehbewegung aus
                    rotate();
                }

                // Warte für eine kurze Zeit, bevor der nächste Durchlauf beginnt
                //Thread.sleep(1000);

            } catch (IOException  /*InterruptedException*/ e) {
                e.printStackTrace();
            }
        }
    }

    private boolean processAndDisplayImage(Mat image) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        Scalar lowerBlue = new Scalar(100, 180, 180); // Untere Grenze für Blau im HSV-Farbraum
        Scalar upperBlue = new Scalar(120, 255, 255); // Obere Grenze für Blau im HSV-Farbraum

        Mat maskBlue = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, maskBlue);

        Mat blueObjects = new Mat();
        Core.bitwise_and(image, image, blueObjects, maskBlue);

        displayImage("Original", image);
        displayImage("Nur Blaue Objekte", blueObjects);

        // Überprüfe, ob blaue Objekte gefunden wurden
        return Core.countNonZero(maskBlue) > 0;
    }

    private byte[] getCameraImageData() throws IOException {
        String cameraUrl = "http://" + ROBOTINO_IP + "/cam0";
        URL url = new URL(cameraUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        byte[] imageData = connection.getInputStream().readAllBytes();
        connection.disconnect();

        return imageData;
    }

    private void displayImage(String windowName, Mat image) {
        String imagePath = "C:\\Users\\Atilla Coskun\\Documents\\ADV\\Bilder\\" + windowName + ".jpg";
        Imgcodecs.imwrite(imagePath, image);
        System.out.println("Image saved to: " + imagePath);
    }

    private void forwardMovement() {
        try {
            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the host, protocol, and port
            String apiUrl = "http://192.168.0.1/data/omnidrive";

            // specify the velocity values for forward motion
            double vx = 2;  // x-velocity in m/s
            double vy = 0;  // y-velocity in m/s
            double omega = 0;  // rotational velocity in rad/s

            // create the JSON payload
            String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

            // create the HTTP PUT request
            HttpPut putRequest = new HttpPut(apiUrl);
            putRequest.setHeader("Content-Type", "application/json");
            putRequest.setEntity(new StringEntity(jsonPayload));

            System.out.println("Executing request to " + apiUrl);

            // execute the request
            CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
            HttpEntity entity = httpResponse.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(httpResponse.getStatusLine());
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                System.out.println(header);
            }
            System.out.println("----------------------------------------");

            // print the response body if available
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotate() {
        try {
            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the host, protocol, and port
            String apiUrl = "http://192.168.0.1/data/omnidrive";

            // specify the velocity values for forward motion
            double vx = 0;  // x-velocity in m/s
            double vy = 0;  // y-velocity in m/s
            double omega = 1;  // rotational velocity in rad/s

            // create the JSON payload
            String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

            // create the HTTP PUT request
            HttpPut putRequest = new HttpPut(apiUrl);
            putRequest.setHeader("Content-Type", "application/json");
            putRequest.setEntity(new StringEntity(jsonPayload));

            System.out.println("Executing request to " + apiUrl);

            // execute the request
            CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
            HttpEntity entity = httpResponse.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(httpResponse.getStatusLine());
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                System.out.println(header);
            }
            System.out.println("----------------------------------------");

            // print the response body if available
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
