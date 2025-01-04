
package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babycare.Objects.Baby;
import com.example.babycare.R;

import java.time.LocalDate;
import java.time.Period;

public class BabyHome extends Fragment {

    Baby session_baby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_baby_home, container, false);



        Bundle bundle = getArguments();
        session_baby = (Baby) bundle.getSerializable("session_baby");
        calculateMonthsandWeeks();

        view.findViewById(R.id.toBabyProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle baby_bundle = new Bundle();
                baby_bundle.putSerializable("Baby",session_baby);
                Navigation.findNavController(view).navigate(R.id.nav_to_BabyProfile,baby_bundle);
            }
        });

        view.findViewById(R.id.toRoutineTracker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle baby_bundle = new Bundle();
                baby_bundle.putSerializable("Baby",session_baby);
                Navigation.findNavController(view).navigate(R.id.nav_to_RoutineTracker,baby_bundle);
            }
        });

        view.findViewById(R.id.toBabyWall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle baby_bundle = new Bundle();
                baby_bundle.putSerializable("Baby",session_baby);
                Navigation.findNavController(view).navigate(R.id.nav_to_BabyWall,baby_bundle);

            }
        });

        TextView babyname_display = view.findViewById(R.id.baby_name);
        babyname_display.setText(session_baby.getName());

        TextView babyage_display = view.findViewById(R.id.baby_age);
        if (session_baby.getYears()==0){
            babyage_display.setText(session_baby.getMonth()+" months," + session_baby.getWeek()+" weeks");
        }else{
            babyage_display.setText(session_baby.getYears()+" years, "+session_baby.getMonth()+" months");
        }

        TextView babyweek_display = view.findViewById(R.id.babyweek);
        babyweek_display.setText(String.valueOf(session_baby.getWeek()));

        TextView babymonth_display = view.findViewById(R.id.month_display);
        babymonth_display.setText(String.valueOf(session_baby.getMonth()));




        return view;
    }


    public void calculateMonthsandWeeks(){

        LocalDate birthday = LocalDate.parse(session_baby.getBirthday());
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
}