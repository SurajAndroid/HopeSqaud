package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 9/9/17.
 */

public class ImageStatus implements Serializable {


    public String UserStatusFileID;
    public String UserStatusFileUrl;
    public String UserName;
    public String FirstName;
    public String LastName;
    public String AddedFrom;

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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getAddedFrom() {
        return AddedFrom;
    }

    public void setAddedFrom(String addedFrom) {
        AddedFrom = addedFrom;
    }

    @Override
    public String toString() {
        return "ImageStatus{" +
                "UserStatusFileID='" + UserStatusFileID + '\'' +
                ", UserStatusFileUrl='" + UserStatusFileUrl + '\'' +
                ", UserName='" + UserName + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", AddedFrom='" + AddedFrom + '\'' +
                '}';
    }
}
