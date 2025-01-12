package com.example.babycare.MainActivity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.babycare.Objects.User;

public class SharedUserModel extends ViewModel {
    // Create a MutableLiveData to hold the shared data
    private final MutableLiveData<User> sessionUser = new MutableLiveData<>();

    // Getter method to expose LiveData to fragments
    public LiveData<User> getSharedData() {
        return sessionUser;
    }

    // Setter method to update the data
    public void setSharedData(User data) {
        sessionUser.setValue(data);  // This will trigger the observers
    }


}