package org.acme;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RobotinoMoveForwardtillAbstand {

    public static void main(String[] args) {
        try {
            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the host, protocol, and port
            String apiUrl = "http://192.168.0.1/data/omnidrive";

            // specify the initial velocity values for forward motion
            double vx = 0.5;  // x-velocity in m/s
            double vy = 0.0;  // y-velocity in m/s
            double omega = 0.0;  // rotational velocity in rad/s

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

            // Monitor sensor data and stop when an obstacle is detected within 0.1 meters
            while (true) {
                // Hier müsstest du die Logik für die Überwachung der Sensorwerte implementieren
                // Beispiel: Rufe die Daten des Distanzsensorarrays ab und überprüfe die Entfernungen
                // Wenn eine Entfernung kleiner als 0,1 Meter ist, breche die Schleife ab und stoppe den Robotino
                // Achte darauf, die Geschwindigkeitswerte entsprechend anzupassen, um den Robotino zu stoppen.

                // Beispiel:
                /*
                double[] distanceSensorValues = getDistanceSensorValues(); // Funktion, um die Sensorwerte zu erhalten

                if (distanceSensorValues[0] < 0.1) {
                    // Hindernis in der Nähe, stoppe den Robotino
                    vx = 0.0;
                    vy = 0.0;
                    omega = 0.0;
                    jsonPayload = String.format("[%s, %s, %s]", vx, vy, omega);
                    putRequest.setEntity(new StringEntity(jsonPayload));
                    httpResponse = httpclient.execute(putRequest);
                    break;
                }
                */

                // Füge eine kurze Pause ein, um den Robotino nicht zu überlasten
                Thread.sleep(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion, um die Distanzsensordaten abzurufen (Beispiel)
    private static double[] getDistanceSensorValues() {
        // Hier müsstest du die Logik für die Abfrage der Sensorwerte implementieren
        // Beispiel: Rufe die Daten des Distanzsensorarrays ab und gib die Werte zurück
        // Achtung: Diese Funktion muss an deine spezifische API und Datenstruktur angepasst werden
        return new double[]{0.2, 0.3, 0.25}; // Beispielwerte
    }
}
