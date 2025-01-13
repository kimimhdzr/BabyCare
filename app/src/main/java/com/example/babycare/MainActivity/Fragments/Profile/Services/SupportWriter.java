package com.example.babycare.MainActivity.Fragments.Profile.Services;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupportWriter {
    private FirebaseFirestore db;
    private MyDatabaseHelper dbHelper;  // Initialize DBHelper instance


    public SupportWriter(Context context) {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        dbHelper = new MyDatabaseHelper(context, "CurrentUser.db");
    }

    public void writeSupport(Context context,
                             String uid,
                             String title, String description,
                             String selectedSupport,
                             List<String> imageUrls,
                             SupportWriterCallback callback) {

        // Retrieve the current user's information from the database
        Cursor cursor = dbHelper.getCurrentUserByDocumentID(uid);
        if (cursor != null && cursor.moveToFirst()) {
            // Map the cursor data to the CurrentUser object
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("documentID"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));


            // Create participants map
            Map<String, Object> participants = new HashMap<>();
            participants.put("user_id", userId);
            participants.put("username", username);
            participants.put("email_from", email);
            participants.put("profile_pic", profilePic);


            // Create details map
            Map<String, Object> details = new HashMap<>();
            details.put("description", description);
            details.put("title", title);
            details.put("images", imageUrls);
            details.put("support", selectedSupport);

            // Create report data map
            Map<String, Object> supportData = new HashMap<>();
            supportData.put("participants", participants);
            supportData.put("details", details);
            supportData.put("timestamp", FieldValue.serverTimestamp());

            // Generate a new report ID and save to Firestore
            db.collection("supports")
                    .add(supportData)
                    .addOnSuccessListener(documentReference -> {
                        // Handle success
                        callback.onSuccess();

                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        callback.onFailure(e);
                    });
        } else {
            // Handle failure when user data is not found
            callback.onFailure(new Exception("User not found"));
        }
        // Close cursor after use to prevent memory leak
        if (cursor != null) {
            cursor.close();
        }
    }
}
