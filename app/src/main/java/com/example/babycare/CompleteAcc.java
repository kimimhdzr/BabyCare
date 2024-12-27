package com.example.babycare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompleteAcc extends Fragment {

    Context context;
    FirebaseAuth FAuth;
    String input_email,input_pass,input_username;
    EditText usernameEditText;

    String parent_status;
    public CompleteAcc() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.complete_account, container, false);
        context = getContext();



        if (getArguments() != null) {
            input_email = getArguments().getString("input_email");
            input_pass = getArguments().getString("input_password");
            // Use the value as needed
        }

        Log.d("TAG","T"+input_email);
        Log.d("TAG","T"+input_pass);



        Spinner parent_status_spin = view.findViewById(R.id.parent_status);

        // Create a list of options
        String[] options = {"","Father", "Mother", "Guardian"};


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



        //Back to the previous Fragment
        Button backButton = view.findViewById(R.id.back_btn);
        usernameEditText = view.findViewById(R.id.username);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use FragmentManager to pop the back stack
                getParentFragmentManager().popBackStack();
            }
        });


        //Sign up button to complete registration and save to database
        Button SignUp = view.findViewById(R.id.sign_up);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_username = String.valueOf(usernameEditText.getText());
                checkIfUsernameExists();

            }
        });

        return view;
    }

    private void registerUser() {
        FAuth = FirebaseAuth.getInstance();
        FAuth.createUserWithEmailAndPassword(input_email, input_pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_LONG).show();
                StoreDatabase();
                Intent intent = new Intent(getActivity(), Home_Page.class);
                startActivity(intent);
                requireActivity().finish(); // Close the current activity
            } else {
                Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void checkIfUsernameExists() {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean usernameExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    if (username != null && username.equals(input_username)) {
                        usernameExists = true;
                        break;
                    }
                }

                if (usernameExists) {
                    // Username already exists
                    Toast.makeText(getContext(), "Username is already taken!", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void StoreDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        String userId = databaseReference.push().getKey(); // Or use FirebaseAuth.getInstance().getCurrentUser().getUid()

        if (userId != null) {
            // Create a new User object with the input data
            User user = new User(input_email, input_username, parent_status);

            // Save the user data under the 'users' node with the unique user ID
            databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Toast.makeText(getContext(), "User registered successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


}