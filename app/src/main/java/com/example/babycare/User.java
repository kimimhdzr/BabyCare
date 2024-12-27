package com.example.babycare;

public class User {
    public String email;
    public String username;
    public String parentStatus;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username, String parentStatus) {
        this.email = email;
        this.username = username;
        this.parentStatus = parentStatus;
    }
}