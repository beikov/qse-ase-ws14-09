package at.ac.tuwien.ase09.keycloak;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.ServiceUrlConstants;
import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;
import org.keycloak.util.KeycloakUriBuilder;
import org.keycloak.util.UriUtils;

import at.ac.tuwien.ase09.model.User;

/**
 * A client that is used to access user information of the authentication server.
 * 
 * REST API documentation: http://docs.jboss.org/keycloak/docs/1.0.4.Final/rest-api/overview-index.html
 * 
 * @author Christian Beikov
 */
public class AdminClient {
	
	private static final String APP_REALM = "portfolio-web";
	
    public static class Failure extends Exception {
        private int status;

        public Failure(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
	
	public static User createUser(HttpServletRequest request) {
		KeycloakSecurityContext session = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		
		if (session == null) {
			return null;
		}
		
		User user = new User();
		user.setUsername(session.getIdToken().getPreferredUsername());
		
		return user;
	}

    public static String getCurrentUserName(HttpServletRequest req) {
        KeycloakSecurityContext session = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    	return session.getIdToken().getPreferredUsername();
    }

    public static UserInfo getCurrentUser(HttpServletRequest req) throws Failure {
        KeycloakSecurityContext session = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    	
        if (session == null) {
        	return null;
        }
        
        IDToken token = session.getIdToken();
        String username = token.getPreferredUsername();
        String firstName = token.getName();
        String lastName = token.getFamilyName();
        String email = token.getEmail();

        UserInfo userInfo = new UserInfo(username, firstName, lastName, email);
        return userInfo;
        
//        HttpClient client = new HttpClientBuilder()
//                .disableTrustManager().build();
//        try {
//        	StringBuilder uriBuilder = new StringBuilder();
//        	uriBuilder.append(UriUtils.getOrigin(req.getRequestURL().toString()));
//        	uriBuilder.append("/auth/admin/realms/");
//        	uriBuilder.append(APP_REALM);
//        	uriBuilder.append("/users/");
//        	uriBuilder.append(session.getIdToken().getPreferredUsername());
//        	
//            HttpGet get = new HttpGet(uriBuilder.toString());
//            get.addHeader("Authorization", "Bearer " + session.getTokenString());
//            
//            try {
//                HttpResponse response = client.execute(get);
//                if (response.getStatusLine().getStatusCode() != 200) {
//                    throw new Failure(response.getStatusLine().getStatusCode());
//                }
//                HttpEntity entity = response.getEntity();
//                InputStream is = entity.getContent();
//                try {
//                    return JsonSerialization.readValue(is, UserRepresentation.class);
//                } finally {
//                    is.close();
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } finally {
//            client.getConnectionManager().shutdown();
//        }
    }

    public static void updateCurrentUser(HttpServletRequest req, UserRepresentation user) throws Failure {
        KeycloakSecurityContext session = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    	
        HttpClient client = new HttpClientBuilder()
                .disableTrustManager().build();
        try {
        	StringBuilder uriBuilder = new StringBuilder();
        	uriBuilder.append(UriUtils.getOrigin(req.getRequestURL().toString()));
        	uriBuilder.append("/auth/admin/realms/");
        	uriBuilder.append(APP_REALM);
        	uriBuilder.append("/users/");
        	uriBuilder.append(session.getIdToken().getPreferredUsername());
        	
            HttpPut put = new HttpPut(uriBuilder.toString());
            put.addHeader("Authorization", "Bearer " + session.getTokenString());
            
            try {
                put.setEntity(new ByteArrayEntity(JsonSerialization.writeValueAsBytes(user)));
                
                HttpResponse response = client.execute(put);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Failure(response.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
    
    public static String getLogoutUrl(HttpServletRequest req) throws Failure {
    	String host = UriUtils.getOrigin(req.getRequestURL().toString());
    	return KeycloakUriBuilder.fromUri("/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
    			.host(host).queryParam("redirect_uri", host).toString();
    }
}
