package com.ankineri.gwwcompanion;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
//import android.widget.Toast;

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
    private ConnectIQ mConnectIQ;
    private IQApp mApp;
    private IQDevice mDevice;

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
                    Log.w("GWW", "No location permissions!");
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

    MutableLiveData<Boolean> mUpdateStatusTo = null;

    public void registerStatus(MutableLiveData<Boolean> status) {
        mUpdateStatusTo = status;
    }

    public void connect(boolean showMessages) {
        mConnectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS);
        mConnectIQ.initialize(context, showMessages, new ConnectIQ.ConnectIQListener() {
            @Override
            public void onSdkReady() {
                Log.d("GWW", "SDK ready");
                List<IQDevice> paired = null;
                try {
                    paired = mConnectIQ.getKnownDevices();
                    if (paired != null && paired.size() > 0) {
                        for (final IQDevice device : paired) {
                            mApp = new IQApp(garminAppId);
                            mDevice = device;
                            if (mUpdateStatusTo != null) {
                                mUpdateStatusTo.setValue(true);
                            }
                            if (mConnectedCallback != null) {
                                mConnectedCallback.onGotData(mConnectIQ, device, mApp);
                            }
                        }
                    }
                } catch (InvalidStateException e) {
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                } catch (ServiceUnavailableException e) {
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                }
            }

            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
                Log.d("GWW", "Init error: " + iqSdkErrorStatus.toString());
                if (mUpdateStatusTo != null) {
                    mUpdateStatusTo.setValue(false);
                }
            }

            @Override
            public void onSdkShutDown() {
                Log.d("GWW", "SDK shutdown");
                if (mUpdateStatusTo != null) {
                    mUpdateStatusTo.setValue(false);
                }
            }
        });
    }
    LocationProviderService.ICIQRunner mConnectedCallback = null;
    public void connectAndSend() {
        mConnectedCallback = new LocationProviderService.ICIQRunner() {
            @Override
            public void onGotData(ConnectIQ connectIQ, IQDevice device, IQApp app) {
                sendLocation(device, app, connectIQ);
            }
        };
        connect(false);
    }
}
