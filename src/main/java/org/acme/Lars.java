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

public class Lars {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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

        Scalar lowerCyan = new Scalar(80, 50, 50);
        Scalar upperCyan = new Scalar(100, 255, 255);
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(255, 200, 200);

        Mat maskCyan = new Mat();
        Mat maskRed = new Mat();
        Core.inRange(hsvImage, lowerCyan, upperCyan, maskCyan);
        Core.inRange(hsvImage, lowerRed, upperRed, maskRed);

        Mat cyanObjects = new Mat();
        Mat redObjects = new Mat();
        Mat allObjects = new Mat();
        Core.bitwise_and(image, image, cyanObjects, maskCyan);
        Core.bitwise_and(image, image, redObjects, maskRed);
        Core.bitwise_and(image, image, allObjects, new Mat(maskCyan.size(), maskCyan.type(), new Scalar(255)));

        displayImage("Original", image);
        displayImage("Nur Cyan Objekte", cyanObjects);
        displayImage("Rot", redObjects);
        displayImage("Red and Blue", allObjects);
    }

    private static void displayImage(String windowName, Mat image) {
        Imgcodecs.imwrite(windowName + ".jpg", image);
        System.out.println("Displaying " + windowName + ".jpg");
    }
}
