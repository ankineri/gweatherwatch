package com.ankineri.gwwcompanion;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class MyPackageNotificationReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GWW", "My package changed notification!");
        Intent theIntent = new Intent(context, LocationProviderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(theIntent);
        }
        else {
            context.startService(theIntent);
        }
    }
}
