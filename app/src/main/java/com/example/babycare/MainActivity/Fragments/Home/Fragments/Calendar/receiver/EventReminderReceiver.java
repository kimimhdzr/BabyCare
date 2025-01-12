package com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.babycare.MainActivity.Fragments.Home.Fragments.Calendar.utils.NotificationUtils;

public class EventReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");

        NotificationUtils.showNotification(
                context,
                "Event Reminder: " + title,
                "Your event starts at: " + time
        );
    }
}
