package com.ankineri.gwwcompanion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class BootNotificationReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GWW", "Boot notification!");

        Intent theIntent = new Intent(context, PeriodicService.class);
        context.startService(theIntent);
    }
}
