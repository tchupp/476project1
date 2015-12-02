package edu.msu.chuppthe.steampunked.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String PREFERENCE_ID = "SteampunkedPreferences";
    public static final String LOGIN_USERNAME_KEY = "Login_Username";
    public static final String LOGIN_PASSWORD_KEY = "Login_Password";
    public static final String REMEMBER_ME_KEY = "Login_Remember_Me";
    public static final String AUTH_USERNAME_KEY = "Auth_Username";
    public static final String AUTH_TOKEN_KEY = "Auth_Token";

    /**
     * Shared preferences for the app
     */
    private SharedPreferences preferences;

    public Preferences(Context context) {
        this.preferences = context.getSharedPreferences(
                Preferences.PREFERENCE_ID, Context.MODE_PRIVATE);
    }

    public String getLoginUsername() {
        return this.preferences.getString(LOGIN_USERNAME_KEY, "");
    }

    public void setLoginUsername(String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_USERNAME_KEY, username);
        editor.apply();
    }

    public String getLoginPassword() {
        return this.preferences.getString(LOGIN_PASSWORD_KEY, "");
    }

    public void setLoginPassword(String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_PASSWORD_KEY, password);
        editor.apply();
    }

    public boolean getRememberMe() {
        return !this.preferences.getString(REMEMBER_ME_KEY, "").isEmpty();
    }

    public void setRememberMe(boolean rememberMe) {
        String rememberMeString = rememberMe ? "yes" : "";

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REMEMBER_ME_KEY, rememberMeString);
        editor.apply();
    }

    public String getAuthUsername() {
        return this.preferences.getString(AUTH_USERNAME_KEY, "");
    }

    public void setAuthUsername(String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AUTH_USERNAME_KEY, username);
        editor.apply();
    }

    public String getAuthToken() {
        return this.preferences.getString(AUTH_TOKEN_KEY, "");
    }

    public void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }
}
