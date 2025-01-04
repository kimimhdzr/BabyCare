package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.babycare.Objects.Baby;
import com.example.babycare.R;

import java.util.ArrayList;

public class BabyProfile extends Fragment {

    Baby session_baby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_baby_profile, container, false);
        session_baby = (Baby) getArguments().getSerializable("Baby");

        TextView baby_name = view.findViewById(R.id.baby_name);
        baby_name.setText(session_baby.getName());

        TextView baby_age = view.findViewById(R.id.baby_age);
        if (session_baby.getYears()==0){
            baby_age.setText(session_baby.getMonth()+" months," + session_baby.getWeek()+" weeks");
        }else{
            baby_age.setText(session_baby.getYears()+" years, "+session_baby.getMonth()+" months");
        }

        TextView baby_blood = view.findViewById(R.id.baby_blood);
        baby_blood.setText(session_baby.getBloodtype());

        TextView baby_height = view.findViewById(R.id.baby_height);
        baby_height.setText(session_baby.getHeight()+" cm");

        TextView baby_weight = view.findViewById(R.id.baby_weight);
        baby_weight.setText(session_baby.getWeight()+" kg");

        RecyclerView allergies_list = view.findViewById(R.id.allergy_list);
        AllergyAdapter allergyAdapter = new AllergyAdapter(session_baby.getAllAllergies());
        allergies_list.setAdapter(allergyAdapter);
        allergies_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));


        ImageButton edit_button = view.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle baby_bundle = new Bundle();
                baby_bundle.putSerializable("Baby",session_baby);
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
}