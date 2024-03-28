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

public class gelb {
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

            processAndDisplayImage(image);

        } catch (IOException e) {
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

    private static void processAndDisplayImage(Mat image) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
    
        Scalar lowerYellow = new Scalar(20, 100, 100); // Untere Grenze für Gelb im HSV-Farbraum
        Scalar upperYellow = new Scalar(30, 255, 255); // Obere Grenze für Gelb im HSV-Farbraum
    
        Mat maskYellow = new Mat();
        Core.inRange(hsvImage, lowerYellow, upperYellow, maskYellow);
    
        Mat yellowObjects = new Mat();
        Core.bitwise_and(image, image, yellowObjects, maskYellow);
    
        displayImage("Original", image);
        displayImage("Nur Gelbe Objekte", yellowObjects);
    }
    
    
    
    private static void displayImage(String windowName, Mat image) {
        String imagePath = "C:\\Users\\Atilla Coskun\\Documents\\ADV\\Bilder\\" + windowName + ".jpg";
        Imgcodecs.imwrite(imagePath, image);
        System.out.println("Image saved to: " + imagePath);
    }
    
}
