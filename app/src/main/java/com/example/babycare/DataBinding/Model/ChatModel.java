package com.example.babycare.DataBinding.Model;

import android.content.Context;

import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Services.ChatManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    private String documentID;
    private Map<String, Object> lastMessage = new HashMap<>();
    private Map<String, Object> participants = new HashMap<>();
    private String timestamp;

    public ChatModel() {
    }
    public ChatModel(
            String documentID,
            String lastMessage_senderID,
            String lastMessage_userName,
            String lastMessage_messageId,
            String lastMessage_timestamp,
            String participants_userId,
            String participants_userName,
            String participants_profilePic,
            String createdAt
    ) {
        this.documentID = documentID;

        lastMessage.put("senderId", lastMessage_senderID);
        lastMessage.put("userName", lastMessage_userName);
        lastMessage.put("messageId", lastMessage_messageId);
        lastMessage.put("timestamp", lastMessage_timestamp);

        participants.put("userId", participants_userId);
        participants.put("userName", participants_userName);
        participants.put("profilePic", participants_profilePic);

        this.timestamp = createdAt;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
    public Map<String, Object> getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Map<String, Object> lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, Object> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Object> participants) {
        this.participants = participants;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
