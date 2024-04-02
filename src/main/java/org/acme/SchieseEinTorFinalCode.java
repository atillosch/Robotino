package org.acme;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SchieseEinTorFinalCode {
    static {
        System.load("C:\\Users\\Atilla Coskun\\Documents\\ADV\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    private static final String ROBOTINO_IP = "192.168.0.1";
    private static final int PORT = 12080;

    public static void main(String[] args) {
        while (true) {
            try {
                boolean foundBlue = findAndNavigateToBlueObject();
                if (foundBlue) {
                    alignWithGoalAndShoot();
                } else {
                    turnRight(1.0);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean findAndNavigateToBlueObject() throws IOException, InterruptedException {
        byte[] imageData = getCameraImageData();
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
        if (image.empty()) {
            System.out.println("Error: Unable to decode image data.");
            return false;
        }

        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        Scalar lowerBlue = new Scalar(100, 100, 100); // Untere Grenze für Blau im HSV-Farbraum
        Scalar upperBlue = new Scalar(140, 255, 255); // Obere Grenze für Blau im HSV-Farbraum

        Mat maskBlue = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, maskBlue);

        double bluePixels = Core.countNonZero(maskBlue);
        boolean foundBlue = bluePixels > 0;

        if (!foundBlue) {
            // Wenn kein blaues Objekt gefunden wurde, drehe den Roboter nach rechts und pausiere
            sendVelocityCommand(0.0, 0.0, 1.0); // Omega auf 1 setzen, um nach rechts zu drehen
            Thread.sleep(1000); // 1 Sekunde Pause
        }

        return foundBlue;
    }

    private static void alignWithGoalAndShoot() throws IOException {
        byte[] imageData = getCameraImageData();
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
        if (image.empty()) {
            System.out.println("Error: Unable to decode image data.");
            return;
        }

        // Identifiziere das weiße Tor
        boolean foundGoal = identifyGoal(image);

        if (foundGoal) {
            // Berechne die Richtung zum Ball und zum Tor
            double[] ballPosition = calculateBallPosition(image);
            double[] goalPosition = calculateGoalPosition(image);

            // Berechne den Vektor, um den Ball in Richtung Tor zu bewegen
            double[] directionVector = calculateDirectionVector(ballPosition, goalPosition);

            // Bewege den Roboter in Richtung des Vektors
            sendVelocityCommand(directionVector[0], directionVector[1], 0.0);

            // Schieße den Ball in Richtung des Tors
            shootTowardsGoal();
        } else {
            System.out.println("Error: Unable to identify the goal.");
        }
    }

    private static boolean identifyGoal(Mat image) {
        // Hier implementieren Sie den Code, um das weiße Tor zu identifizieren
        // Rückgabe true, wenn das Tor gefunden wurde, ansonsten false
        return false; // Platzhalter, ersetzen Sie durch Ihre Implementierung
    }

    private static double[] calculateBallPosition(Mat image) {
        // Hier implementieren Sie den Code, um die Position des blauen Balls zu berechnen
        // Rückgabe: ein Array mit x- und y-Koordinaten des Balls
        return new double[]{0.0, 0.0}; // Platzhalter, ersetzen Sie durch Ihre Implementierung
    }

    private static double[] calculateGoalPosition(Mat image) {
        // Hier implementieren Sie den Code, um die Position des weißen Tors zu berechnen
        // Rückgabe: ein Array mit x- und y-Koordinaten des Tors
        return new double[]{0.0, 0.0}; // Platzhalter, ersetzen Sie durch Ihre Implementierung
    }

    private static double[] calculateDirectionVector(double[] ballPosition, double[] goalPosition) {
        // Hier implementieren Sie den Code, um den Richtungsvektor zu berechnen
        // Der Vektor sollte den Roboter vom Ball zum Tor ausrichten
        // Rückgabe: ein Array mit x- und y-Komponenten des Vektors
        return new double[]{0.0, 0.0}; // Platzhalter, ersetzen Sie durch Ihre Implementierung
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

    private static void moveForward(double velocity) throws IOException {
        sendVelocityCommand(velocity, velocity, 0.0);
    }

    private static void turnRight(double duration) throws IOException {
        // For simplicity, we can just turn right by stopping the forward motion
        // and applying an angular velocity (omega) to the robot.
        sendVelocityCommand(0.0, 0.0, -1.0); // Rotate clockwise
    }

    private static void shootTowardsGoal() throws IOException {
        // Hier fügen Sie den Code hinzu, um den Ball in Richtung des Tors zu schießen
        // Beispiel:
        // shootCommand();
    }

    private static void sendVelocityCommand(double vx, double vy, double omega) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String apiUrl = "http://" + ROBOTINO_IP + "/data/omnidrive";
        String jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);

        HttpPut putRequest = new HttpPut(apiUrl);
        putRequest.setHeader("Content-Type", "application/json");
        putRequest.setEntity(new StringEntity(jsonPayload));

        System.out.println("Executing request to " + apiUrl);

        CloseableHttpResponse httpResponse = httpclient.execute(putRequest);
        HttpEntity entity = httpResponse.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(httpResponse.getStatusLine());
        Header[] headers = httpResponse.getAllHeaders();
        for (Header header : headers) {
            System.out.println(header);
        }
        System.out.println("----------------------------------------");

        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
    }
}
