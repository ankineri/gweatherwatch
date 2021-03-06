package com.ankineri.gwwcompanion;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQConnectType;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.Arrays;
import java.util.List;

public class LocationProviderService extends Service {
    final String garminAppId = "0d79495d-c7f0-4040-b897-ecc61b3b5a6d";
    private static final int HOUR = 1000 * 60 * 60;
    private static final int MINUTE = 1000 * 60;

    interface ICIQRunner {
        void onGotData(final ConnectIQ connectIQ, final IQDevice device, final IQApp app);
    }

    class Runner implements Runnable {
        private final Context context;
        LocationManager locationManager;

        public Runner(Context context) {
            this.context = context;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        }

        void sendLocation(final IQDevice device, final IQApp app, final ConnectIQ connectIQ) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(context, "No location permissions!", Toast.LENGTH_SHORT).show();
                        new PermissionsGranter().getPermissions(context);
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    Log.d("GWW", "Got location: " + location.toString());
                    List<Float> toSend = Arrays.asList((float) location.getLatitude(), (float) location.getLongitude());
                    try {
                        Log.d("GWW", "Sending " + toSend);
                        connectIQ.sendMessage(device, app, toSend, new ConnectIQ.IQSendMessageListener() {
                            @Override
                            public void onMessageStatus(IQDevice iqDevice, IQApp iqApp, ConnectIQ.IQMessageStatus iqMessageStatus) {

                            }
                        });
                    } catch (InvalidStateException e) {
                        e.printStackTrace();
                    } catch (ServiceUnavailableException e) {
                        e.printStackTrace();
                    }

                }
            });
            t.start();
        }

        void getDataAndRun(final ICIQRunner callback) {
            Looper.prepare();
            Log.d("GWW", "Creating ConnectIq connection");
            final ConnectIQ connectIQ = ConnectIQ.getInstance(context, IQConnectType.WIRELESS);
            connectIQ.initialize(context, true, new ConnectIQ.ConnectIQListener() {
                @Override
                public void onSdkReady() {
                    Log.d("GWW", "SDK ready");
                    List<IQDevice> paired = null;
                    try {
                        paired = connectIQ.getKnownDevices();
                        if (paired != null && paired.size() > 0) {
                            for (final IQDevice device : paired) {
                                IQApp app = new IQApp(garminAppId);
                                callback.onGotData(connectIQ, device, app);
                            }
                        }
                    } catch (InvalidStateException e) {
                        e.printStackTrace();
                    } catch (ServiceUnavailableException e) {
                        e.printStackTrace();
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
                }
            });
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
                        for (final IQDevice device : paired) {
                            IQDevice.IQDeviceStatus status = null;
                            try {


                                connectIQ.registerForDeviceEvents(device, new ConnectIQ.IQDeviceEventListener() {
                                    @Override
                                    public void onDeviceStatusChanged(IQDevice iqDevice, IQDevice.IQDeviceStatus iqDeviceStatus) {
                                        Log.d("GWW", "Device " + iqDevice.getFriendlyName() + " is now in status " + iqDeviceStatus.name());

                                        if (iqDeviceStatus == IQDevice.IQDeviceStatus.CONNECTED) {

                                            try {
                                                connectIQ.getApplicationInfo(garminAppId, device, new ConnectIQ.IQApplicationInfoListener() {
                                                    @Override
                                                    public void onApplicationInfoReceived(IQApp iqApp) {
                                                        Log.d("GWW", "Have app info for " + iqApp.getDisplayName());
                                                        try {
                                                            connectIQ.registerForAppEvents(device, iqApp, new ConnectIQ.IQApplicationEventListener() {
                                                                @Override
                                                                public void onMessageReceived(IQDevice iqDevice, IQApp iqApp, List<Object> list, ConnectIQ.IQMessageStatus iqMessageStatus) {
                                                                    Log.d("GWW", "Have message: " + list.get(0).toString());
                                                                    sendLocation(iqDevice, iqApp, connectIQ);
                                                                }
                                                            });
                                                        } catch (InvalidStateException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onApplicationNotInstalled(String s) {
                                                        Log.d("GWW", "Have NO application, requesting install " + s);
                                                        try {
                                                            connectIQ.openStore(garminAppId);
                                                        } catch (InvalidStateException e) {
                                                            e.printStackTrace();
                                                        } catch (ServiceUnavailableException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } catch (InvalidStateException e) {
                                                e.printStackTrace();
                                            } catch (ServiceUnavailableException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                });
                            } catch (InvalidStateException e) {
                                e.printStackTrace();
                            }
                            Log.d("GWW", "Registered for device " + device.getFriendlyName());
                            IQApp app = new IQApp(garminAppId);
                            try {
                                connectIQ.registerForAppEvents(device, app, new ConnectIQ.IQApplicationEventListener() {
                                    @Override
                                    public void onMessageReceived(IQDevice iqDevice, IQApp iqApp, List<Object> list, ConnectIQ.IQMessageStatus iqMessageStatus) {
                                        Log.d("GWW", "Have message: " + list.get(0).toString());
                                        sendLocation(iqDevice, iqApp, connectIQ);
                                    }
                                });
                            } catch (InvalidStateException e) {
                                e.printStackTrace();
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
            this.startForeground(startId, new Notification(R.drawable.ic_launcher_foreground, "gWeatherWatch companion", Notification.PRIORITY_LOW));
        }

        new PermissionsGranter().getPermissions(this.getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Log.d("GWW", "Scheduling the work");
            JobScheduler sched = (JobScheduler) this.getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(0,
                    new ComponentName(this.getApplicationContext().getPackageName(), PeriodicService.class.getName()))
                    .setBackoffCriteria(15 * MINUTE, JobInfo.BACKOFF_POLICY_LINEAR)
//                    .setMinimumLatency(0)
                    .setPeriodic((long) (15 * MINUTE))
                    .setPersisted(true);
            sched.schedule(builder.build());
        } else {
            if (runningThread != null && runningThread.isAlive()) {
                Log.d("GWW", "Thread is already running, doing nothing");
            } else {
                Log.d("GWW", "Starting service thread");

                Thread t = new Thread(new Runner(this.getApplicationContext()));
                t.start();
            }
        }
        if (intent != null) {
            BootNotificationReceiver.completeWakefulIntent(intent);
        }

        return Service.START_STICKY;
    }
}
