package at.ac.tuwien.ase09.android.singleton;

import android.util.Log;

import java.util.Properties;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.ValuePaperResource;

/**
 * Created by Moritz on 27.10.2014.
 */
public class WebserviceFactory {
    private final static String SERVICE_ENDPOINT_ADDRESS_ARG = "service-endpoint-address";
    public final static String CONNECTION_TIMEOUT_ARG = "connection-timeout";
    private static WebserviceFactory instance;

    private final String webserviceEndpointAddress;
    private final int connectionTimeout;

    static {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    private WebserviceFactory(String webserviceEndpointAddress, int connectionTimeout){
        this.webserviceEndpointAddress = webserviceEndpointAddress;
        this.connectionTimeout = connectionTimeout;
    }

    public static void configure(Properties properties){
        String webserviceEndpointAddress = properties.getProperty(SERVICE_ENDPOINT_ADDRESS_ARG);
        if(webserviceEndpointAddress == null){
            throw new IllegalArgumentException("Properties do not contain [" + SERVICE_ENDPOINT_ADDRESS_ARG + "]");
        }
        Log.i("WebserviceFactory", "Configured webservice factory for endpoint " + webserviceEndpointAddress);
        int connectionTimeout = Integer.valueOf(properties.getProperty(CONNECTION_TIMEOUT_ARG, "10000"));

        instance = new WebserviceFactory(webserviceEndpointAddress, connectionTimeout);
    }

    private <T> T getResource(Class<T> clazz){
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
        return ProxyFactory.create(clazz, webserviceEndpointAddress, new ApacheHttpClient4Executor(httpParams));
    }

    public PortfolioResource getPortfolioResource(){
        return getResource(PortfolioResource.class);
    }

    public ValuePaperResource getValuePaperResource(){
        return getResource(ValuePaperResource.class);
    }

    public static WebserviceFactory getInstance(){
        return instance;
    }

    public int getConnectionTimeout(){
        return connectionTimeout;
    }
}
