package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by rohit on 16/9/17.
 */

public class ChatMessageSuccess {

    private String code;
    private String success;
    private String message;
    private ArrayList<ChatMessage> result;

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

    public ArrayList<ChatMessage> getResult() {
        return result;
    }

    public void setResult(ArrayList<ChatMessage> result) {
        this.result = result;
    }
}
