package com.example.babycare.MainActivity.Fragments.Community.Services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NewComment {
    private FirebaseFirestore db;
    private MyDatabaseHelper dbHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public NewComment() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void createComment(Context context, String forumId, String description, List<String> selectimageUrls, PostCreationCallback callback) {

        dbHelper = new MyDatabaseHelper(context, "CurrentUser.db");

        final String currentUid = currentUser.getUid();
        String currentUsername = "";
        String currentProfilePicURL ="";

        Cursor cursor = dbHelper.getCurrentUserByDocumentID(currentUid);
        if (cursor != null && cursor.moveToFirst()) {
            currentUsername = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            currentProfilePicURL = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));
        }

        final String userName = currentUsername;
        final String profilePic = currentProfilePicURL;


        // Step 1: Upload the selected images and get the uploaded URLs
        uploadImages(selectimageUrls, new ImageUploadCallback() {
            @Override
            public void onUploadSuccess(List<String> uploadimageUrls) {
                // Notify that images were uploaded successfully
                callback.onImagesUploaded(true);

                // Step 2: Create the post using the uploaded image URLs
                Map<String, Object> sender = new HashMap<>();
                sender.put("userId", currentUid);
                sender.put("profilePic", profilePic);
                sender.put("userName", userName);


                // Create the post data
                Map<String, Object> comment = new HashMap<>();
                comment.put("description", description);
                comment.put("attachments", uploadimageUrls);  // Use the uploaded image URLs
                comment.put("sender", sender);
                comment.put("timestamp", FieldValue.serverTimestamp());

                // Step 3: Save the post to the Firestore 'posts' collection
                db.collection("Forums")
                        .document(forumId) // Replace forumId with the actual forum ID
                        .collection("Comments") // Access the Comments subcollection
                        .add(comment) // Add the comment to the subcollection
                        .addOnSuccessListener(documentReference -> {
                            // Post creation successful
                            System.out.println("Post successfully written with ID: " + documentReference.getId());
                            callback.onPostCreated(true, uploadimageUrls, documentReference.getId());  // Trigger post creation callback
                        })
                        .addOnFailureListener(e -> {
                            // Post creation failed
                            System.err.println("Error creating post: " + e.getMessage());
                            callback.onPostCreated(false, uploadimageUrls, "fail");  // Trigger failure callback
                        });
            }

            @Override
            public void onUploadFailure() {
                // Notify that image upload failed
                callback.onImagesUploaded(false);
            }
        });
    }


    private void uploadImages(List<String> selectimageUrls, ImageUploadCallback callback) {
        List<String> uploadimageUrls = new ArrayList<>();
        AtomicInteger uploadCount = new AtomicInteger(0);  // To track how many images have been uploaded
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        String currentUid = currentUser.getUid();

        if (selectimageUrls == null || selectimageUrls.isEmpty()){
            callback.onUploadSuccess(uploadimageUrls);  // All images uploaded successfully
            return;
        }
        int totalImages = selectimageUrls.size();
        for (int i = 0; i < totalImages; i++) {
            // Assuming you have a Firebase Storage reference
            String fileUri = selectimageUrls.get(i);
            StorageReference imageRef = storageRef.child("images/comments/" + currentUid + "/" + UUID.randomUUID().toString());

            // Upload the image file to Firebase Storage
            imageRef.putFile(Uri.parse(fileUri))
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL once the image is uploaded
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            uploadimageUrls.add(uri.toString());

                            // Check if all images have been uploaded
                            if (uploadCount.incrementAndGet() == selectimageUrls.size()) {
                                callback.onUploadSuccess(uploadimageUrls);  // All images uploaded successfully
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors during upload
                        callback.onUploadFailure();  // Trigger the failure callback
                    });
        }
    }


}