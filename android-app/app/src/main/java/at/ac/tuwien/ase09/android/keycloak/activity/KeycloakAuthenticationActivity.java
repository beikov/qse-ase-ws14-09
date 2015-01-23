package at.ac.tuwien.ase09.android.keycloak.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.security.Key;

import at.ac.tuwien.ase09.android.R;
import at.ac.tuwien.ase09.android.keycloak.KeyCloak;
import at.ac.tuwien.ase09.android.keycloak.KeyCloakAccount;
import at.ac.tuwien.ase09.android.keycloak.token.AccessTokenExchangeLoader;
import at.ac.tuwien.ase09.android.keycloak.util.IOUtils;


public class KeycloakAuthenticationActivity extends AccountAuthenticatorActivity implements LoaderManager.LoaderCallbacks<KeyCloakAccount> {

    private KeyCloak kc;
    private static final String ACCESS_TOKEN_KEY = "accessToken";

    public static final int LOGIN_OK = 0;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        kc = new KeyCloak(this);
        Account[] accounts = AccountManager.get(this.getApplicationContext()).getAccountsByType(KeyCloak.ACCOUNT_TYPE);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public Loader<KeyCloakAccount> onCreateLoader(int i, Bundle bundle) {
        return new AccessTokenExchangeLoader(this, bundle.getString(ACCESS_TOKEN_KEY));
    }

    @Override
    public void onLoadFinished(Loader<KeyCloakAccount> keyCloakAccountLoader, KeyCloakAccount keyCloakAccount) {
        AccountAuthenticatorResponse response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        String keyCloakAccountJson = new Gson().toJson(keyCloakAccount);
        Bundle accountBundle = new Bundle();
        accountBundle.putString(KeyCloak.ACCOUNT_KEY, keyCloakAccountJson);


        AccountManager am = AccountManager.get(this.getApplicationContext());
        Account androidAccount = new Account(keyCloakAccount.getPreferredUsername(), KeyCloak.ACCOUNT_TYPE);
        Account[] accounts = am.getAccountsByType(KeyCloak.ACCOUNT_TYPE);
        for (Account existingAccount : accounts) {
            if (existingAccount.name == androidAccount.name) {
                am.setUserData(androidAccount, KeyCloak.ACCOUNT_KEY, keyCloakAccountJson);
                if (response != null) {
                    response.onResult(accountBundle);
                }
                finish();
            }
        }

        am.removeAccount(androidAccount, null, null);
        am.addAccountExplicitly(androidAccount, null, accountBundle);

        if (response != null) {
            response.onResult(accountBundle);
        }

        finish();


    }

    @Override
    public void onLoaderReset(Loader<KeyCloakAccount> keyCloakAccountLoader) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private KeyCloak kc;
        private WebView webView;

        public PlaceholderFragment() {

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (kc == null) {
                kc = new KeyCloak(activity);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.keycloak_login_fragment, container, false);
            webView = (WebView) rootView.findViewById(R.id.webview);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.i("WEBVIEW", url);
                    if (url.contains("code=")) {
                        final String token = IOUtils.fetchToken(url);
                        Log.d("TOKEN", token);

                        Bundle data = new Bundle();
                        data.putString(ACCESS_TOKEN_KEY, token);
                        getLoaderManager().initLoader(1, data, (LoaderManager.LoaderCallbacks) (getActivity())).forceLoad();

                        return true;
                    }

                    return false;
                }


            });
            webView.loadUrl(kc.createLoginUrl());
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
        }
    }
}
