package com.example.babycare.MainActivity.Fragments.Community.Services;

import java.util.List;

public interface PostCreationCallback {
    void onPostCreated(boolean isSuccess, List<String> uploadimageUrls, String documentID);
    void onImagesUploaded(boolean isSuccess);
}