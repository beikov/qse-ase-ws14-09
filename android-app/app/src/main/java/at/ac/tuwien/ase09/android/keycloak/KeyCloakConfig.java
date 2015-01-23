package at.ac.tuwien.ase09.android.keycloak;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.keycloak.util.IOUtils;
import at.ac.tuwien.ase09.android.keycloak.util.ObjectUtils;

/**
 * Created by Moritz on 19.01.2015.
 */
public final class KeyCloakConfig {

    private static final String TAG = KeyCloakConfig.class.getSimpleName().toUpperCase();

    public final String realmUrl;
    public final String authServerUrl;
    public final String realm;
    public final String clientId;
    public final String clientSecret;

    private static KeyCloakConfig instace;

    private KeyCloakConfig(Context context) {
        InputStream fileStream = context.getResources().openRawResource(R.raw.keycloak);
        String configText = IOUtils.getString(fileStream);
        IOUtils.close(fileStream);

        try {
            JSONObject configFile = new JSONObject(configText);
            realm = configFile.getString("realm");
            authServerUrl = configFile.getString("auth-server-url");
            realmUrl = authServerUrl + "/realms/" + URLEncoder.encode(realm, "UTF-8");
            clientId = configFile.getString("resource");
            clientSecret = ObjectUtils.getOrDefault(configFile.optJSONObject("credentials"), new JSONObject()).optString("secret");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("You don't support UTF-8", e);
        }
    }

    public static synchronized KeyCloakConfig getInstance(Context context) {
        if (instace == null) {
            instace = new KeyCloakConfig(context);
        }
        return  instace;
    }



}