package com.ankineri.gwwcompanion;

import static android.content.Context.POWER_SERVICE;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.Date;

public class Shared {
    public static MutableLiveData<Boolean> isServiceStarted = new MutableLiveData<>(Boolean.FALSE);
    public static MutableLiveData<Boolean> isGarminConnected = new MutableLiveData<>(Boolean.FALSE);

    public static MutableLiveData<Boolean> isSetupComplete = new MutableLiveData<>(Boolean.FALSE);

    public static MutableLiveData<Date> lastSuccessfulSend = new MutableLiveData<>(null);
    public static MutableLiveData<Date> lastServiceRun = new MutableLiveData<>(null);

    public static long serviceNoTouchUntil = 0;
    public static PowerManager.WakeLock wakeLock = null;

    synchronized public static void takeWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GWW::Wake");
        }
        if (wakeLock.isHeld()) {
            return;
        }
        wakeLock.acquire(5 * 1000L);
    }

    synchronized public static void releaseWakeLock(Context context) {
        Log.d("GWW", "Attempting to release wake lock");
        if (wakeLock == null) {
            Log.d("GWW", "No wakelock to release.");
            return;
        }
        if (wakeLock.isHeld()) {
            Log.d("GWW", "Releasing wakelock");
            wakeLock.release();
        } else {
            Log.d("GWW", "NOT releasing wakelock - not held");
        }
        wakeLock = null;
    }

}
