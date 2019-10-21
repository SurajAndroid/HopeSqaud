package com.hopesquad.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rohit on 9/9/17.
 */

public class FoodPlanSuccess implements Serializable {


    public String message;
    public String code;
    public String success;
    public ArrayList<FoodPlan> result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public ArrayList<FoodPlan> getResult() {
        return result;
    }

    public void setResult(ArrayList<FoodPlan> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FoodPlanSuccess{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", result=" + result +
                '}';
    }
}
