package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 10/9/17.
 */

public class UserExercise implements Serializable {

    private String WorkOutName;
    private String ExerciseID;
    private String ExerciseImageURL;
    private String ExerciseName;
    private String ExerciseStatus;
    private String Sets;
    private String Reps;
    private String RestInSec;
    private String StartWeight;
    private String EndWeight;
    private String Comment;
    private String UserID;
    private String LinkName;
    private String LinkURL;
    private String WorkOutImageURL;


    public String getWorkOutName() {
        return WorkOutName;
    }

    public void setWorkOutName(String workOutName) {
        WorkOutName = workOutName;
    }

    public String getExerciseID() {
        return ExerciseID;
    }

    public void setExerciseID(String exerciseID) {
        ExerciseID = exerciseID;
    }

    public String getExerciseImageURL() {
        return ExerciseImageURL;
    }

    public void setExerciseImageURL(String exerciseImageURL) {
        ExerciseImageURL = exerciseImageURL;
    }

    public String getExerciseName() {
        return ExerciseName;
    }

    public void setExerciseName(String exerciseName) {
        ExerciseName = exerciseName;
    }

    public String getExerciseStatus() {
        return ExerciseStatus;
    }

    public void setExerciseStatus(String exerciseStatus) {
        ExerciseStatus = exerciseStatus;
    }

    public String getSets() {
        return Sets;
    }

    public void setSets(String sets) {
        Sets = sets;
    }

    public String getReps() {
        return Reps;
    }

    public void setReps(String reps) {
        Reps = reps;
    }

    public String getRestInSec() {
        return RestInSec;
    }

    public void setRestInSec(String restInSec) {
        RestInSec = restInSec;
    }

    public String getStartWeight() {
        return StartWeight;
    }

    public void setStartWeight(String startWeight) {
        StartWeight = startWeight;
    }

    public String getEndWeight() {
        return EndWeight;
    }

    public void setEndWeight(String endWeight) {
        EndWeight = endWeight;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getLinkName() {
        return LinkName;
    }

    public void setLinkName(String linkName) {
        LinkName = linkName;
    }

    public String getLinkURL() {
        return LinkURL;
    }

    public void setLinkURL(String linkURL) {
        LinkURL = linkURL;
    }

    public String getWorkOutImageURL() {
        return WorkOutImageURL;
    }

    public void setWorkOutImageURL(String workOutImageURL) {
        WorkOutImageURL = workOutImageURL;
    }
}
