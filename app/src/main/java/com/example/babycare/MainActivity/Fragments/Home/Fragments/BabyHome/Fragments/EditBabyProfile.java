package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.EditAllergyAdapter;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class EditBabyProfile extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MyDatabaseHelper dbHelper;
    private BabyProfileModel session_baby;
    private EditAllergyAdapter allergyAdapter;
    private ArrayList<String> allergies;
    ShapeableImageView add_baby_pfp;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    public Uri selectedimage;

    private EditText babyName, babyBirthday, babyHeight, babyWeight, babyBlood;
    private RecyclerView allergyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_baby_profile, container, false);

        // Initialize Firebase and database helper
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        // Retrieve session data
        Bundle bundle = getArguments();
        session_baby = (BabyProfileModel) bundle.getSerializable("session_baby");
        allergies = session_baby.getAllergies();

        // Initialize views
        babyName = view.findViewById(R.id.inputname);
        babyBirthday = view.findViewById(R.id.inputbirthday);
        babyHeight = view.findViewById(R.id.inputheight);
        babyWeight = view.findViewById(R.id.inputweight);
        babyBlood = view.findViewById(R.id.inputblood);
        allergyList = view.findViewById(R.id.allergy_list2);
        add_baby_pfp = view.findViewById(R.id.add_baby_pfp);

        // Set initial values
        setInitialValues();

        // Setup allergy adapter
        setupAllergyList();

        add_baby_pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        // Add allergy button
        view.findViewById(R.id.add_allergy).setOnClickListener(v -> addAllergy(view));

        // Save button
        view.findViewById(R.id.save_btn).setOnClickListener(v -> saveChanges());

        return view;
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Only allow image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            imageUri = data.getData(); // Get the image URI
            add_baby_pfp.setImageURI(imageUri); // Set the selected image to the ImageView
            // You can call a method here to upload the image to your server or Firebase
            selectedimage = imageUri;
            Log.e("Edit Profile", imageUri.toString() );
        }
    }

    private void setInitialValues() {
        babyName.setText(session_baby.getName());
        babyBirthday.setText(session_baby.getDob());
        babyHeight.setText(String.valueOf(session_baby.getHeight()));
        babyWeight.setText(String.valueOf(session_baby.getWeight()));
        babyBlood.setText(session_baby.getBloodType());
        Glide.with(getContext())
                .load(session_baby.getProfilePic()) // Replace with your drawable resource
                .into(add_baby_pfp);
    }

    private void setupAllergyList() {
        allergyAdapter = new EditAllergyAdapter(allergies, position -> {
            session_baby.removeAllergy(position);
            allergyAdapter.updateList(session_baby.getAllergies());
        });
        allergyList.setAdapter(allergyAdapter);
        allergyList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void addAllergy(View view) {
        EditText allergyInput = view.findViewById(R.id.new_allergy);
        String newAllergy = allergyInput.getText().toString().trim();
        if (!newAllergy.isEmpty()) {
            session_baby.addAllergy(newAllergy);
            allergyAdapter.updateList(session_baby.getAllergies());
            allergyInput.setText("");
        }
    }

//    private void uploadImageToFirebase(String babyID, Uri imageUri, NavController navController, Map<String, Object> babyProfile) {
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//        StorageReference storageReference = firebaseStorage.getReference();
//        StorageReference storageRef = storageReference.child("images/babyprofile/" + babyID + ".jpg");
//
//        storageRef.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String imageUrl = uri.toString();
//                }))
//                .addOnFailureListener(e -> {
//                    // Log the error to the console
//                    Log.e("FirebaseUpload", "Error uploading image for babyID: " + babyID, e);
//                    dbHelper.addBabyProfile(babyProfile);
//                });
//    }

    private void saveChanges() {
        if (!validateInputs()) return;

        String newName = babyName.getText().toString();
        String newBirthday = babyBirthday.getText().toString();
        String newHeight = babyHeight.getText().toString();
        String newWeight = babyWeight.getText().toString();
        String newBlood = babyBlood.getText().toString();
        ArrayList<String> currentAllergies = allergyAdapter.getAllergies();
//        uploadImageToFirebase();

        db.collection("BabyProfile")
                .document(session_baby.getDocumentID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> updates = new HashMap<>();

                        updateFieldIfChanged(updates, "name", documentSnapshot.getString("name"), newName, val -> session_baby.setName(val));
                        updateFieldIfChanged(updates, "dob", convertTimestampToFormattedString(documentSnapshot.getTimestamp("dob")), newBirthday, val -> session_baby.setDob(val));
                        updateFieldIfChanged(updates, "height", documentSnapshot.getString("height"), newHeight, val -> session_baby.setHeight(val));
                        updateFieldIfChanged(updates, "weight", documentSnapshot.getString("weight"), newWeight, val -> session_baby.setWeight(val));
                        updateFieldIfChanged(updates, "bloodType", documentSnapshot.getString("bloodType"), newBlood, val -> session_baby.setBloodType(val));

                        if (!currentAllergies.equals(documentSnapshot.get("allergies"))) {
                            updates.put("allergies", currentAllergies);
                            session_baby.setAllergies(currentAllergies);
                        }

                        // Apply updates in a single batch
                        if (!updates.isEmpty()) {
                            db.collection("BabyProfile")
                                    .document(session_baby.getDocumentID())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        dbHelper.updateBabyProfile(
                                                session_baby.getDocumentID(),
                                                documentSnapshot.getString("parent.fatherId"),
                                                documentSnapshot.getString("parent.motherId"),
                                                documentSnapshot.getString("parent.guardianId"),
                                                newName, newBirthday, newHeight, newWeight, newBlood,
                                                formatAllergies(currentAllergies),
                                                documentSnapshot.getString("profilePic")
                                        );
                                        Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Update failed", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching document", e));
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (isEmpty(babyName)) isValid = false;
        if (isEmpty(babyBirthday) || !isValidDateFormat(babyBirthday.getText().toString())) {
            babyBirthday.setError("Invalid date format");
            isValid = false;
        }
        if (isEmpty(babyHeight)) isValid = false;
        if (isEmpty(babyWeight)) isValid = false;
        if (isEmpty(babyBlood)) isValid = false;

        return isValid;
    }

    private boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("Field cannot be empty");
            return true;
        }
        return false;
    }

    private void updateFieldIfChanged(Map<String, Object> updates, String key, String oldValue, String newValue, Consumer<String> onUpdate) {
        if (!Objects.equals(oldValue, newValue)) {
            updates.put(key, newValue);
            onUpdate.accept(newValue);
        }
    }

    private String convertTimestampToFormattedString(Timestamp timestamp) {
        if (timestamp == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timestamp.toDate());
    }

    public static boolean isValidDateFormat(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String formatAllergies(List<String> allergiesList) {
        return (allergiesList == null || allergiesList.isEmpty()) ? "" : String.join(", ", allergiesList);
    }
}
