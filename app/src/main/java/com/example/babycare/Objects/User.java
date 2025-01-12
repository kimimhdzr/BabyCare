package com.example.babycare.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username,email,status;
    private ArrayList<Baby> children = new ArrayList<>();

    public User() {
        this.username = "";
        this.status = "";
    }

    public User(String username, String email, String status) {
        this.username = username;
        this.email = email;
        this.status = status;
        this.children = children;
    }

    public User(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }


    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Baby> getChildren() {
        return children;
    }


    public void addChildren(Baby baby){
        children.add(baby);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChildren(ArrayList<Baby> children) {
        this.children = children;
    }
}
