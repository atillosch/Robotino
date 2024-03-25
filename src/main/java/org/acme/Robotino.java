package org.acme;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Robotino {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Bild aus Datei laden
        Mat image = Imgcodecs.imread("path/to/your/image.jpg");

        // Konvertiere das Bild in den HSV-Farbraum
        Mat hsv_image = new Mat();
        Imgproc.cvtColor(image, hsv_image, Imgproc.COLOR_BGR2HSV);

        // Definiere den Farbbereich für Cyan in HSV
        // Hinweis: Diese Werte können je nach Schattierung von Cyan, die du erkennen möchtest, angepasst werden
        Scalar lower_cyan = new Scalar(80, 50, 50);
        Scalar upper_cyan = new Scalar(100, 255, 255);
        Scalar lower_red = new Scalar(0, 100, 100);
        Scalar upper_red = new Scalar(255, 200, 200);

        // Erstelle eine Maske, die nur Farben innerhalb des Cyan-Bereichs zeigt
        Mat mask = new Mat();
        Mat mask_red = new Mat();
        Core.inRange(hsv_image, lower_cyan, upper_cyan, mask);
        Core.inRange(hsv_image, lower_red, upper_red, mask_red);

        // Wende die Maske auf das Originalbild an, um nur die cyanfarbenen Teile zu zeigen
        Mat cyan_objects = new Mat();
        Mat red_objects = new Mat();
        Core.bitwise_and(image, image, cyan_objects, mask);
        Core.bitwise_and(image, image, red_objects, mask_red);

        // Speichere die Bilder in das Verzeichnis "C:\Users\catil\OneDrive\abdul realschule\Bilder\BilderRobotino"
        Imgcodecs.imwrite("C:\\Users\\catil\\OneDrive\\abdul realschule\\Bilder\\BilderRobotino\\cyan_objects.jpg", cyan_objects);
        Imgcodecs.imwrite("C:\\Users\\catil\\OneDrive\\abdul realschule\\Bilder\\BilderRobotino\\red_objects.jpg", red_objects);

        // Zeige das Originalbild und das Ergebnisbild an
        HighGui.imshow("Original", image);
        HighGui.imshow("Nur Cyan Objekte", cyan_objects);
        HighGui.imshow("Rot", red_objects);

        // Warte auf einen Tastendruck und schließe die Fenster
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
}