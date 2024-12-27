package com.example.babycare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public User currentuser;
    public String UID;

    // Declare fragments
    private Fragment homeFragment = new HomeFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        Intent intent = getIntent();
        UID = intent.getStringExtra("USER_UID");
        retrieveCurrentUser();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Set up BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Link BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    public void retrieveCurrentUser(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(UID);

        // Retrieve data for the current user based on their UID
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming your user data has fields like "name", "email", etc.
                    String name = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String status = dataSnapshot.child("parentStatus").getValue(String.class);

                    currentuser = new User(email,name,status);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, null) || super.onSupportNavigateUp();
    }
}