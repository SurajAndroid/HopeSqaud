package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by rohit on 1/10/17.
 */

public class UserListResponse {

    private String code;
    private String success;
    private String message;
    private ArrayList<UserListData> result;

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

    public ArrayList<UserListData> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserListData> result) {
        this.result = result;
    }

}
