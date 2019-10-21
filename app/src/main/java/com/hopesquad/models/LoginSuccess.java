package com.hopesquad.models;

import java.io.Serializable;

/**
 * Created by rohit on 9/9/17.
 */

public class LoginSuccess implements Serializable {


    public String message;
    public String code;
    public String success;
    public UserInfo  result;

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

    public UserInfo getResult() {
        return result;
    }

    public void setResult(UserInfo result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "LoginSuccess{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", success='" + success + '\'' +
                ", result=" + result +
                '}';
    }
}
