package com.example.babycare.AuthActivity.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Fragment {
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button loginButton, signupButton;
    private TextView forgotpassTxtView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.sign_in);
        signupButton = view.findViewById(R.id.create_account);
        forgotpassTxtView = view.findViewById(R.id.forgot_password);



        NavController navController = NavHostFragment.findNavController(this);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_SignUp1);
            }
        });
        forgotpassTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_ForgotPassword);
            }
        });


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (TextUtils.isEmpty(emailText)) {
                email.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(passwordText)) {
                password.setError("Password is required");
                return;
            }

            // Sign in with Firebase
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            // Navigate to another activity (e.g., MainActivity)
                        } else {
                            Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        return view;
    }
}