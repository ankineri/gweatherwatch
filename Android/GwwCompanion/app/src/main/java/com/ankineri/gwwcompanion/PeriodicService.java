package com.ankineri.gwwcompanion;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PeriodicService extends Service {

    public PeriodicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service binding logic here
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the intent to start this service
        Intent serviceIntent = new Intent(this, PeriodicService.class);
        alarmIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        // Get a reference to the AlarmManager system service
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Schedule the first alarm to run immediately
//        scheduleAlarm(0);
    }


    private static final int ALARM_INTERVAL = 30 * 60 * 1000; // 30 minutes in milliseconds
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel any remaining alarms
        cancelAlarm();
    }

    private void scheduleAlarm(long delay) {
        // Schedule a repeating alarm to run this service every 30 minutes
        long triggerTime = System.currentTimeMillis() + delay;
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);
    }

    private void cancelAlarm() {
        // Cancel any remaining alarms for this service
        alarmManager.cancel(alarmIntent);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleAlarm(ALARM_INTERVAL);
        Shared.lastServiceRun.postValue(new Date());
        Shared.isServiceStarted.postValue(true);
        if (System.currentTimeMillis() <= Shared.serviceNoTouchUntil) {
            Log.d("GWW", "Not touching Garmin from service");
            return START_STICKY;
        }
        Log.d("GWW", "In the scheduled job - running send message");
        new ConnectIqHelper(this.getApplicationContext()).connectAndSend();
        // Service execution logic here
        return START_STICKY;
    }
}
