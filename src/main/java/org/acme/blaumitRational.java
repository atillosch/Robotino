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

public class blaumitRational {
    static {
        System.load("C:\\Users\\Atilla Coskun\\Documents\\ADV\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    private static final String ROBOTINO_IP = "192.168.0.1";
    private static final int PORT = 12080;

    public static void main(String[] args) {
        try {
            byte[] imageData = getCameraImageData();
            Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
            if (image.empty()) {
                System.out.println("Error: Unable to decode image data.");
                return;
            }

            boolean foundBlue = processAndDisplayImage(image);

            if (foundBlue) {
                // Wenn blaue Objekte gefunden wurden, führe die Klasse RobotinoMoveRational aus
                RobotinoMoveRational.main(args);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean processAndDisplayImage(Mat image) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
    
        Scalar lowerBlue = new Scalar(90, 50, 50); // Untere Grenze für Blau im HSV-Farbraum
        Scalar upperBlue = new Scalar(130, 255, 255); // Obere Grenze für Blau im HSV-Farbraum
    
        Mat maskBlue = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, maskBlue);
    
        Mat blueObjects = new Mat();
        Core.bitwise_and(image, image, blueObjects, maskBlue);
    
        displayImage("Original", image);
        displayImage("Nur Blaue Objekte", blueObjects);

        // Überprüfe, ob blaue Objekte gefunden wurden
        return Core.countNonZero(maskBlue) > 0;
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

    private static void displayImage(String windowName, Mat image) {
        String imagePath = "C:\\Users\\Atilla Coskun\\Documents\\ADV\\Bilder\\" + windowName + ".jpg";
        Imgcodecs.imwrite(imagePath, image);
        System.out.println("Image saved to: " + imagePath);
    }
}
