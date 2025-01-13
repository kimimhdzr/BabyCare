package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailEditTxt;
    private AppCompatButton backButton;
    private AppCompatButton resetButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        emailEditTxt = view.findViewById(R.id.email);
        backButton = view.findViewById(R.id.backButton);
        resetButton = view.findViewById(R.id.resetButton);
        mAuth = FirebaseAuth.getInstance();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditTxt.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditTxt.setError("Email is required");
                    emailEditTxt.requestFocus();
                    return;
                }

                // Send password reset email
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(getView(), "Password reset email sent!", Snackbar.LENGTH_LONG).show();

                           if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        return view;
    }
}