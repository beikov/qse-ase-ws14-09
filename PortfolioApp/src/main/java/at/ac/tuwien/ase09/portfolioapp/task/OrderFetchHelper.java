package at.ac.tuwien.ase09.portfolioapp.task;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class OrderFetchHelper {
    /* TODO, add proper url */
    private static final String fetchUrl = "";
    private static final int HTTP_STATUS_OK = 200;
    private static byte[] buff = new byte[1024];

    public static class ApiException extends Exception {
        private static final long serialVersionUID = 1L;
        public ApiException (String msg)
        {
            super (msg);
        }
        public ApiException (String msg, Throwable thr)
        {
            super (msg, thr);
        }
    }

    protected static synchronized String downloadFromServer (String... params)
            throws ApiException
    {
        String retrieve = null;
        String id = params[0];
        String url = fetchUrl + "&id=" + id;

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {

            HttpResponse response = client.execute(request);
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {

                throw new ApiException("Invalid response from server" +
                        status.toString());
            }

            HttpEntity entity = response.getEntity();
            InputStream ist = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            int readCount = 0;
            while ((readCount = ist.read(buff)) != -1) {
                content.write(buff, 0, readCount);
            }
            retrieve = new String (content.toByteArray());
        } catch (Exception e) {
            throw new ApiException("Problem connecting to the server " +
                    e.getMessage(), e);
        }
        return retrieve;
    }
}