package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 9/9/17.
 */

public class UserInfo implements Serializable {

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
    public String TrainerUserID;
    public String UserID;
    public String UserName;
    public String UserSessionID;
    public String UserStatus;
    public String UserType;


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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLogoPath() {
        return LogoPath;
    }

    public void setLogoPath(String logoPath) {
        LogoPath = logoPath;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
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

    public String getTrainerUserID() {
        return TrainerUserID;
    }

    public void setTrainerUserID(String trainerUserID) {
        TrainerUserID = trainerUserID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "BackgorundPath='" + BackgorundPath + '\'' +
                ", DeviceToken='" + DeviceToken + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                ", Email='" + Email + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Phone='" + Phone + '\'' +
                ", LogoPath='" + LogoPath + '\'' +
                ", Password='" + Password + '\'' +
                ", ProfilePhoto='" + ProfilePhoto + '\'' +
                ", TrainerUserID='" + TrainerUserID + '\'' +
                ", UserID='" + UserID + '\'' +
                ", UserName='" + UserName + '\'' +
                ", UserSessionID='" + UserSessionID + '\'' +
                ", UserStatus='" + UserStatus + '\'' +
                ", UserType='" + UserType + '\'' +
                '}';
    }
}
