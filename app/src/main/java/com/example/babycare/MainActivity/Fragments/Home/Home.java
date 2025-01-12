package com.example.babycare.MainActivity.Fragments.Home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.babycare.MainActivity.SharedUserModel;
import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.Tip;
import com.example.babycare.Objects.User;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Home extends Fragment implements AdapterInt {


    private TipsCircle tipsCircle;
    private User session_user;

    private SharedUserModel sharedUserModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        ViewPager2 tips_cards = view.findViewById(R.id.tipsViewPager);
        RecyclerView circles = view.findViewById(R.id.circle_recycler);

        Tip tip1 = new Tip("START YOUR DAY","start your day with a good night sleep and a good morning routine and a healthy breakfast");
        Tip tip2 = new Tip("Healthy Nutrient","A healthy nutrient is a very vital aspect in ensuring your child's growth");

        ArrayList<Tip> tips = new ArrayList<>();
        tips.add(tip1);
        tips.add(tip2);

        TipsAdapter tipsAdapter = new TipsAdapter(tips, this);
        tipsCircle = new TipsCircle(tips);

        tips_cards.setAdapter(tipsAdapter);
        circles.setAdapter(tipsCircle);

        circles.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        tips_cards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the second adapter's selected position
                tipsCircle.updateSelectedPosition(position);
            }
        });




        RelativeLayout toCalendar = view.findViewById(R.id.toCalendarPage);
        toCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_to_Calendar);
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(int position) {
        // Update the second adapter's selected position when an item is selected in the first adapter
        tipsCircle.updateSelectedPosition(position);
    }
}