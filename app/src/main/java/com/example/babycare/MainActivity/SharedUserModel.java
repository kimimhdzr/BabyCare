package com.example.babycare.MainActivity;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SharedUserModel extends ViewModel {
    // Create a MutableLiveData to hold the shared data
    private final MutableLiveData<User> sessionUser = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    // Getter method to expose LiveData to fragments
    public LiveData<User> getSharedData() {
        return sessionUser;
    }

    // Setter method to update the data in ViewModel
    public void setSharedData(User data) {
        sessionUser.setValue(data); // This will trigger the observers
    }

    // Method to fetch user data from Firebase Firestore
    public void fetchUserData(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class); // Convert Firestore document to User object
                        fetchSessionChildren(user); // Update LiveData
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure (e.g., log the error)
                    e.printStackTrace();
                });
    }

    public void fetchSessionChildren(User user) {
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User or username is null");
        }

        db.collection("babies")
                .whereEqualTo("parent", user.getUsername()) // Filter by parent's username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Baby> fetchChildren = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot) {
                                // Retrieve baby data from the Firestore document
                                String babyName = document.getString("name");
                                String parentName = document.getString("parent");
                                String bloodType = document.getString("bloodtype");
                                String birthDate = document.getString("birthday");
                                String height = document.getString("height");
                                String weight = document.getString("weight");
                                ArrayList<String> allergies = (ArrayList<String>) document.get("allergies");

                                Baby baby = new Baby(parentName, babyName, bloodType, birthDate, height, weight);
                                baby.setAllergies(allergies);
                                fetchChildren.add(baby);
                            }

                            // Update the user's children and LiveData
                            user.setChildren(fetchChildren);
                            setSharedData(user);
                        } else {
                            // No children found for the parent
                            setSharedData(user); // Update LiveData with the current user
                            Log.d("Firestore", "No babies found for the parent.");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching babies", task.getException());
                    }
                });
    }

}