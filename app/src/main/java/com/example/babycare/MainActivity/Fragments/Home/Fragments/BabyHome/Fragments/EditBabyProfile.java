package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.babycare.Objects.Baby;
import com.example.babycare.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import android.provider.MediaStore;

public class EditBabyProfile extends Fragment {
    Baby session_baby;
    EditAllergyAdapter allergyAdapter;
    ArrayList<String> allergies;
    String oldName, oldBirthday, oldHeight, oldWeight, oldBlood,oldAllergies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_baby_profile, container, false);

        Bundle bundle = getArguments();
        session_baby = (Baby) bundle.getSerializable("session_baby");
        allergies = session_baby.getAllergies();

        EditText baby_name = view.findViewById(R.id.inputname);
        baby_name.setText(session_baby.getName());

        EditText baby_birthday = view.findViewById(R.id.inputbirthday);
        baby_birthday.setText(session_baby.getBirthday());

        EditText baby_height = view.findViewById(R.id.inputheight);
        baby_height.setText(String.valueOf(session_baby.getHeight()));

        EditText baby_weight = view.findViewById(R.id.inputweight);
        baby_weight.setText(String.valueOf(session_baby.getWeight()));

        EditText baby_blood = view.findViewById(R.id.inputblood);
        baby_blood.setText(session_baby.getBloodtype());

        RecyclerView allergy_list = view.findViewById(R.id.allergy_list2);



        allergyAdapter = new EditAllergyAdapter(allergies,position -> {
            // Handle delete action
            session_baby.removeAllergy(position);
            allergyAdapter.updateList(session_baby.getAllergies());
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
                    session_baby.addAllergy(new_allergy);
                    allergyAdapter.updateList(session_baby.getAllergies());
                    allergy_input.setText("");
                }

            }
        });

        Button save = view.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = baby_name.getText().toString();
                String newBirthday = baby_birthday.getText().toString();
                String newHeight = baby_height.getText().toString();
                String newWeight = baby_weight.getText().toString();
                String newBlood = baby_blood.getText().toString();

                if(checking()){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("babies")
                        .whereEqualTo("name", session_baby.getName())
                        .whereEqualTo("parent",session_baby.getParent())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Check if the document with that name exists
                                if (!task.getResult().isEmpty()) {
                                    // We assume only one document will match, but adjust as necessary
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                    // Fetch current values from Firestore (old values)
                                    String oldName = document.getString("name");
                                    String oldBirthday = document.getString("birthday");
                                    String oldHeight = document.getString("height");
                                    String oldWeight = document.getString("weight");
                                    String oldBloodtype = document.getString("bloodtype");

                                    // Check if the new values are different and update Firestore if needed
                                    if (!oldName.equals(newName)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("name", newName);
                                        session_baby.setName(newName);
                                    }

                                    if (!oldBirthday.equals(baby_birthday)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("birthday", newBirthday);
                                        session_baby.setBirthday(newBirthday);
                                    }

                                    if (!oldHeight.equals(newHeight)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("height", newHeight);
                                        session_baby.setHeight(newHeight);
                                    }

                                    if (!oldWeight.equals(newWeight)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("weight", newWeight);
                                        session_baby.setWeight(newWeight);
                                    }

                                    if (!oldBloodtype.equals(baby_blood)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("bloodtype", newBlood);
                                        session_baby.setBloodtype(newBlood);
                                    }

                                    // Handle allergies updates (assuming allergies are in a list)
                                    ArrayList<String> currentAllergies = allergyAdapter.getAllergies();


                                    List<String> oldAllergies = (ArrayList<String>) document.get("allergies");
                                    if (oldAllergies != null && !oldAllergies.equals(currentAllergies)) {
                                        db.collection("babies")
                                                .document(document.getId())
                                                .update("allergies", currentAllergies);
                                        session_baby.setAllergies(currentAllergies);
                                    }

                                } else {
                                    // Handle case where the document with the name doesn't exist
                                    Log.d("Firestore", "No document found with the specified name");
                                }
                            } else {
                                // Handle any errors in the query
                                Log.w("Firestore", "Error getting document.", task.getException());
                            }
                        });
                    Toast.makeText(getContext(),"Changes successful",Toast.LENGTH_LONG).show();

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
}