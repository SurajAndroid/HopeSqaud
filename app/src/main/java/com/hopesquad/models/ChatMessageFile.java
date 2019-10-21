package com.hopesquad.models;

/**
 * Created by rohit on 16/9/17.
 */

public class ChatMessageFile {

    private String FilePath;
    private String FileUrl;
    private int MessageID;

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public int getMessageID() {
        return MessageID;
    }

    public void setMessageID(int messageID) {
        MessageID = messageID;
    }
}
