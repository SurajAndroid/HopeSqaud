package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 9/9/17.
 */

public class FoodPlan implements Serializable {


    public String FoodPlanID;
    public String FileName;
    public String UploadedDate;
    public String UserID;
    public String FoodFileURL;
    public String PlanType;

    public String getFoodPlanID() {
        return FoodPlanID;
    }

    public void setFoodPlanID(String foodPlanID) {
        FoodPlanID = foodPlanID;
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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFoodFileURL() {
        return FoodFileURL;
    }

    public void setFoodFileURL(String foodFileURL) {
        FoodFileURL = foodFileURL;
    }

    public String getPlanType() {
        return PlanType;
    }

    public void setPlanType(String planType) {
        PlanType = planType;
    }

    @Override
    public String toString() {
        return "FoodPlan{" +
                "FoodPlanID='" + FoodPlanID + '\'' +
                ", FileName='" + FileName + '\'' +
                ", UploadedDate='" + UploadedDate + '\'' +
                ", UserID='" + UserID + '\'' +
                ", FoodFileURL='" + FoodFileURL + '\'' +
                ", PlanType='" + PlanType + '\'' +
                '}';
    }
}
