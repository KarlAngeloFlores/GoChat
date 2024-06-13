package com.example.gochat.Chat;

import java.util.ArrayList;

public class MessageObject {

    private String messageId, senderId, message, contactNo;

    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrlList, String contactNo) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.contactNo = contactNo;
    }


    public String  getContactNo() {
        return  contactNo;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

}

