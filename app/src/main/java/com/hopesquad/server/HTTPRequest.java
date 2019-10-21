package com.hopesquad.server;

import com.google.gson.Gson;
import com.hopesquad.helper.AppConstants;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility class to generate HTTP request strings
 */

public class HTTPRequest {
    public static final String STAGING_URL = AppConstants.BASE_URL;
    public static final String SERVICE_URL = STAGING_URL;

    public static final String IMAGE_URL = SERVICE_URL + "img/";
    public static final String UPLOAD_URL = SERVICE_URL;
    public static final String DEVICE_TYPE = "2";

    public static final String USER_TYPE_ID = "3";

    public static final int URLTYPE_SERVICE = 1;
    public static final int URLTYPE_UPLOAD = 2;
    public static final int URLTYPE_IMAGE = 3;
    public static final int URLTYPE_EXTERNAL = 4;
    public static final boolean ADD_HEADER = true;
    public static final boolean REMOVE_HEADER = true;
    public static final String DAEFAULT_FILTER_ORDER = "season_scheduled_date";
    public static final int URLTYPE_EMR_SERVICE = 5;


    public static String LoginSessionKey;
    public static int SUCCESS = 200;

    public static void setLoginSessionKey(String LoginSessionKey) {
        HTTPRequest.LoginSessionKey = LoginSessionKey;
    }

    /**
     * the constructor
     */
    public HTTPRequest() {
    }


}
