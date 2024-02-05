import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * A simple Java REST GET example using the Apache HTTP library.
 * This executes a call against the Yahoo Weather API service, which is
 * actually an RSS service (http://developer.yahoo.com/weather/).
 *
 * Try this Twitter API URL for another example (it returns JSON results):
 * http://search.twitter.com/search.json?q=%40apple
 * (see this URL for more Twitter info: https://dev.twitter.com/docs/using-search)
 *
 * Apache HttpClient: http://hc.apache.org/httpclient-3.x/
 *
 */
public class LarsJava {

    public static void main(String[] args) {
        try {
            // create a CloseableHttpClient
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // specify the host, protocol, and port
            HttpGet getRequest = new HttpGet("192.168.0.2");

            System.out.println("executing request to " + getRequest.getURI());

            // execute the request
            HttpResponse httpResponse = httpclient.execute(getRequest);
            HttpEntity entity = httpResponse.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(httpResponse.getStatusLine());
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                System.out.println(header);
            }
            System.out.println("----------------------------------------");

            // print the response body
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
