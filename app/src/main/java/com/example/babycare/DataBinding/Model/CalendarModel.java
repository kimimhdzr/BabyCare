package com.example.babycare.DataBinding.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarModel {
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String date;

    public CalendarModel() {
    }
    public CalendarModel(String title, String description, String startTime, String endTime, String date) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    public void saveToFirestore() {
        // Get the current user's UID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Save the event to the Calendar subcollection
        db.collection("users")
                .document(currentUserId)
                .collection("Calendar")
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Event added successfully: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding event: " + e.getMessage());
                });
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
