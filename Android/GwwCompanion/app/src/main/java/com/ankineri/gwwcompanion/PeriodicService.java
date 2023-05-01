package com.ankineri.gwwcompanion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
        // Service initialization logic here
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GWW", "In the scheduled job - running send message");
        new ConnectIqHelper(this.getApplicationContext()).connectAndSend();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel("fg", "fg");
            Notification.Builder builder = new Notification.Builder(this, "fg");
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setContentText("Hello");
            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, notification);
            startForeground(1, notification);
        }
        // Service execution logic here
        return START_STICKY;
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
}
