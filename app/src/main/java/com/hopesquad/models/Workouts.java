package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by rohit on 10/9/17.
 */

public class Workouts {

    private String WorkOutName;
    private String UserID;
    private String DayName;
    private String DayID;
    private ArrayList<UserExercise> UserExerciseList;

    public String getWorkOutName() {
        return WorkOutName;
    }

    public void setWorkOutName(String workOutName) {
        WorkOutName = workOutName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getDayName() {
        return DayName;
    }

    public void setDayName(String dayName) {
        DayName = dayName;
    }

    public String getDayID() {
        return DayID;
    }

    public void setDayID(String dayID) {
        DayID = dayID;
    }

    public ArrayList<UserExercise> getUserExerciseList() {
        return UserExerciseList;
    }

    public void setUserExerciseList(ArrayList<UserExercise> userExerciseList) {
        UserExerciseList = userExerciseList;
    }
}
