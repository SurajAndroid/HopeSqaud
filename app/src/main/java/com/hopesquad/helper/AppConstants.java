package com.hopesquad.helper;


public class AppConstants {

    /* API Base Url*/

    // Production Url
//    public static final String BASE_URL = "http://app.hopesquad.dk/HopeSquadService/HopeSquadservice.svc/";

    // Test Server Url
//    public static final String BASE_URL = "http://app.hopesquad.dk/HSServiceTest/HopeSquadService.svc/";

//  public static final String BASE_URL = "http://services.iosappexpertise.com/HopeSquadService.svc/";
//    public static final String BASE_URL = "http://192.168.1.5/HopeSquadService/HopeSquadService.svc/";

    public static final String BASE_URL = "http://app.hopesquad.dk/api/";

    // Methods
    public static final String LOGIN = "AuthenticateUserLogin";
    public static final String INSERT_USER_DETAIL = "InsertUserDetail";
    public static final String GET_USER_STATUS_NEW = "GetAccountStatus";
    public static final String GET_ALL_USER_IMAGES = "GetAllUserImages";
    public static final String GET_ALL_GALLERY_IMAGES = "getAllGalleryImages";
    public static final String GET_ALL_USER_MEDIA = "GetAllUsers";
    public static final String GET_FOOD_PLAN = "getFoodPlan";
    public static final String GET_USERS_WORKOUT = "GetUsersWorkOut";
    public static final String UPDATE_USER_EXCERCISE_INFO = "UpdateUserExerciseInfo";
    public static final String GET_ALL_USERS_MESSAGES = "GetAllUsersMessages";
    public static final String GET_ALL_USERS = "GetAllUsers";
    public static final String INSERT_MESSAGE = "InsertMessageInfo";
    public static final String GET_ALL_USER_STATUS = "GetAllUsersStatus";
    public static final String UPLOAD_FILE = "UploadFile";//"UploadProfilePhoto";//"UploadFile";

    /**
     * Get Final API URL
     *
     * @param methodUrl API Method Name
     */
    public static String getMethodUrl(String methodUrl) {
        return BASE_URL + methodUrl;
    }
}