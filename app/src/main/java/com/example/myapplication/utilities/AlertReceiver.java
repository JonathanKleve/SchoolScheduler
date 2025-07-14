package com.example.myapplication.utilities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.myapplication.R; // Replace with your app's R class

public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "course_end_channel"; // Use the same constant here

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notificationId", 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.scheduler_icon) // Use a proper icon for your app
                .setContentTitle("Alert!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("AlertReceiver", "Notification permission not granted");
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }
}
