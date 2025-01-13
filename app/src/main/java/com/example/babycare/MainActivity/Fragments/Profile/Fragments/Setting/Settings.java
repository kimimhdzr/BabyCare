package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.AuthActivity.AuthActivity;
import com.example.babycare.DataBinding.Cache.PostCache;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends Fragment {
    private FirebaseAuth mAuth;
    private SwitchMaterial switchnotification;
    private BottomNavigationView bottomNavigationView;
    private ImageButton navtoeditprofile, navtochangepassword, navtoappinformation, navtosupport, navtologout, backbutton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        switchnotification = view.findViewById(R.id.switchnotification);
        navtoeditprofile = view.findViewById(R.id.navtoeditprofile);
        navtochangepassword = view.findViewById(R.id.navtochangepassword);
        navtoappinformation = view.findViewById(R.id.navtoappinformation);
        navtosupport = view.findViewById(R.id.navtosupport);
        navtologout = view.findViewById(R.id.navtologout);
        backbutton = view.findViewById(R.id.backbutton);


        NavController navController = NavHostFragment.findNavController(this);
        navtoeditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_EditProfile);
            }
        });
        navtochangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_ChangePass);
            }
        });
        navtoappinformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_AppInfo);
            }
        });
        navtosupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_Support);
            }
        });
        navtologout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Use 'getActivity()' if in a Fragment
                builder.setTitle("Confirmation")
                        .setMessage("Do you want to logout now?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            signOut();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });


        bottomNavigationView = ((MainActivity) getActivity()).findViewById(R.id.bottomNavView);
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


    private void signOut() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");
        dbHelper.deleteAllData();
        PostCache.getInstance().clearCache();

        // Optionally, redirect the user to the login screen
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}