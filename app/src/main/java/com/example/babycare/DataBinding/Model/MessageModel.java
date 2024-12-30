package com.example.babycare.DataBinding.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModel {

    private String messageID;
    private String chatID;
    private List <String> attachments = new ArrayList<>();
    private String message;
    private String senderID;
    private String timestamp;

    public MessageModel() {
    }

    public MessageModel(
            String messageID,
            String timestamp,
            String senderID,
            String message,
            List <String> attachments,
            String chatID
    ) {
        this.messageID = messageID;
        this.timestamp = timestamp;
        this.senderID = senderID;
        this.message = message;
        this.attachments = attachments;
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
