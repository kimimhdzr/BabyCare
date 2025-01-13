package com.example.babycare.MainActivity.Fragments.Community.Services;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewChat {
    private FirebaseFirestore db;
    private MyDatabaseHelper dbHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    public NewChat() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void createChat(
            Context context,
            String chatId,
            String toUid,
            String toUsername,
            String toProfilePicURL,
            String messageContent,
            ChatCreationCallback callback) {


        dbHelper = new MyDatabaseHelper(context, "CurrentUser.db");

        String currentUid = currentUser.getUid();
        String currentUsername = "";
        String currentProfilePicURL ="";

        Cursor cursor = dbHelper.getCurrentUserByDocumentID(currentUid);
        if (cursor != null && cursor.moveToFirst()) {
            currentUsername = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            currentProfilePicURL = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));
        }

        String messageId = UUID.randomUUID().toString();

        // Create last message using a HashMap
        Map<String, Object> lastMessage = new HashMap<>();
        lastMessage.put("senderId", currentUid);
        lastMessage.put("userName", currentUsername);
        lastMessage.put("messageId", messageId);
        lastMessage.put("messageContent", messageContent);
        lastMessage.put("timestamp", FieldValue.serverTimestamp());

        // Create user_1 using a HashMap
        Map<String, Object> user_1 = new HashMap<>();
        user_1.put("userId", currentUid);
        user_1.put("userName", currentUsername);
        user_1.put("profilePic", currentProfilePicURL);

        // Create user_2 using a HashMap
        Map<String, Object> user_2 = new HashMap<>();
        user_2.put("userId", toUid);
        user_2.put("userName", toUsername);
        user_2.put("profilePic", toProfilePicURL);


        // Create participants using a HashMap
        Map<String, Object> participants = new HashMap<>();
        participants.put("user_1", user_1);
        participants.put("user_2", user_2);

        // Create the chat document using HashMap
        Map<String, Object> chat = new HashMap<>();
        chat.put("lastMessage", lastMessage);
        chat.put("participants", participants);
        chat.put("createdAt", FieldValue.serverTimestamp());


        // Write the chat document to Firestore
        db.collection("chats")
                .document(chatId)
                .set(chat)
                .addOnSuccessListener(aVoid -> {
                    // Chat created successfully, call the callback
                    Log.d("NewChat", "Chat successfully created with ID: " + chatId);
                    callback.onChatCreated(chatId);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.w("NewChat", "Error creating chat", e);
                });
    }

}