package com.example.babycare.AuthActivity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUp2 extends Fragment {

    Context context;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText usernameEditText;
    private Button backButton, signUpButton;
    private String input_email, input_pass, parent_status,UID;
    private Spinner parent_status_spin;

    public SignUp2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);
        context = getContext();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        if (getArguments() != null) {
            input_email = getArguments().getString("input_email");
            input_pass = getArguments().getString("input_pass");
            UID = getArguments().getString("UID");

            // Use the value as needed
        }



        backButton = view.findViewById(R.id.back_btn);
        NavController navController = NavHostFragment.findNavController(this);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_SignUp1);
            }
        });


        parent_status_spin = view.findViewById(R.id.parent_status);

        // Create a list of options
        String[] options = {"Father", "Mother", "Guardian"};

        // Set up the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(R.layout.status_spinner);
        parent_status_spin.setAdapter(adapter);
        parent_status_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent_status = options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        signUpButton = view.findViewById(R.id.sign_up);
        usernameEditText = view.findViewById(R.id.username);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_pass == null || input_email == null){
                    registerToDB();
                }
                else{
                    signUp();
                }


            }
        });


        return view;
    }


    public void registerToDB(){

        String username = usernameEditText.getText().toString().trim();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", username);
        userProfile.put("profilePic", "");
        userProfile.put("phoneNumber", "");
        userProfile.put("createdAt", FieldValue.serverTimestamp());
        userProfile.put("type", parent_status);

        db.collection("users")
                .document(UID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("session_id",UID);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save user info", Toast.LENGTH_SHORT).show();
                });
    }

    public void signUp(){
        String username = usernameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Name is required");
            return;
        }

        mAuth.createUserWithEmailAndPassword(input_email, input_pass)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Map<String, Object> userProfile = new HashMap<>();
                        userProfile.put("username", username);
                        userProfile.put("profilePic", "");
                        userProfile.put("phoneNumber", "");
                        userProfile.put("createdAt", FieldValue.serverTimestamp());
                        userProfile.put("type", parent_status);

                        // Save the User Profile into Firestore
                        db.collection("users")
                                .document(user.getUid())
                                .set(userProfile)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("session_id",user.getUid());
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    getActivity().finish();

                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to save user info", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(getContext(), "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        NavController navController = NavHostFragment.findNavController(this);
                        navController.navigate(R.id.nav_to_SignUp1);
                    }
                });
    }
}