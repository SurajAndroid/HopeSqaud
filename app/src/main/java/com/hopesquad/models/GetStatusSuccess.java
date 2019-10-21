package com.hopesquad.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rohit on 9/9/17.
 */

public class GetStatusSuccess implements Serializable {


    public String message;
    public String code;
    public String success;
    public ArrayList<UserImage> result;

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

    public ArrayList<UserImage> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserImage> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UserImageSuccess{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", result=" + result +
                '}';
    }
}
