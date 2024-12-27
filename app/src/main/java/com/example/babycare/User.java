package com.example.babycare;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class User {
    public String email;
    public String username;
    public String parentStatus;
    public ArrayList<Baby> children = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username, String parentStatus) {
        this.email = email;
        this.username = username;
        this.parentStatus = parentStatus;
    }

    public void addChild(Baby newchild){
        children.add(newchild);
    }

    public void removeChild(Baby newchild){
        children.remove(newchild);
    }

    public int getTotalChildren(){
        return children.size();
    }
}