package com.ankineri.gwwcompanion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.Arrays;
import java.util.List;

public class ConnectIqHelper {
    final String garminAppId = "0d79495d-c7f0-4040-b897-ecc61b3b5a6d";

    private final Context context;
    private final LocationManager locationManager;

    public ConnectIqHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
                            Log.d("GWW", "Send message status: " + iqMessageStatus.name());
                            try {
                                connectIQ.shutdown(context);
                            } catch (InvalidStateException e) {
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
        });
        t.start();
    }

    void getDataAndRun(final LocationProviderService.ICIQRunner callback) {
        Log.d("GWW", "Creating ConnectIq connection");
        final ConnectIQ connectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS);
        //final ConnectIQ connectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.TETHERED);
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

    public void connectAndSend() {
        getDataAndRun(new LocationProviderService.ICIQRunner() {
            @Override
            public void onGotData(ConnectIQ connectIQ, IQDevice device, IQApp app) {
                sendLocation(device, app, connectIQ);
            }
        });
    }
}
