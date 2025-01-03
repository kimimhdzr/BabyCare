
package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

        session_baby = new Baby("Daddy Hafiz","Baby Kimmy","-O","2000-05-23");
        calculateMonthsandWeeks();

        TextView babyname = view.findViewById(R.id.baby_name);
        babyname.setText(session_baby.getName());

        TextView babyage = view.findViewById(R.id.baby_age);
        if(session_baby.getYears() == 0){
            babyage.setText(session_baby.getMonth() +" months");
        }else{
            babyage.setText(session_baby.getYears() + " years, " + session_baby.getMonth() + " months");
        }



        Bundle bundle = new Bundle();
        bundle.putSerializable("Baby", session_baby);

        view.findViewById(R.id.toBabyProfile).setOnClickListener(v ->

                Navigation.findNavController(v).navigate(R.id.nav_to_BabyProfile,bundle)
        );

        view.findViewById(R.id.toRoutineTracker).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_to_RoutineTracker,bundle)
        );

        view.findViewById(R.id.toBabyWall).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_to_BabyWall,bundle)
        );




        return view;
    }


    public void calculateMonthsandWeeks(){

        LocalDate birthday = LocalDate.parse(session_baby.getBirthday());
        LocalDate today = LocalDate.now();

        Period diff = Period.between(birthday,today);

        int weeks = diff.getDays()/7;

        session_baby.setYears(diff.getYears());
        session_baby.setMonth(diff.getMonths());
        session_baby.setWeek(diff.getMonths());
    }
}