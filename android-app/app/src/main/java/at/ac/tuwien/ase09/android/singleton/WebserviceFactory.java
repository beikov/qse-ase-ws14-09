package at.ac.tuwien.ase09.android.singleton;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.net.URI;
import java.util.Properties;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.HttpHeaders;

import at.ac.tuwien.ase09.android.interceptor.ClientErrorInterceptorImpl;
import at.ac.tuwien.ase09.android.keycloak.KeyCloak;
import at.ac.tuwien.ase09.rest.OrderResource;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.UserResource;
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

    private static Activity mainContext;

    private static final ResteasyProviderFactory resteasyProviderFactory;

    static {
        ResteasyProviderFactory.setRegisterBuiltinByDefault(true);
        resteasyProviderFactory = ResteasyProviderFactory.getInstance();
        resteasyProviderFactory.addClientErrorInterceptor(new ClientErrorInterceptorImpl());
    }

    private WebserviceFactory(String webserviceEndpointAddress, int connectionTimeout){
        this.webserviceEndpointAddress = webserviceEndpointAddress;
        this.connectionTimeout = connectionTimeout;
    }

    public static void configure(Properties properties, Activity mainContext){
        String webserviceEndpointAddress = properties.getProperty(SERVICE_ENDPOINT_ADDRESS_ARG);
        if(webserviceEndpointAddress == null){
            throw new IllegalArgumentException("Properties do not contain [" + SERVICE_ENDPOINT_ADDRESS_ARG + "]");
        }
        Log.i("WebserviceFactory", "Configured webservice factory for endpoint " + webserviceEndpointAddress);
        int connectionTimeout = Integer.valueOf(properties.getProperty(CONNECTION_TIMEOUT_ARG, "10000"));

        instance = new WebserviceFactory(webserviceEndpointAddress, connectionTimeout);
        WebserviceFactory.mainContext = mainContext;
    }

    private String getAuthToken(){
        AccountManager am = AccountManager.get(mainContext.getApplicationContext());
        Account[] accounts = am.getAccountsByType(KeyCloak.ACCOUNT_TYPE);
        Account account;
        Bundle b = new Bundle();
        Bundle addAccountResult;
        if(accounts.length == 0){
            AccountManagerFuture<Bundle> future = am.addAccount(KeyCloak.ACCOUNT_TYPE, KeyCloak.ACCOUNT_AUTHTOKEN_TYPE, null, null, mainContext, null, null);
            try {
                addAccountResult = future.getResult();
                accounts = am.getAccountsByType(KeyCloak.ACCOUNT_TYPE);
            }catch(Exception e){
                Log.i(WebserviceFactory.class.getName(), "Error during authentication", e);
                throw new RuntimeException(e);
            }
            addAccountResult.getSerializable("afd");
        }
        account = accounts[0];
        UserContext.getInstance().setAccount(account);
        AccountManagerFuture<Bundle> future = am.getAuthToken(account, KeyCloak.ACCOUNT_AUTHTOKEN_TYPE, null, mainContext, null, null);
        try {
            b = future.getResult();
        }catch(Exception e){
            Log.i(WebserviceFactory.class.getName(), "Error during authentication", e);
        }
        return b.getString(AccountManager.KEY_AUTHTOKEN);
    }

    private <T> T getResource(Class<T> clazz, final String authToken){
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);

        // kind of stupid workaround due to contextclassloader issue
        synchronized (resteasyProviderFactory) {

            return ProxyFactory.create(clazz, URI.create(webserviceEndpointAddress), new ApacheHttpClient4Executor(httpParams) {
                @Override
                public ClientResponse execute(ClientRequest request) throws Exception {
                    request.header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                    return super.execute(request);
                }
            }, resteasyProviderFactory);
        }
    }

    public PortfolioResource getPortfolioResource(){
        return getResource(PortfolioResource.class, getAuthToken());
    }

    public ValuePaperResource getValuePaperResource(){
        return getResource(ValuePaperResource.class, getAuthToken());
    }

    public OrderResource getOrderResource(){
        return getResource(OrderResource.class, getAuthToken());
    }

    public UserResource getUserResource(){
        return getResource(UserResource.class, getAuthToken());
    }

    public static WebserviceFactory getInstance(){
        return instance;
    }

    public int getConnectionTimeout(){
        return connectionTimeout;
    }
}
