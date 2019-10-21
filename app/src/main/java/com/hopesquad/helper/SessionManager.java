package com.hopesquad.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.hopesquad.models.UserDetail;
import com.hopesquad.models.UserInfo;
import com.hopesquad.onboarding.LoginActivity;
import com.hopesquad.onboarding.SplashActivity;

/**
 * Created by rohit on 9/9/17.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "Hope_Squad_Pref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String USER_DETAIL = "UserDetail";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(UserDetail userDetail) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        Gson gson = new Gson();
        String json = gson.toJson(userDetail);
        editor.putString(USER_DETAIL, json);

        // commit changes
        editor.commit();
    }

    /**
     * Create login session
     */
    public void createUserLoginSession(UserInfo userDetail) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        Gson gson = new Gson();
        String json = gson.toJson(userDetail);
        editor.putString(USER_DETAIL, json);
        // commit changes
        editor.commit();
    }


    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Check login method wil check user login active_status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login active_status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            ((Activity) _context).finish();
        }

    }


    /**
     * Clear session details
     */
    public void logoutUser() {

        // Logout from Facebook
        LoginManager.getInstance().logOut();

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, SplashActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    /**
     * Save User Detail
     */
    public void saveUserDetail(UserDetail userDetail) {
        Gson gson = new Gson();
        String json = gson.toJson(userDetail);
        editor.putString(USER_DETAIL, json);
        editor.commit();
    }

    /**
     * Get UserDetail
     */
    public UserDetail getUserDetail() {
        Gson gson = new Gson();
        String json = pref.getString(USER_DETAIL, "");
        UserDetail userDetail = gson.fromJson(json, UserDetail.class);
        return userDetail;
    }

    /**
     * Get UserDetail
     */
    public UserInfo getUserInfo() {
        Gson gson = new Gson();
        String json = pref.getString(USER_DETAIL, "");
        UserInfo userDetail = gson.fromJson(json, UserInfo.class);
        return userDetail;
    }
}
