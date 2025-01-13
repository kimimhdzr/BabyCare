package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.AllergyAdapter;
import com.example.babycare.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class BabyProfile extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MyDatabaseHelper dbHelper;
    FirebaseUser currentUser;
    BabyProfileModel session_baby;
    ShapeableImageView baby_pfp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_baby_profile, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();


        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");



        Bundle bundle = getArguments();
        session_baby = (BabyProfileModel) bundle.getSerializable("Baby");
        calculateMonthsandWeeks();


        calculateMonthsandWeeks();



        TextView baby_name = view.findViewById(R.id.baby_name);
        baby_name.setText(session_baby.getName());

        baby_pfp = view.findViewById(R.id.baby_pfp);
        Glide.with(getContext())
                .load(session_baby.getProfilePic())  // Pass the image URL
                .into(baby_pfp);  // Set the ImageView to display the image

        TextView baby_age = view.findViewById(R.id.baby_age);
        if (session_baby.getYears()==0){
            baby_age.setText(session_baby.getMonth()+" months," + session_baby.getWeek()+" weeks");
        }else{
            baby_age.setText(session_baby.getYears()+" years, "+session_baby.getMonth()+" months");
        }

        TextView baby_blood = view.findViewById(R.id.baby_blood);
        baby_blood.setText(session_baby.getBloodType());

        TextView baby_height = view.findViewById(R.id.baby_height);
        baby_height.setText(session_baby.getHeight()+" cm");

        TextView baby_weight = view.findViewById(R.id.baby_weight);
        baby_weight.setText(session_baby.getWeight()+" kg");


        RecyclerView allergies_list = view.findViewById(R.id.allergy_list);
        AllergyAdapter allergyAdapter = new AllergyAdapter(session_baby.getAllergies());
        allergies_list.setAdapter(allergyAdapter);
        allergies_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));


        ImageButton edit_button = view.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle baby_bundle = new Bundle();
                baby_bundle.putSerializable("session_baby",session_baby);
                Navigation.findNavController(view).navigate(R.id.nav_to_EditBabyProfile,baby_bundle);
            }
        });


        ImageButton back_button = view.findViewById(R.id.blue_backbtn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).popBackStack();
            }
        });




        return view;
    }

    public void calculateMonthsandWeeks(){

        LocalDate birthday = LocalDate.parse(convertDateFormat(session_baby.getDob()));
        LocalDate today = LocalDate.now();

        Log.d("TEST","TR"+ birthday);
        Log.d("TEST","TR"+ today);

        Period diff = Period.between(birthday,today);
        Log.d("TEST","TR"+ diff);



        double weeks = diff.getDays() / 7.0;
        Log.d("TEST","TEST" + weeks);

        Log.d("TEST","TR"+ diff.getMonths());
        Log.d("TEST","TR"+ diff.getYears());
        Log.d("TEST","TR"+ weeks);

        session_baby.setYears(diff.getYears());
        session_baby.setMonth(diff.getMonths());
        session_baby.setWeek((int)weeks);
    }

    public String convertDateFormat(String originalDate) {
        try {
            // Define the input format (MMM dd, yyyy)
            SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy");

            // Parse the original date string into a Date object
            Date date = inputFormat.parse(originalDate);

            // Define the desired output format (yyyy-MM-dd)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Format the Date object into the desired string format
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null if there's an error
        }
    }
}