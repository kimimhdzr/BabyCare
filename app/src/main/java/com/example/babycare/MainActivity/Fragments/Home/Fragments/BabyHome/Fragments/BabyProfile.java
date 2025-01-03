package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.babycare.Objects.Baby;
import com.example.babycare.R;

public class BabyProfile extends Fragment {

    Baby session_baby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_baby_profile, container, false);
        session_baby = (Baby) getArguments().getSerializable("Baby");

        RecyclerView allergies_list = view.findViewById(R.id.allergy_list);

















        return view;
    }
}