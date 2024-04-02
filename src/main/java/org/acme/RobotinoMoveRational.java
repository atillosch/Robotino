package org.acme;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
//import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RobotinoMoveRational {

    public static void main(String[] args) {
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
