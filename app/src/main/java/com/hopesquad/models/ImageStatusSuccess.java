package com.hopesquad.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rohit on 9/9/17.
 */

public class ImageStatusSuccess implements Serializable {


    public String code;
    public String success;
    public String message;
    public ArrayList<ImageStatus> result;

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

    public ArrayList<ImageStatus> getResult() {
        return result;
    }

    public void setResult(ArrayList<ImageStatus> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ImageStatusSuccess{" +
                "code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
