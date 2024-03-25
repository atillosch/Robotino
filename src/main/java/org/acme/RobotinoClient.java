package org.acme;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RobotinoClient {

    private static final String ROBOTINO_IP = "192.168.0.1";
    private static final int PORT = 12080;
    private static final String PARAMS = "{\"sid\":\"polygon_drive\"}";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        RobotinoClient client = new RobotinoClient();
        client.getLaserRange();
        client.getCameraImage();
    }

    public void getLaserRange() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://%s/data/scan0", ROBOTINO_IP)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public void getCameraImage() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://%s/cam0", ROBOTINO_IP)))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.body()));
        Mat mat = Imgcodecs.imdecode(new MatOfByte(response.body()), Imgcodecs.IMREAD_UNCHANGED);
        Imgproc.rectangle(mat, new Point(10, 10), new Point(100, 100), new Scalar(255, 0, 0), 5);
        Imgcodecs.imwrite("cam_bild.jpg", mat);
    }
}