package com.hopesquad.models;

import java.util.ArrayList;

/**
 * Created by rohit on 16/9/17.
 */

public class ChatMessage {

    private String Message;
    private String MessageID;
    private String Position;
    private String PostedTime;
    private String ReceiverName;
    private String RecevierID;
    private String SenderID;
    private String SenderName;
    private String TickReadStatus;
    private String senderProfilePhoto;
    private String receiverProfilePhoto;
    private String ImageMessageUrl;


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getPostedTime() {
        return PostedTime;
    }

    public void setPostedTime(String postedTime) {
        PostedTime = postedTime;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getRecevierID() {
        return RecevierID;
    }

    public void setRecevierID(String recevierID) {
        RecevierID = recevierID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getTickReadStatus() {
        return TickReadStatus;
    }

    public void setTickReadStatus(String tickReadStatus) {
        TickReadStatus = tickReadStatus;
    }

    public String getSenderProfilePhoto() {
        return senderProfilePhoto;
    }

    public void setSenderProfilePhoto(String senderProfilePhoto) {
        this.senderProfilePhoto = senderProfilePhoto;
    }

    public String getReceiverProfilePhoto() {
        return receiverProfilePhoto;
    }

    public void setReceiverProfilePhoto(String receiverProfilePhoto) {
        this.receiverProfilePhoto = receiverProfilePhoto;
    }

    public String getImageMessageUrl() {
        return ImageMessageUrl;
    }

    public void setImageMessageUrl(String imageMessageUrl) {
        ImageMessageUrl = imageMessageUrl;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "Message='" + Message + '\'' +
                ", MessageID='" + MessageID + '\'' +
                ", Position='" + Position + '\'' +
                ", PostedTime='" + PostedTime + '\'' +
                ", ReceiverName='" + ReceiverName + '\'' +
                ", RecevierID='" + RecevierID + '\'' +
                ", SenderID='" + SenderID + '\'' +
                ", SenderName='" + SenderName + '\'' +
                ", TickReadStatus='" + TickReadStatus + '\'' +
                ", senderProfilePhoto='" + senderProfilePhoto + '\'' +
                ", receiverProfilePhoto='" + receiverProfilePhoto + '\'' +
                ", ImageMessageUrl='" + ImageMessageUrl + '\'' +
                '}';
    }
}
