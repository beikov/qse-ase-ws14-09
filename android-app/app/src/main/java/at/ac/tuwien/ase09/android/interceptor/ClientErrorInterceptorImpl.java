package at.ac.tuwien.ase09.android.interceptor;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;

import java.io.InputStream;

/**
 * Created by Moritz on 22.01.2015.
 */
public class ClientErrorInterceptorImpl implements ClientErrorInterceptor
{
    public void handle(ClientResponse response) throws RuntimeException
    {
        throw new RuntimeException("ERROR response status: " + response.getStatus());
    }
}
