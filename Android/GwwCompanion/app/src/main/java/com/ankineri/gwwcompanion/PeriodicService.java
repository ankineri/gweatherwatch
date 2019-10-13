package com.ankineri.gwwcompanion;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PeriodicService extends JobService {
    public PeriodicService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("GWW", "In the scheduled job - running send message");
        new ConnectIqHelper(this.getApplicationContext()).connectAndSend();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
