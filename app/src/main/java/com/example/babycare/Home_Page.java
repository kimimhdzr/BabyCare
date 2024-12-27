package com.example.babycare;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home_Page extends AppCompatActivity {

    User currentuser;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Intent intent = getIntent();
        UID = intent.getStringExtra("USER_UID");
        retrieveCurrentUser();

        Log.d("ID","IT" + UID);


        TextView Username = findViewById(R.id.sessionusername);




    }

    public void retrieveCurrentUser(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(UID);

        // Retrieve data for the current user based on their UID
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming your user data has fields like "name", "email", etc.
                    String name = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String status = dataSnapshot.child("parentStatus").getValue(String.class);

                    Log.d("TAG","TEST" + name);
                    Log.d("TAG","TEST" + email);
                    Log.d("TAG","TEST" + status);

                    currentuser = new User(email,name,status);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}



