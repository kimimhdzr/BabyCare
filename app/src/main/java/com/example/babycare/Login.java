package com.example.babycare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);




        Button sign_in = findViewById(R.id.sign_in);
        EditText email_input = findViewById(R.id.email);
        EditText password_input = findViewById(R.id.password);

        Button toSignUp = findViewById(R.id.create_account);

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new CreateAcc();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Allows back navigation
                        .commit();
            }
        });



        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(email_input.getText());
                String pass = String.valueOf(password_input.getText());
                FirebaseAuth auth = FirebaseAuth.getInstance();

                // Validate inputs
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Login successful, get the user ID (UID)
                                String uid = auth.getCurrentUser().getUid();

                                // Pass UID to the next activity
                                Intent intent = new Intent(view.getContext(), MainActivity.class); // Replace with your target activity
                                intent.putExtra("USER_UID", uid);
                                view.getContext().startActivity(intent);

                                // Optionally finish the activity (if the context is an Activity)
                                if (view.getContext() instanceof Activity) {
                                    ((Activity) view.getContext()).finish();
                                }
                            } else {
                                // Login failed, show error message
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed.";
                                Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });




    }


}