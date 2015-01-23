package at.ac.tuwien.ase09.keycloak;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.keycloak.KeycloakPrincipal;
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
	private static final String APP_NAME = "protected";
	private static final String CONTEXT_PATH = "/auth";
	
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
		KeycloakSecurityContext session = ((KeycloakPrincipal) request.getUserPrincipal()).getKeycloakSecurityContext();
		
		if (session == null) {
			return null;
		}
		
		User user = new User();
		user.setUsername(session.getIdToken().getPreferredUsername());
		
		return user;
	}

    public static String getCurrentUserName(HttpServletRequest req) {
    	KeycloakSecurityContext session = ((KeycloakPrincipal) req.getUserPrincipal()).getKeycloakSecurityContext();
    	return session.getIdToken().getPreferredUsername();
    }

    public static UserInfo getCurrentUser(HttpServletRequest req) throws Failure {
    	KeycloakSecurityContext session = ((KeycloakPrincipal) req.getUserPrincipal()).getKeycloakSecurityContext();
    	
    	if (session == null) {
        	return null;
        }
    	
    	String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        
    	IDToken token;
    	if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
    		token = session.getToken();
    	}else{
    		token = session.getIdToken();
    	}
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
    	KeycloakSecurityContext session = ((KeycloakPrincipal) req.getUserPrincipal()).getKeycloakSecurityContext();
    	
        HttpClient client = new HttpClientBuilder()
                .disableTrustManager().build();
        try {
        	StringBuilder uriBuilder = getBase(req);
        	uriBuilder.append("/admin/realms/");
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
    
    public static String getLoginUrl(HttpServletRequest req) {
    	String host = UriUtils.getOrigin(req.getRequestURL().toString());
    	return getBase(host).append("/realms/").append(APP_REALM).append("/tokens/login")
    			.append("?redirect_uri=").append(getEncodedParam(host + "/protected"))
    			.append("&client_id=").append(APP_NAME).toString();
    }
    
    public static String getLogoutUrl(HttpServletRequest req) {
    	String host = UriUtils.getOrigin(req.getRequestURL().toString());
    	return getBase(host).append("/realms/").append(APP_REALM).append("/tokens/logout")
    			.append("?redirect_uri=").append(getEncodedParam(host)).toString();
    }
    
    public static String getRegisterUrl(HttpServletRequest req) {
    	String host = UriUtils.getOrigin(req.getRequestURL().toString());
    	return getBase(host).append("/realms/").append(APP_REALM).append("/tokens/registrations")
    			.append("?redirect_uri=").append(getEncodedParam(host + "/protected"))
    			.append("&client_id=").append(APP_NAME).toString();
    }
    
    private static StringBuilder getBase(HttpServletRequest req) {
    	return getBase(UriUtils.getOrigin(req.getRequestURL().toString()));
    }
    
    private static StringBuilder getBase(String host) {
    	StringBuilder uriBuilder = new StringBuilder();
    	uriBuilder.append(host);
    	uriBuilder.append(CONTEXT_PATH);
    	return uriBuilder;
    }
    
    private static String getEncodedParam(String param) {
    	try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
}
