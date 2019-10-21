package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by pc on 24/01/2018.
 */

public class UserMediaResponse {

    private String code;
    private String success;
    private String message;
    private ArrayList<UserMediaList> result;

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

    public ArrayList<UserMediaList> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserMediaList> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UserMediaResponse{" +
                "code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
