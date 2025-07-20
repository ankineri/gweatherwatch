package com.ankineri.gwwcompanion;


import static android.content.Context.POWER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PeriodicWorker extends Worker {
    static final int DELAY_MINUTES = 15;
    public static void schedule(Context context) {
        PeriodicWorkRequest wr = new PeriodicWorkRequest.Builder(PeriodicWorker.class, DELAY_MINUTES, TimeUnit.MINUTES).build();
        Operation op = WorkManager.getInstance(context).enqueueUniquePeriodicWork("update", ExistingPeriodicWorkPolicy.KEEP, wr);
        try {
            op.getResult().get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void runOnce(Context context) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(PeriodicWorker.class).build();
        Operation op = WorkManager.getInstance(context).enqueueUniqueWork("oneoff", ExistingWorkPolicy.REPLACE, req);
        try {
            op.getResult().get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public PeriodicWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    static long lastRan = 0;
    @NonNull
    @Override
    public Result doWork() {
        Log.d("GWW", "In worker!");
        if (System.currentTimeMillis() < lastRan + 10 * 1000L) {
            Log.d("GWW", "Running too soon - quit");
            return Result.success();
        }
        lastRan = System.currentTimeMillis();
        Shared.lastServiceRun.postValue(new Date());
        Shared.isServiceStarted.postValue(true);
        if (System.currentTimeMillis() <= Shared.serviceNoTouchUntil) {
            Log.d("GWW", "Not touching Garmin from service");
            return Result.success();
        }
        Shared.takeWakeLock(getApplicationContext());

        Log.d("GWW", "In the scheduled job - running send message");
        new ConnectIqHelper(this.getApplicationContext(), false).connectAndSend();
        return Result.success();
    }
}
