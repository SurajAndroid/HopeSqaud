package com.hopesquad.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by pc on 24/01/2018.
 */

public class UserMediaList {

    private String UserStatusFileID;
    private String UserStatusFileUrl;

    public String getUserStatusFileID() {
        return UserStatusFileID;
    }

    public void setUserStatusFileID(String userStatusFileID) {
        UserStatusFileID = userStatusFileID;
    }

    public String getUserStatusFileUrl() {
        return UserStatusFileUrl;
    }

    public void setUserStatusFileUrl(String userStatusFileUrl) {
        UserStatusFileUrl = userStatusFileUrl;
    }

    @Override
    public String toString() {
        return "UserMediaList{" +
                "UserStatusFileID='" + UserStatusFileID + '\'' +
                ", UserStatusFileUrl='" + UserStatusFileUrl + '\'' +
                '}';
    }
}
