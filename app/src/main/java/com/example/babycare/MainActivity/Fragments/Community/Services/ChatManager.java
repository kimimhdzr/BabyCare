package com.example.babycare.MainActivity.Fragments.Community.Services;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.babycare.R;
import com.example.babycare.DataBinding.Model.MessageModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Personal.Chat;
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
                    // Message successfully saved to Firestore

                    // Add the new message to the adapter instantly
                    MessageModel newMessage = new MessageModel(
                            messageId,
                            "Just now", // Use a placeholder for timestamp until Firestore updates
                            uid,
                            content,
                            new ArrayList<>(),
                            chatID
                    );

                    // Notify the adapter about the new message
                    if (context instanceof FragmentActivity) {
                        FragmentActivity activity = (FragmentActivity) context;
                        activity.runOnUiThread(() -> {
                            NavHostFragment navHostFragment = (NavHostFragment) activity
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.nav_host_fragment_home);

                            if (navHostFragment != null) {
                                // Get the current fragment from the NavController
                                Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

                                // Check if the current fragment is the Chat fragment
                                if (currentFragment instanceof Chat) {
                                    Chat chatFragment = (Chat) currentFragment;

                                    // Update the messagesAdapter
                                    if (chatFragment.messagesAdapter != null) {
                                        chatFragment.messagesAdapter.addMessage(newMessage); // Custom method to add a single message
                                    }
                                } else {
                                    Log.w("ChatManager", "Current fragment is not the Chat fragment.");
                                }
                            }
                        });
                    }

                }).addOnFailureListener(e -> {
                    // Handle error
                });
    }

}
