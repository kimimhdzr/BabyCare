package com.example.babycare.AuthActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.BabyHome;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.Objects.Baby;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AuthActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);



    }

    @Override
    public boolean onSupportNavigateUp() {
        // Allow for up navigation (back action)
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_auth);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}