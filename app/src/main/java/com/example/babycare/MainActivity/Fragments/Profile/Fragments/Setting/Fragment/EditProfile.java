package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.babycare.MainActivity.SharedUserModel;
import com.example.babycare.Objects.User;
import com.example.babycare.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EditProfile extends Fragment {
    private FirebaseAuth mAuth;
    User session_user;
    private FirebaseUser currentUser;
    SharedUserModel sharedUserModel;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        db = FirebaseFirestore.getInstance();

        sharedUserModel = new ViewModelProvider(requireActivity()).get(SharedUserModel.class);
        session_user = (User) sharedUserModel.getSharedData().getValue();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        String signInMethod = currentUser.getProviderData().get(1).getProviderId();
        if(!signInMethod.equals("google.com")){
            signInMethod = currentUser.getProviderData().get(0).getProviderId();
        }


        // You can also get other details about the user
        String email = currentUser.getEmail();
        String displayName = currentUser.getDisplayName();

        // Log the user details
        Log.d("UserDetails", "Email: " + email + ", UID: " + uid + ", Display Name: " + displayName);

        EditText name = view.findViewById(R.id.input_name);
        name.setText(session_user.getUsername());


        EditText email_address = view.findViewById(R.id.input_email);
        email_address.setText(email);


        Button save = view.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm changes?")
                        .setPositiveButton("OK", (dialog, which) -> {
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            }
        });






        return view;
    }

    public Task<Boolean> checkExistingUsername(String username) {


        return db.collection("users")
                .whereEqualTo("username", username) // Filter by username field
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        return !querySnapshot.isEmpty(); // Return true if the username exists, false otherwise
                    } else {
                        // If the query failed, return false
                        return false;
                    }
                });
    }
}