package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 9/9/17.
 */

public class UserDetail implements Serializable {

    public String BackgorundPath;
    public String DeviceToken;
    public String DisplayName;
    public String Email;
    public String FirstName;
    public String LastName;
    public String Phone;
    public String LogoPath;
    public String Password;

    public String ProfilePhoto;
    public int TrainerUserID;
    public int UserID;
    public String UserName;
    public String UserSessionID;
    public int UserStatus;
    public int UserType;

    public String message;
    public int code;
    public boolean success;



    public UserDetail() {
    }


    public int getTrainerUserID() {
        return TrainerUserID;
    }

    public void setTrainerUserID(int trainerUserID) {
        TrainerUserID = trainerUserID;
    }

    public String getBackgorundPath() {
        return BackgorundPath;
    }

    public void setBackgorundPath(String backgorundPath) {
        BackgorundPath = backgorundPath;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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

    public String getLogoPath() {
        return LogoPath;
    }

    public void setLogoPath(String logoPath) {
        LogoPath = logoPath;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }



    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserSessionID() {
        return UserSessionID;
    }

    public void setUserSessionID(String userSessionID) {
        UserSessionID = userSessionID;
    }

    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int userType) {
        UserType = userType;
    }
}
