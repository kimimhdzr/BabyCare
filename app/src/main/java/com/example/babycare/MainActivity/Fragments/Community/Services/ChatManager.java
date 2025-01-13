package com.example.babycare.MainActivity.Fragments.Community.Services;

import android.content.Context;

import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    private static ChatManager instance;
    private FirebaseFirestore db;
    MyDatabaseHelper dbHelper;
    private String chatID;
    private String chatName;

    public ChatManager() {
        db = FirebaseFirestore.getInstance();
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatName() {return chatName;}

    public void setChatName(String chatName) {this.chatName = chatName;}

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void sendMessage(Context context, String content, String uid) {
        if(chatID == null || chatID.isEmpty()) {
            // Log an error or handle the case when chatId is null or empty
            return;
        }
        dbHelper = new MyDatabaseHelper(context, "CurrentUser.db");


        // Create a new message document
        String messageId = db.collection("messages").document().getId(); // Generate a new ID for the message


        Map<String, Object> message = new HashMap<>();
        message.put("attachments", new ArrayList<>());
        message.put("message", content);
        message.put("senderId", uid);
        message.put("timestamp", FieldValue.serverTimestamp());

        // Save the message to the messageModels collection
        db.collection("chats")
                .document(chatID)
                .collection("messages")
                .document(messageId)
                .set(message)
                .addOnSuccessListener(aVoid -> {
                    // MessageModel saved successfully
//                    updateChat(chatID, messageId, content, dbHelper.getRole());
                }).addOnFailureListener(e -> {
                    // Handle error
                });
    }

//    private void updateChat(String chatId, String messageId, String content, String senderId) {
//        Map<String, Object> chatUpdate = new HashMap<>();
//        chatUpdate.put("last_message.message_id", messageId);
//        chatUpdate.put("last_message.content", content);
//        chatUpdate.put("last_message.timestamp", FieldValue.serverTimestamp());
//        chatUpdate.put("last_message.sender", senderId);
//        // Update the chat document
//        db.collection("chats")
//                .document(chatId)
//                .update(chatUpdate)
//                .addOnSuccessListener(aVoid -> {
//                    // Chat updated successfully
//                }).addOnFailureListener(e -> {
//                    // Handle error
//                });
//    }
}
