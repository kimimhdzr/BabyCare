package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.babycare.MainActivity.Fragments.Profile.Adapter.AppInfoAdapter;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppInformation extends Fragment {

    private ImageButton backbutton;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerview;
    List<String> QuestionsList, DetailsList;
    AppInfoAdapter appInfoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_information, container, false);


        backbutton = view.findViewById(R.id.close_icon);
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


        recyclerview = view.findViewById(R.id.recyclerview);


        QuestionsList = new ArrayList<>(Arrays.asList(
                getString(R.string.question1),
                getString(R.string.question2),
                getString(R.string.question3),
                getString(R.string.question4),
                getString(R.string.question5),
                getString(R.string.question6),
                getString(R.string.question7),
                getString(R.string.question8),
                getString(R.string.question9),
                getString(R.string.question10),
                getString(R.string.question11),
                getString(R.string.question12)
        ));
        DetailsList = new ArrayList<>(Arrays.asList(
                getString(R.string.detail1),
                getString(R.string.detail2),
                getString(R.string.detail3),
                getString(R.string.detail4),
                getString(R.string.detail5),
                getString(R.string.detail6),
                getString(R.string.detail7),
                getString(R.string.detail8),
                getString(R.string.detail9),
                getString(R.string.detail10),
                getString(R.string.detail11),
                getString(R.string.detail12)
        ));

        recyclerview = view.findViewById(R.id.recyclerview);
        appInfoAdapter = new AppInfoAdapter(
                getContext(),
                QuestionsList,
                DetailsList
        );
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setItemAnimator(new DefaultItemAnimator()); //add animation to the cards
        recyclerview.setAdapter(appInfoAdapter);



        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}