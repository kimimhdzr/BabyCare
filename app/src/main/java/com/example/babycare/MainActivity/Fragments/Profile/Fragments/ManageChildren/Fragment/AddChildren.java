package com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AddChildren extends Fragment {
    private static final String TAG = "AddChildren";

    private ArrayList<String> allergies;
    private EditAllergyAdapter allergyAdapter;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MyDatabaseHelper dbHelper;
    FirebaseUser currentUser;

    private ShapeableImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_children, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();


        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        allergies = new ArrayList<>();

        NavController navController = NavHostFragment.findNavController(this);

        EditText baby_name = view.findViewById(R.id.inputname);
        EditText baby_birthday = view.findViewById(R.id.inputbirthday);
        EditText baby_height = view.findViewById(R.id.inputheight);
        EditText baby_weight = view.findViewById(R.id.inputweight);
        EditText baby_blood = view.findViewById(R.id.inputblood);

        RecyclerView allergy_list = view.findViewById(R.id.allergy_list2);
        allergyAdapter = new EditAllergyAdapter(allergies, position -> {
            // Handle delete action
            allergies.remove(position);
            allergyAdapter.updateList(allergies);
        });

        profileImageView = view.findViewById(R.id.baby_pfp); // Make sure this exists in your layout XML
        profileImageView.setOnClickListener(v -> openGallery());

        allergy_list.setAdapter(allergyAdapter);
        allergy_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Button add_allergy = view.findViewById(R.id.add_allergy);
        add_allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                // Use findViewById directly since Button and EditText are in the same layout
                EditText allergy_input = view.findViewById(R.id.new_allergy); // Directly use the activity's context
                String new_allergy = allergy_input.getText().toString();
                if (!new_allergy.isEmpty()) {
                    allergies.add(new_allergy);
                    allergyAdapter.updateList(allergies);
                    allergy_input.setText("");
                } else {
                    allergy_input.setError("Please enter an allergy");
                }
            }
        });

        Button save = view.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checking()) {

                    String name = baby_name.getText().toString();
                    String dob = baby_birthday.getText().toString();
                    Timestamp dobTimestamp = convertToFirebaseTimestamp(dob);
                    String height = baby_height.getText().toString();
                    String weight = baby_weight.getText().toString();
                    String bloodType = baby_blood.getText().toString();

                    Cursor cursor = dbHelper.readAllDataCurrentUser();
                    String type = "";
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                            // Process the data as needed
                        } while (cursor.moveToNext());

                        cursor.close(); // Close the cursor (avoid memory leaks)
                    }


                    Map<String, Object> parent = new HashMap<>();
                    parent.put("fatherId", "");
                    parent.put("motherId",  "");
                    parent.put("guardianId", "");

                    if ("Father".equals(type)) {
                        parent.put("fatherId", uid);
                    } else if ("Mother".equals(type)) {
                        parent.put("fatherId", uid);
                    } else if ("Guardian".equals(type)) {
                        parent.put("fatherId", uid);
                    }

                    Map<String, Object> babyProfile = new HashMap<>();
                    babyProfile.put("name", name);
                    babyProfile.put("dob", dobTimestamp);
                    babyProfile.put("height", height);
                    babyProfile.put("weight", weight);
                    babyProfile.put("bloodType", bloodType);
                    babyProfile.put("parent", parent);
                    babyProfile.put("allergies",  allergies);
                    babyProfile.put("profilePic", "");


                    // Store the Baby object in Firestore
                    db.collection("BabyProfile")
                            .add(babyProfile) // Use add() to generate a unique ID for the document
                            .addOnSuccessListener(documentReference -> {
                                // Successfully added
                                babyProfile.put("dob", convertTimestampToFormattedString(dobTimestamp));
                                System.out.println("Baby data added with ID: " + documentReference.getId());
                                if (imageUri != null){
                                    uploadImageToFirebase(documentReference.getId(), imageUri, navController, babyProfile);
                                } else {
                                    dbHelper.addBabyProfile(babyProfile);
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                System.err.println("Error adding baby data: " + e.getMessage());
                                navController.popBackStack();
                            });

                    Toast.makeText(getContext(), "Baby Added", Toast.LENGTH_LONG);
                    navController.popBackStack();
                }
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Only allow image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public boolean checking() {
        EditText baby_name = getActivity().findViewById(R.id.inputname);
        EditText baby_birthday = getActivity().findViewById(R.id.inputbirthday);
        EditText baby_height = getActivity().findViewById(R.id.inputheight);
        EditText baby_weight = getActivity().findViewById(R.id.inputweight);
        EditText baby_blood = getActivity().findViewById(R.id.inputblood);

        if (baby_name.getText().toString().isEmpty()) {
            baby_name.setError("Please enter a name");
            return false;
        }
        if (baby_birthday.getText().toString().isEmpty()) {
            baby_birthday.setError("Please enter a birthday");
            return false;
        }
        if (baby_height.getText().toString().isEmpty()) {
            baby_height.setError("Please enter a height");
            return false;
        }
        if (baby_weight.getText().toString().isEmpty()) {
            baby_weight.setError("Please enter a weight");
            return false;
        }
        if (baby_blood.getText().toString().isEmpty()) {
            baby_blood.setError("Please enter a blood type");
            return false;
        }

        if (!isValidDateFormat(baby_birthday.getText().toString())) {
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
    private Timestamp convertToFirebaseTimestamp(String formattedDate) {
        try {
            // Define the date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  // Make sure to handle time zones as needed

            // Parse the string into a Date object
            Date date = sdf.parse(formattedDate);

            // Return as a Firebase Timestamp
            return new Timestamp(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();  // Get the image URI
            profileImageView.setImageURI(imageUri);  // Display the image in the CircleImageView
        }
    }

    private void uploadImageToFirebase(String babyID, Uri imageUri, NavController navController, Map<String, Object> babyProfile) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference storageRef = storageReference.child("images/babyprofile/" + babyID + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updatePictureIntoFirebase(babyID, imageUrl, navController, babyProfile);
                }))
                .addOnFailureListener(e -> {
                    // Log the error to the console
                    Log.e("FirebaseUpload", "Error uploading image for babyID: " + babyID, e);
                    dbHelper.addBabyProfile(babyProfile);
                });
    }

    private void updatePictureIntoFirebase(String babyID, String imageUrl, NavController navController, Map<String, Object> babyProfile) {
        // Reference the BabyProfile document using babyID
        db.collection("BabyProfile")
                .document(babyID)
                .update("profilePic", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Log success
                    Log.d("updatePictureIntoFirebase", "Profile picture updated successfully!");

                    babyProfile.put("profilePic", imageUrl);
                    dbHelper.addBabyProfile(babyProfile);

                    // Navigate to the desired fragment if a NavController is provided
                    if (navController != null) {
                        navController.popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    // Log failure
                    Log.e("updatePictureIntoFirebase", "Error updating profile picture", e);
                    dbHelper.addBabyProfile(babyProfile);
                });
    }

}