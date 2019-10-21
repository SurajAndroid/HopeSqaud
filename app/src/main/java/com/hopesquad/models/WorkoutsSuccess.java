package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by rohit on 10/9/17.
 */

public class WorkoutsSuccess {

    private String code;
    private String success;
    private String message;
    private ArrayList<Workouts> result;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Workouts> getResult() {
        return result;
    }

    public void setResult(ArrayList<Workouts> result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "WorkoutsSuccess{" +
                "code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
