package org.acme;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {

    public static void bb3(Mat image) {
        // Konvertiere das Bild in den HSV-Farbraum
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Definiere den Farbbereich für Cyan in HSV
        // Hinweis: Diese Werte können je nach Schattierung von Cyan, die du erkennen möchtest, angepasst werden
         Scalar lowerCyan = new Scalar(80, 50, 50);
        Scalar upperCyan = new Scalar(100, 255, 255);
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(255, 200, 200);

        // Erstelle eine Maske, die nur Farben innerhalb des Cyan-Bereichs zeigt
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerCyan, upperCyan, mask);
        Mat maskRed = new Mat();
        Core.inRange(hsvImage, lowerRed, upperRed, maskRed);

        // Wende die Maske auf das Originalbild an, um nur die cyanfarbenen Teile zu zeigen
        Mat cyanObjects = new Mat();
        Core.bitwise_and(image, image, cyanObjects, mask);
        Mat redObjects = new Mat();
        Core.bitwise_and(image, image, redObjects, maskRed);
        Mat allObjects = new Mat();
        Core.bitwise_and(image, image, allObjects, maskRed);

        // Zeige das Originalbild und das Ergebnisbild an
        Imgcodecs.imwrite("Original.jpg", image);
        Imgcodecs.imwrite("NurCyanObjekte.jpg", cyanObjects);
        Imgcodecs.imwrite("Rot.jpg", redObjects);
        Imgcodecs.imwrite("RedAndBlue.jpg", allObjects);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Lade das Bild (ersetze "DeinBild.jpg" durch den tatsächlichen Dateipfad)
        Mat image = Imgcodecs.imread("DeinBild.jpg");
        System.out.println("Funktioniert amk.");

        if (image.empty()) {
            System.out.println("Fehler beim Laden des Bildes.");
            return;
        }

        // Führe die Bildverarbeitung durch
        bb3(image);
    }
}
