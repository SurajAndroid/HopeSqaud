package com.hopesquad.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rohit on 9/9/17.
 */

public class UserImage implements Serializable {


    public String UserFileID;
    public String FileName;
    public String UploadedDate;
    public String AddedFrom;
    public String UserID;
    public String ImageFileURL;

    public String getUserFileID() {
        return UserFileID;
    }

    public void setUserFileID(String userFileID) {
        UserFileID = userFileID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getUploadedDate() {
        return UploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        UploadedDate = uploadedDate;
    }

    public String getAddedFrom() {
        return AddedFrom;
    }

    public void setAddedFrom(String addedFrom) {
        AddedFrom = addedFrom;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getImageFileURL() {
        return ImageFileURL;
    }

    public void setImageFileURL(String imageFileURL) {
        ImageFileURL = imageFileURL;
    }


    @Override
    public String toString() {
        return "UserImage{" +
                "UserFileID='" + UserFileID + '\'' +
                ", FileName='" + FileName + '\'' +
                ", UploadedDate='" + UploadedDate + '\'' +
                ", AddedFrom='" + AddedFrom + '\'' +
                ", UserID='" + UserID + '\'' +
                ", ImageFileURL='" + ImageFileURL + '\'' +
                '}';
    }
}
