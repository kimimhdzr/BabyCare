package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.babycare.Objects.Baby;
import com.example.babycare.R;

import java.util.ArrayList;
import java.util.List;

public class EditBabyProfile extends Fragment {
    Baby session_baby;
    EditAllergyAdapter allergyAdapter;
    ArrayList<String> allergies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_baby_profile, container, false);

        Bundle bundle = getArguments();
        session_baby = (Baby) bundle.getSerializable("Baby");
        allergies = session_baby.getAllAllergies();

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
            allergyAdapter.updateList(session_baby.getAllAllergies());
        });
        allergy_list.setAdapter(allergyAdapter);
        allergy_list.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        Button add_allergy = view.findViewById(R.id.add_allergy);
        add_allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText allergy_input = view.findViewById(R.id.input_allergy);
                String new_allergy = allergy_input.getText().toString();
                session_baby.addAllergy(new_allergy);
                allergyAdapter.updateList(session_baby.getAllAllergies());
            }
        });















        return view;
    }
}