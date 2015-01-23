package at.ac.tuwien.ase09.android.keycloak.util;

import android.accounts.NetworkErrorException;
import android.util.Base64;
import android.util.Log;

import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import at.ac.tuwien.ase09.android.keycloak.KeyCloak;
import at.ac.tuwien.ase09.android.keycloak.KeyCloakAccount;


public final class TokenExchangeUtils {
    private static final String TAG = TokenExchangeUtils.class.getSimpleName();
    private TokenExchangeUtils() {
    }
    public static KeyCloakAccount exchangeForAccessCode(String accessToken, KeyCloak kc) {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("code", accessToken);
        data.put("client_id", kc.getClientId());
        data.put("redirect_uri", kc.getRedirectUri());
        data.put("grant_type", "authorization_code");
        if (kc.getClientSecret() != null && !kc.getClientSecret().isEmpty()) {
            data.put("client_secret", kc.getClientSecret());
        }
        try {
            URL accessTokenEndpoint = new URL(kc.getBaseURL() + "/tokens/access/codes");
            if (kc.getClientSecret() == null || kc.getClientSecret().isEmpty()) {
                accessTokenEndpoint = new URL(kc.getBaseURL() + "/tokens/access/codes?client_id=" + IOUtils.encodeURIComponent(kc.getClientId()));
            }
            String bodyString = getBody(data);
            HttpRestProvider provider = getHttpProvider(kc, accessTokenEndpoint);
            HeaderAndBody result = provider.post(bodyString);
            JSONObject accessResponse = handleResult(result);
            KeyCloakAccount account = new KeyCloakAccount();
            account.extractTokenProperties(accessResponse);
            return account;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static KeyCloakAccount refreshToken(KeyCloakAccount account, KeyCloak kc) throws NetworkErrorException {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("refresh_token", account.getRefreshToken());
        data.put("grant_type", "refresh_token");
        try {
            URL refreshTokenEndpoint = new URL(kc.getBaseURL() + "/tokens/refresh");
            if (kc.getClientSecret() == null || kc.getClientSecret().isEmpty()) {
                refreshTokenEndpoint = new URL(kc.getBaseURL() + "/tokens/refresh?client_id=" + IOUtils.encodeURIComponent(kc.getClientId()));
            }
            String bodyString = getBody(data);
            HttpRestProvider provider = getHttpProvider(kc, refreshTokenEndpoint);
            HeaderAndBody result = provider.post(bodyString);
            JSONObject accessResponse = handleResult(result);
            account.extractTokenProperties(accessResponse);
            return account;
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkErrorException(e);
        }
    }
    private static JSONObject handleResult(HeaderAndBody result) {
        byte[] bodyData = result.getBody();
        String body = new String(bodyData);
        try {
            return new JSONObject(body);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    private static String getBody(Map<String, String> data) {
        final StringBuilder bodyBuilder = new StringBuilder();
        final String formTemplate = "%s=%s";
        String amp = "";
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bodyBuilder.append(amp);
            try {
                bodyBuilder.append(String.format(formTemplate, entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            amp = "&";
        }
        return bodyBuilder.toString();
    }
    private static HttpRestProvider getHttpProvider(KeyCloak kc, URL url) {
        HttpRestProvider provider = new HttpRestProvider(url);
        provider.setDefaultHeader("Content-Type", "application/x-www-form-urlencoded");
        if (kc.getClientSecret() != null && !kc.getClientSecret().isEmpty()) {
            try {
                provider.setDefaultHeader("Authorization", "Basic " + Base64.encodeToString((kc.getClientId() + ":" + kc.getClientSecret()).getBytes("UTF-8"), Base64.DEFAULT | Base64.NO_WRAP));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return provider;
    }
}
