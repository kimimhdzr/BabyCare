package com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.babycare.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Locale;

public class AddEventDialog extends DialogFragment {

    private TextView startTime;
    private TextView endTime;
    private EditText title;
    private EditText description;
    private Button btnAdd, btnCancel;

    private OnEventAddedListener listener;

    // Define the listener interface
    public interface OnEventAddedListener {
        void onEventAdded(String title, String description, String startTime, String endTime);
    }

    // Setter for the listener
    public void setOnEventAddedListener(OnEventAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_dialog_box, container, false);

        startTime = view.findViewById(R.id.TVStartTime);
        endTime = view.findViewById(R.id.TVEndTime);
        title = view.findViewById(R.id.ETTitle);
        description = view.findViewById(R.id.ETDescription);
        //btnAdd = view.findViewById(R.id.btnAdd);
        //btnCancel = view.findViewById(R.id.btnCancel);

        startTime.setOnClickListener(v -> showMaterialTimePicker(startTime));
        endTime.setOnClickListener(v -> showMaterialTimePicker(endTime));

        btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventAdded(
                        title.getText().toString(),
                        description.getText().toString(),
                        startTime.getText().toString(),
                        endTime.getText().toString()
                );
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void showMaterialTimePicker(TextView textView) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        timePicker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");

        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = hour >= 12 ? "PM" : "AM";
            hour = hour > 12 ? hour - 12 : hour == 0 ? 12 : hour;

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm);
            textView.setText(selectedTime);
        });
    }
}
