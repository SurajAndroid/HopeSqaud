package com.hopesquad.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveSharedPref {

	private static final String PREFRENCE_NAME = "trifidagro";
	public static final String GCM_TOKEN= "token";
	public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

	public static void saveData(Context context, String key, String data) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, data);
		editor.commit();
	}

	public static void saveIntData(Context context, String key, int data) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, data);
		editor.commit();
	}

	public static int getIntData(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}

	public static String getData(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}

	public static void clearData(Context context) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}



}
