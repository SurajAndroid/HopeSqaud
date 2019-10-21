package com.hopesquad;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Suraj Shakya on 2018/05/17.
 */


public class ManageSession {

    private static final String PREFS_NAME = "Hope";
    private static final String LOGIN_PREFS = "Login_Status";
    private static final String REMEMBER_PREFS = "_remember";
    public static SharedPreferences settings;
    public static SharedPreferences.Editor editor;

    private static final String SESSION_NAME = "CAMERAIMAGE";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    public Uri getImageUri() {
        String imageUri = mSharedPreferences.getString("getImageUri", "");
        if (imageUri == null || imageUri.equals("")) return null;
        return Uri.parse(imageUri);
    }

    public void setImageUri(Uri imageUri) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImageUri", imageUri.toString());
        prefsEditor.commit();
    }

    public String getImagePath() {
        return mSharedPreferences.getString("getImagePath", "");
    }

    public void setImagePath(String imagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImagePath", imagePath);
        prefsEditor.commit();
    }

    public String getCropImagePath() {
        return mSharedPreferences.getString("getCropImagePath", "");
    }

    public void setCropImagePath(String cropImagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getCropImagePath", cropImagePath);
        prefsEditor.commit();
    }

    ManageSession(Context context) {
        mSharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        prefsEditor = mSharedPreferences.edit();
    }

    /*Set Login Prefrence*/
    public static boolean setLoginPreference(Context context, String key, String value) {
        settings = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);

        return editor.commit();
    }

    /*Get Login Prefrence*/
    public static String getLoginPreference(Context context, String key) {
        settings = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    /*Set App prefrences*/
    public static boolean setPreference(Context context, String key, String value) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /*Get App Prefrences*/
    public static String getPreference(Context context, String key) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    /*Clear App Prefrences*/
    public static void getClearPreference(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    /*Clear Login Prefrences*/
    public static void getClearLogout(Context context) {
        settings = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    /*Set App prefrences*/
    public static boolean setRememberPreference(Context context, String key, String value) {
        settings = context.getSharedPreferences(REMEMBER_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /*Get App Prefrences*/
    public static String getRememberPreference(Context context, String key) {
        settings = context.getSharedPreferences(REMEMBER_PREFS, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    /*Clear Login Prefrences*/
    public static void ClearRememberPrefrence(Context context) {
        settings = context.getSharedPreferences(REMEMBER_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }


    public static String printHashKey(Context pContext) {
        String hashKey = null;
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("", "printHashKey()", e);
        }
        return hashKey;
    }

}
