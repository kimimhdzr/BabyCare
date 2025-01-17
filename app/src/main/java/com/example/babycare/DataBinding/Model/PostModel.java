package com.example.babycare.DataBinding.Model;

import java.io.Serializable;
import java.util.List;

public class PostModel  implements Serializable {

    private String documentID;
    private String description;

    private List<String> attachments;

    private String userId;
    private String profilePic;
    private String userName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    private String timestamp;

    public PostModel() {
    }

    public PostModel (
            String documentID,
            String description,
            List<String> attachments,

            String userId,
            String profilePic,
            String userName,
            String email,

            String timestamp
            ) {

        this.documentID = documentID;
        this.description = description;
        this.attachments = attachments;

        this.userId = userId;
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;

        this.timestamp = timestamp;

    }


    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}