package com.example.babycare.MainActivity.Fragments.Profile;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainProfile extends Fragment {

    MaterialCardView manageChildrenCard, settingCard, editProfileCard;
    ShapeableImageView profileImage;
    TextView userNameTxtView;
    private MyDatabaseHelper dbHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        manageChildrenCard = view.findViewById(R.id.manageChildren);
        settingCard = view.findViewById(R.id.setting);
        editProfileCard = view.findViewById(R.id.editProfile);

        profileImage = view.findViewById(R.id.profileImage);
        userNameTxtView = view.findViewById(R.id.username);


        Cursor cursor = dbHelper.getCurrentUserByDocumentID(uid);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            userNameTxtView.setText(username);
            String profilePicURL = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));

            Glide.with(getContext())
                    .load(profilePicURL) // Replace with your drawable resource
                    .into(profileImage);
        }

        NavController navController = NavHostFragment.findNavController(this);
        manageChildrenCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_ManageChildren);
            }
        });
        settingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_Setting);
            }
        });
        editProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_EditProfile);
            }
        });


        return view;
    }
}