package com.ankineri.gwwcompanion;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;
import android.util.Log;

public class BootNotificationReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GWW", "Boot (or update) notification!");
        PeriodicWorker.schedule(context);
    }
}
