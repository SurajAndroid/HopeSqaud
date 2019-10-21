package com.hopesquad.models;

/**
 * Created by rohit on 24/9/17.
 */

public class UserImages {
    private String FileName;
    private String Note;
    private String UploadedDate;
    private int UserFileID;
    private int UserID;
    private String UserImageFilePath;
    private String UserImageFileURL;
    private int UserStatus;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getUploadedDate() {
        return UploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        UploadedDate = uploadedDate;
    }

    public int getUserFileID() {
        return UserFileID;
    }

    public void setUserFileID(int userFileID) {
        UserFileID = userFileID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserImageFilePath() {
        return UserImageFilePath;
    }

    public void setUserImageFilePath(String userImageFilePath) {
        UserImageFilePath = userImageFilePath;
    }

    public String getUserImageFileURL() {
        return UserImageFileURL;
    }

    public void setUserImageFileURL(String userImageFileURL) {
        UserImageFileURL = userImageFileURL;
    }

    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }
}
