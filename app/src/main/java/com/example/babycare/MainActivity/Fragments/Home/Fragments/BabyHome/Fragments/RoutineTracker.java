package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.babycare.R;

public class RoutineTracker extends Fragment {
    private static final String PREFS_NAME = "BabyTimerPrefs";
    private static final String KEY_BATH = "last_bath_time";
    private static final String KEY_FOOD = "last_food_time";
    private static final String KEY_SLEEP = "last_sleep_time";
    private static final String KEY_CHANGE = "last_change_time";

    private TextView timerBath, timerFood, timerSleep, timerChange;
    private Button resetButtonBath, resetButtonFood, resetButtonSleep, resetButtonChange;
    private SharedPreferences sharedPreferences;
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_tracker, container, false);

        // Initialize Views
        timerBath = view.findViewById(R.id.timer_bath);
        timerFood = view.findViewById(R.id.timer_food);
        timerSleep = view.findViewById(R.id.timer_sleep);
        timerChange = view.findViewById(R.id.timer_change);

        resetButtonBath = view.findViewById(R.id.reset_button_bath);
        resetButtonFood = view.findViewById(R.id.reset_button_food);
        resetButtonSleep = view.findViewById(R.id.reset_button_sleep);
        resetButtonChange = view.findViewById(R.id.reset_button_change);

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        // Set up button listeners
        resetButtonBath.setOnClickListener(v -> resetTimer(KEY_BATH));
        resetButtonFood.setOnClickListener(v -> resetTimer(KEY_FOOD));
        resetButtonSleep.setOnClickListener(v -> resetTimer(KEY_SLEEP));
        resetButtonChange.setOnClickListener(v -> resetTimer(KEY_CHANGE));

        // Start the timer updates
        handler = new Handler();
        startTimerUpdates();

        return view;
    }

    private void resetTimer(String key) {
        long currentTime = System.currentTimeMillis();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, currentTime);
        editor.apply();
    }

    private void startTimerUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateTimer(timerBath, KEY_BATH);
                updateTimer(timerFood, KEY_FOOD);
                updateTimer(timerSleep, KEY_SLEEP);
                updateTimer(timerChange, KEY_CHANGE);

                // Repeat every second
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateTimer(TextView textView, String key) {
        long lastResetTime = sharedPreferences.getLong(key, 0);
        long currentTime = System.currentTimeMillis();

        if (lastResetTime != 0) {
            long elapsedTime = currentTime - lastResetTime;
            textView.setText(formatElapsedTime(elapsedTime));
        } else {
            textView.setText("Never");
        }
    }

    private String formatElapsedTime(long elapsedTimeMillis) {
        long seconds = elapsedTimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m " + seconds + "s ago";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s ago";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s ago";
        } else {
            return seconds + "s ago";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Stop the timer updates
    }
}