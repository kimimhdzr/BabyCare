package com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments.AllergyAdapter;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments.EditAllergyAdapter;
import com.example.babycare.MainActivity.SharedUserModel;
import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.User;
import com.example.babycare.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddChildren extends Fragment {

    private ArrayList<String> allergies;
    private EditAllergyAdapter allergyAdapter;
    private User session_user;
    FirebaseFirestore db;
    private SharedUserModel sharedUserModel;
    private CircleImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        session_user = (User) getArguments().getSerializable("session_user");

        sharedUserModel = new ViewModelProvider(requireActivity()).get(SharedUserModel.class);
        sharedUserModel.getSharedData();



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_children, container, false);


        db = FirebaseFirestore.getInstance();
        allergies = new ArrayList<>();

        NavController navController = NavHostFragment.findNavController(this);

        EditText baby_name = view.findViewById(R.id.inputname);
        EditText baby_birthday = view.findViewById(R.id.inputbirthday);
        EditText baby_height = view.findViewById(R.id.inputheight);
        EditText baby_weight = view.findViewById(R.id.inputweight);
        EditText baby_blood = view.findViewById(R.id.inputblood);

        RecyclerView allergy_list = view.findViewById(R.id.allergy_list2);
        allergyAdapter = new EditAllergyAdapter(allergies,position -> {
            // Handle delete action
            allergies.remove(position);
            allergyAdapter.updateList(allergies);
        });


        allergy_list.setAdapter(allergyAdapter);
        allergy_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        Button add_allergy = view.findViewById(R.id.add_allergy);
        add_allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                // Use findViewById directly since Button and EditText are in the same layout
                EditText allergy_input = view.findViewById(R.id.new_allergy); // Directly use the activity's context
                String new_allergy = allergy_input.getText().toString();
                if(!new_allergy.isEmpty()){
                    allergies.add(new_allergy);
                    allergyAdapter.updateList(allergies);
                    allergy_input.setText("");
                }
               else{
                    allergy_input.setError("Please enter an allergy");
                }
            }
        });

        Button save = view.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checking()){
                    String name = baby_name.getText().toString();
                    String birthday = baby_birthday.getText().toString();
                    String height = baby_height.getText().toString();
                    String weight = baby_weight.getText().toString();
                    String blood = baby_blood.getText().toString();

                    Baby baby = new Baby(session_user.getUsername(),name,blood,birthday,height,weight);
                    baby.setAllergies(allergies);

                    // Store the Baby object in Firestore
                    db.collection("babies")
                            .add(baby) // Use add() to generate a unique ID for the document
                            .addOnSuccessListener(documentReference -> {
                                // Successfully added
                                System.out.println("Baby data added with ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                System.err.println("Error adding baby data: " + e.getMessage());
                            });

                    Toast.makeText(getContext(),"Baby Added",Toast.LENGTH_LONG);
                    session_user.addChildren(baby);
                    sharedUserModel.setSharedData(session_user);
                    ;
                    navController.popBackStack();

                }

            }
        });

        return view;
    }



    public boolean checking(){
        EditText baby_name = getActivity().findViewById(R.id.inputname);
        EditText baby_birthday = getActivity().findViewById(R.id.inputbirthday);
        EditText baby_height = getActivity().findViewById(R.id.inputheight);
        EditText baby_weight = getActivity().findViewById(R.id.inputweight);
        EditText baby_blood = getActivity().findViewById(R.id.inputblood);

        if(baby_name.getText().toString().isEmpty()){
            baby_name.setError("Please enter a name");
            return false;
        }
        if(baby_birthday.getText().toString().isEmpty()){
            baby_birthday.setError("Please enter a birthday");
        }
        if(baby_height.getText().toString().isEmpty()){
            baby_height.setError("Please enter a height");
        }
        if(baby_weight.getText().toString().isEmpty()){
            baby_weight.setError("Please enter a weight");
            return false;
        }
        if(baby_blood.getText().toString().isEmpty()){
            baby_blood.setError("Please enter a blood type");
            return false;
        }

        if(!isValidDateFormat(baby_birthday.getText().toString())){
            baby_birthday.setError("Please enter a valid date");
            return false;
        }

        return true;

    }

    public static boolean isValidDateFormat(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateString, formatter); // Ensures strict parsing
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void storeBabyDataInFirestore(Baby baby, NavController navController) {
        db.collection("babies")
                .add(baby)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Baby Added", Toast.LENGTH_LONG).show();
                    session_user.addChildren(baby);
                    sharedUserModel.setSharedData(session_user);
                    navController.popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error adding baby: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

}