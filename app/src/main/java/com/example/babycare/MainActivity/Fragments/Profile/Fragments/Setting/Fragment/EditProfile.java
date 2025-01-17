package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends Fragment {
    TextView uploadphotoViewTxt;
    EditText nameEditTxt, emailEditTxt, phoneEditTxt;
    public Uri selectedimage;
    ShapeableImageView profileImg;
    ImageButton close_iconImgBtn;
    MaterialButton saveButton;
    private BottomNavigationView bottomNavigationView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private MyDatabaseHelper dbHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        nameEditTxt = view.findViewById(R.id.name);
        emailEditTxt = view.findViewById(R.id.email);
        phoneEditTxt = view.findViewById(R.id.phone);
        profileImg = view.findViewById(R.id.profileImage);
        //click
        uploadphotoViewTxt = view.findViewById(R.id.uploadphototxt);
        close_iconImgBtn = view.findViewById(R.id.close_icon);
        saveButton = view.findViewById(R.id.SaveButton);

        bottomNavigationView = ((MainActivity) getActivity()).findViewById(R.id.bottomNavView);


        String userId = "";
        String username = "";
        String email = "";
        String profilePic = "";
        String phoneNumber = "";

        Cursor cursor = dbHelper.getCurrentUserByDocumentID(uid);
        if (cursor != null && cursor.moveToFirst()) {
            // Map the cursor data to the CurrentUser object
            userId = cursor.getString(cursor.getColumnIndexOrThrow("documentID"));
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"));
            profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));
        }


        nameEditTxt.setHint(username);
        emailEditTxt.setHint(email);
        phoneEditTxt.setHint(phoneNumber);


        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic)
                    .into(profileImg);
        }

        uploadphotoViewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = nameEditTxt.getText().toString().trim();
                String emailString = emailEditTxt.getText().toString().trim();
                String phoneString = phoneEditTxt.getText().toString().trim();

                if (TextUtils.isEmpty(nameString)
                        && TextUtils.isEmpty(emailString)
                        && TextUtils.isEmpty(phoneString)
                        && imageUri == null
                ) {
                    //no change
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    uploadImageToServer(nameString, emailString, phoneString);
                }


            }
        });

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
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
            profileImg.setImageURI(imageUri); // Set the selected image to the ImageView
            // You can call a method here to upload the image to your server or Firebase
            selectedimage = imageUri;
            Log.e("Edit Profile", imageUri.toString() );
        }
    }


    private void uploadImageToServer(final String name, final String email, final String phone) {
        // Implement your image upload logic here
        // For example, using Firebase Storage or your backend service
        String uid = currentUser.getUid();
        if (selectedimage != null) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference profileimageref = storageReference.child("images/profiles/" + uid + ".jpg");

            profileimageref.putFile(selectedimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileimageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    updateUserProfile(uid, name, email, phone, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Edit Profile", "Fail to upload profile image");
                        }
                    });
        } else {
            Log.e("Edit Profile", "No image selected");
            updateUserProfile(uid, name, email, phone, null);
        }
    }

    private void updateUserProfile(String uid, String name, String email, String phone, String imageUrl) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create a map to store updated fields
        Map<String, Object> updatedData = new HashMap<>();

        if (!TextUtils.isEmpty(name)) {
            updatedData.put("username", name);
        }
        if (!TextUtils.isEmpty(email)) {
            updatedData.put("email", email);
        }
        if (!TextUtils.isEmpty(phone)) {
            updatedData.put("phoneNumber", phone);
        }
        if (imageUrl != null) {
            updatedData.put("profilePic", imageUrl);
        }

        // Update user document in Firestore
        firestore.collection("users").document(uid)
                .update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!TextUtils.isEmpty(name)) {
                            dbHelper.updateUserName(uid, name);
                        }
                        if (!TextUtils.isEmpty(email)) {
                            dbHelper.updateUserEmail(uid, email);
                        }
                        if (!TextUtils.isEmpty(phone)) {
                            dbHelper.updateUserPhoneNumber(uid, phone);
                        }
                        if (imageUrl != null) {
                            dbHelper.updateUserProfilePic(uid, imageUrl);
                        }
                        Snackbar.make(getView(), "Profile updated successfully!", Snackbar.LENGTH_LONG).show();
                        // Navigate back to profile after successful update
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Edit Profile", "Failed to update profile: " + e.getMessage());
                    }
                });
    }

}