package com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar.receiver.EventReminderReceiver;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private List<CalendarClass> calendarItemList;
    private CalendarAdapter adapter;
    private TextView TVDateDisplay;
    private String selectedDate;
    private Button BtnSetDate;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FrameLayout bottomSheetDrawer;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar);

        // Initialize the BottomSheetBehavior
        bottomSheetDrawer = findViewById(R.id.bottomSheetDrawer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDrawer);

        bottomSheetBehavior.setPeekHeight(950);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        recyclerView = findViewById(R.id.RVCalendarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TVDateDisplay = findViewById(R.id.TVDateDisplay);
        BtnSetDate = findViewById(R.id.BtnSetDate);

        calendarItemList = new ArrayList<>();
        adapter = new CalendarAdapter(calendarItemList);
        recyclerView.setAdapter(adapter);

        adapter.setOnEventClickListener(event -> showEventDetailsDialog(event));

        setCurrentDate();

        calendarView = findViewById(R.id.calendarView);

        // Calendar selection listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            selectedDate = dateFormat.format(selectedCalendar.getTime());
            TVDateDisplay.setText(selectedDate);
            Log.d("Selected Date", "Selected Date: " + selectedDate); // Debug log for selected date
        });

        BtnSetDate.setOnClickListener(v -> showAddEventDialog());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with notifications
            } else {
                // Permission denied, show a message
            }
        }
    }

    private void setCurrentDate() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Set the current date to the TextView
        TVDateDisplay.setText(currentDate);
        selectedDate = currentDate;  // Set the initial date to today
    }

    private void showAddEventDialog() {
        // Create dialog to input event details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_dialog_box, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.ETTitle);
        EditText descriptionInput = dialogView.findViewById(R.id.ETDescription);
        TextView startTimeText = dialogView.findViewById(R.id.TVStartTime);
        TextView endTimeText = dialogView.findViewById(R.id.TVEndTime);
        Button BtnAddEvent = dialogView.findViewById(R.id.BtnAdd);
        Button BtnCancelEvent = dialogView.findViewById(R.id.BtnCancel);

        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_card_border));

        // Show the dialog
        dialog.show();

        // Set the start and end time to current time and 1 hour later by default
        setDefaultTimes(startTimeText, endTimeText);

        // Show the time picker for start and end time
        startTimeText.setOnClickListener(v -> showMaterialTimePicker(startTimeText));
        endTimeText.setOnClickListener(v -> showMaterialTimePicker(endTimeText));

        // Handle the Add button click
        BtnAddEvent.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String startTime = startTimeText.getText().toString().trim();
            String endTime = endTimeText.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || selectedDate == null) {
                showErrorDialog("All fields must be filled out, including selecting a date from the calendar.");
            } else {
                CalendarClass calendarEvent = new CalendarClass(title, description, startTime, endTime, selectedDate);

                // Add the event to the adapter
                adapter.addEvent(calendarEvent);

                // Schedule a notification for this event
                scheduleNotification(calendarEvent);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Handle the Cancel button click
        BtnCancelEvent.setOnClickListener(v -> {
            // Dismiss the dialog when Cancel is clicked
            dialog.dismiss();
        });

        // Ensure the BottomSheet stays expanded when the dialog is shown
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showEventDetailsDialog(CalendarClass event) {
        // Create a dialog to show event details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_detail_dialog_box, null);
        builder.setView(dialogView);

        // Set the event details in the dialog
        TextView dateTextView = dialogView.findViewById(R.id.TVDateDetail);
        TextView titleTextView = dialogView.findViewById(R.id.TVTitleDetail);
        TextView descriptionTextView = dialogView.findViewById(R.id.TVDescriptionDetail);
        TextView startTimeTextView = dialogView.findViewById(R.id.TVStartTime);
        TextView endTimeTextView = dialogView.findViewById(R.id.TVEndTime);
        Button BtnEditEvent = dialogView.findViewById(R.id.BtnEdit);
        Button BtnDeleteEvent = dialogView.findViewById(R.id.BtnDelete);

        dateTextView.setText(event.getDate());
        titleTextView.setText(event.getTitle());
        descriptionTextView.setText(event.getDescription());
        startTimeTextView.setText(event.getStartTime());
        endTimeTextView.setText(event.getEndTime());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_card_border));
        dialog.show();

        // Handle Edit button click
        BtnEditEvent.setOnClickListener(v -> {
            dialog.dismiss(); // Close current dialog
            showEditEventDialog(event); // Open Edit Dialog
        });

        // Handle Delete button click
        BtnDeleteEvent.setOnClickListener(v -> {
            adapter.removeEvent(event); // Remove event from adapter
            dialog.dismiss(); // Close current dialog
        });
    }

    private void showEditEventDialog(CalendarClass event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_dialog_box, null);
        builder.setView(dialogView);

        TextView addDetails = dialogView.findViewById(R.id.TVAddDetails);
        EditText titleInput = dialogView.findViewById(R.id.ETTitle);
        EditText descriptionInput = dialogView.findViewById(R.id.ETDescription);
        TextView startTimeText = dialogView.findViewById(R.id.TVStartTime);
        TextView endTimeText = dialogView.findViewById(R.id.TVEndTime);
        Button BtnSaveEvent = dialogView.findViewById(R.id.BtnAdd);
        Button BtnCancelEvent = dialogView.findViewById(R.id.BtnCancel);

        // Populate fields with existing event data
        titleInput.setText(event.getTitle());
        descriptionInput.setText(event.getDescription());
        startTimeText.setText(event.getStartTime());
        endTimeText.setText(event.getEndTime());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_card_border));
        dialog.show();

        startTimeText.setOnClickListener(v -> showMaterialTimePicker(startTimeText));
        endTimeText.setOnClickListener(v -> showMaterialTimePicker(endTimeText));

        addDetails.setText("Update Details");
        BtnSaveEvent.setText("Save"); // Change button text to "Save"

        BtnSaveEvent.setOnClickListener(v -> {
            String newTitle = titleInput.getText().toString().trim();
            String newDescription = descriptionInput.getText().toString().trim();
            String newStartTime = startTimeText.getText().toString().trim();
            String newEndTime = endTimeText.getText().toString().trim();

            if (newTitle.isEmpty() || newDescription.isEmpty() || newStartTime.isEmpty() || newEndTime.isEmpty()) {
                showErrorDialog("All fields must be filled out.");
            } else {
                event.setTitle(newTitle);
                event.setDescription(newDescription);
                event.setStartTime(newStartTime);
                event.setEndTime(newEndTime);

                adapter.notifyDataSetChanged(); // Update the RecyclerView
                dialog.dismiss();
            }
        });

        BtnCancelEvent.setOnClickListener(v -> dialog.dismiss());
    }

    @SuppressLint("ScheduleExactAlarm")
    public void scheduleNotification(CalendarClass event) {
        // Check if the event time is in the future
        Calendar currentCalendar = Calendar.getInstance();
        Calendar eventCalendar = Calendar.getInstance();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy hh:mm a", Locale.getDefault());
            // Set the event's date and time to the calendar
            eventCalendar.setTime(sdf.parse(event.getDate() + " " + event.getStartTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // If the event time is in the past, do not schedule the notification
        if (eventCalendar.before(currentCalendar)) {
            // Optionally, show a message to the user
            Toast.makeText(this, "Event time has already passed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check permissions for exact alarms (for devices running Android 12 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        // Proceed to schedule the notification for future events
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, EventReminderReceiver.class);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("time", event.getStartTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set the alarm for the event time
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                eventCalendar.getTimeInMillis(),
                pendingIntent
        );
    }

    private void setDefaultTimes(TextView startTimeText, TextView endTimeText) {
        Calendar currentTime = Calendar.getInstance();

        // Set the start time to the current hour and minute
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        String startTime = String.format(Locale.getDefault(), "%02d:%02d AM", hour > 12 ? hour - 12 : hour, minute);

        // Set the end time to 1 hour after the current time
        currentTime.add(Calendar.HOUR_OF_DAY, 1); // Add 1 hour
        hour = currentTime.get(Calendar.HOUR_OF_DAY);
        minute = currentTime.get(Calendar.MINUTE);
        String endTime = String.format(Locale.getDefault(), "%02d:%02d AM", hour > 12 ? hour - 12 : hour, minute);

        // Set the times to the TextViews
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);
    }

    private void showErrorDialog(String message) {
        // Create an AlertDialog to show the error message
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showMaterialTimePicker(TextView timeTextView) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");

        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = hour >= 12 ? "PM" : "AM";
            hour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm);
            timeTextView.setText(selectedTime);
        });
    }
}

