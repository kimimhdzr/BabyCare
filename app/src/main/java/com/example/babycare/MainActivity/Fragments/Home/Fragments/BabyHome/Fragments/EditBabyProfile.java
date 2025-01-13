package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.EditAllergyAdapter;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditBabyProfile extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    MyDatabaseHelper dbHelper;
    BabyProfileModel session_baby;
    EditAllergyAdapter allergyAdapter;
    ArrayList<String> allergies;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_baby_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        Bundle bundle = getArguments();
        session_baby = (BabyProfileModel) bundle.getSerializable("session_baby");
        allergies = session_baby.getAllergies();

        EditText baby_name = view.findViewById(R.id.inputname);
        baby_name.setText(session_baby.getName());

        EditText baby_birthday = view.findViewById(R.id.inputbirthday);
        baby_birthday.setText(session_baby.getDob());

        EditText baby_height = view.findViewById(R.id.inputheight);
        baby_height.setText(String.valueOf(session_baby.getHeight()));

        EditText baby_weight = view.findViewById(R.id.inputweight);
        baby_weight.setText(String.valueOf(session_baby.getWeight()));

        EditText baby_blood = view.findViewById(R.id.inputblood);
        baby_blood.setText(session_baby.getBloodType());

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
                    db.collection("BabyProfile")
                            .document(session_baby.getDocumentID())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Check if the document with that name exists
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        // We assume only one document will match, but adjust as necessary
                                        // Fetch current values from Firestore (old values)
                                        String documentID = documentSnapshot.getId();
                                        String oldName = documentSnapshot.getString("name");
                                        Timestamp oldBirthdayTimestamp = documentSnapshot.getTimestamp("dob");
                                        String oldBirthdayFormatted = convertTimestampToFormattedString(oldBirthdayTimestamp);
                                        String oldHeight = documentSnapshot.getString("height");
                                        String oldWeight = documentSnapshot.getString("weight");
                                        String oldbloodType = documentSnapshot.getString("bloodType");

                                        String parent_fatherId = documentSnapshot.getString("parent.fatherId");
                                        String parent_motherId = documentSnapshot.getString("parent.motherId");
                                        String parent_guardianId = documentSnapshot.getString("parent.guardianId");

                                        List<String> oldAllergies = (List<String>) documentSnapshot.get("allergies");
                                        String profilePic = documentSnapshot.getString("profilePic");



                                        // Check if the new values are different and update Firestore if needed
                                        if (!oldName.equals(newName)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("name", newName);
                                            session_baby.setName(newName);
                                        }

                                        if (!oldBirthdayFormatted.equals(baby_birthday)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("dob", convertFormattedStringToTimestamp(newBirthday));
                                            session_baby.setDob(newBirthday);
                                        }

                                        if (!oldHeight.equals(newHeight)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("height", newHeight);
                                            session_baby.setHeight(newHeight);
                                        }

                                        if (!oldWeight.equals(newWeight)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("weight", newWeight);
                                            session_baby.setWeight(newWeight);
                                        }

                                        if (!oldbloodType.equals(newBlood)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("bloodType", newBlood);
                                            session_baby.setBloodType(newBlood);
                                        }

                                        // Handle allergies updates (assuming allergies are in a list)
                                        ArrayList<String> currentAllergies = allergyAdapter.getAllergies();

                                        if (oldAllergies != null && !oldAllergies.equals(currentAllergies)) {
                                            db.collection("BabyProfile")
                                                    .document(documentSnapshot.getId())
                                                    .update("allergies", currentAllergies);
                                            session_baby.setAllergies(currentAllergies);
                                        }

                                        dbHelper.updateBabyProfile(
                                                documentID,
                                                parent_fatherId,
                                                parent_motherId,
                                                parent_guardianId,
                                                newName,
                                                newBirthday,
                                                newHeight,
                                                newWeight,
                                                newBlood,
                                                formatAllergies(currentAllergies),
                                                profilePic
                                        );

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
            // Parse the date string strictly with the formatter
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String convertTimestampToFormattedString(Timestamp timestamp) {
        if (timestamp == null) {
            return null; // Handle null timestamp if necessary
        }

        // Convert the Firestore Timestamp to a Date object
        Date date = timestamp.toDate();

        // Format the date to "MMM dd, yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    public Timestamp convertFormattedStringToTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null; // Handle invalid or empty date string
        }

        try {
            // Define the same format used for the date string
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            // Parse the string into a Date object
            Date date = dateFormat.parse(dateString);

            // Convert the Date object to a Firestore Timestamp
            return new Timestamp(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("DateConversion", "Invalid date format: " + dateString);
            return null; // Return null if parsing fails
        }
    }



    public String formatAllergies(List<String> allergiesList) {
        String allergies ="";
        if (allergiesList != null && !allergiesList.isEmpty()) {
            // Convert the ArrayList into a comma-separated string
            allergies = String.join(", ", allergiesList);
        } else {
            // Set to null or empty string if the list is null or empty
            allergies = "";
        }
        return allergies;
    }
}