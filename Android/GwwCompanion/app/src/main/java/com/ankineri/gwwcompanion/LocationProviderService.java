package com.ankineri.gwwcompanion;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQConnectType;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;

public class LocationProviderService extends Service {

    class Runner implements Runnable {
        private final Context context;

        public Runner(Context context) {
            this.context = context;
        }
        @Override
        public void run() {
            Looper.prepare();
            Log.d("GWW", "Creating ConnectIq connection");
            final ConnectIQ connectIQ = ConnectIQ.getInstance(context, IQConnectType.WIRELESS);
            connectIQ.initialize(context, true, new ConnectIQ.ConnectIQListener() {
                @Override
                public void onSdkReady() {
                    Log.d("GWW", "SDK ready");
                    Toast.makeText(context, "ConnectIQ initialized OK", Toast.LENGTH_SHORT).show();
                    List<IQDevice> paired = null;
                    try {
                        paired = connectIQ.getKnownDevices();
                    } catch (InvalidStateException e) {
                        e.printStackTrace();
                    } catch (ServiceUnavailableException e) {
                        e.printStackTrace();
                    }

                    if (paired != null && paired.size() > 0) {
                        // get the status of the devices
                        for (IQDevice device : paired) {
                            IQDevice.IQDeviceStatus status = null;
                            try {
                                status = connectIQ.getDeviceStatus(device);
                            } catch (InvalidStateException e) {
                                e.printStackTrace();
                            } catch (ServiceUnavailableException e) {
                                e.printStackTrace();
                            }
                            Log.d("GWW", "Found device " + device.getFriendlyName());
                            if (status == IQDevice.IQDeviceStatus.CONNECTED) {
                                Log.d("GWW", "Found connected device!");
                            }
                        }
                    }
                }

                @Override
                public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
                    Log.d("GWW", "Init error: " + iqSdkErrorStatus.toString());
                    Toast.makeText(context, "ConnectIQ initialization failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSdkShutDown() {
                    Log.d("GWW", "SDK shutdown");
                    Toast.makeText(context, "ConnectIQ SDK shutdown", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public LocationProviderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Thread runningThread = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForeground(startId, new Notification());
        }
        if (runningThread != null && runningThread.isAlive()) {
            Log.d("GWW", "Thread is already running, doing nothing");
        } else {
            Log.d("GWW", "Starting service thread");

            Thread t = new Thread(new Runner(this.getApplicationContext()));
            t.start();
        }

        if (intent != null) {
            BootNotificationReceiver.completeWakefulIntent(intent);
        }

        this.stopForeground(true);
        return Service.START_STICKY;
    }
}
