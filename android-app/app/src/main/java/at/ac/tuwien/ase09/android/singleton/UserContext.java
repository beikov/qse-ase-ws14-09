package at.ac.tuwien.ase09.android.singleton;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

/**
 * Created by Moritz on 20.01.2015.
 */
public class UserContext {
    private  static UserContext instance;

    private String authToken;
    private Account account;

    private UserContext(){}

    public static UserContext getInstance(){
        if(instance == null){
            instance = new UserContext();
        }
        return instance;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
