package com.ankineri.gwwcompanion;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
//import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ConnectIqHelper {
    MutableLiveData<String> status = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public LiveData<String> getStatus() {
        return status;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    interface ICIQRunner {
        void onGotData(final ConnectIQ connectIQ, final IQDevice device, final IQApp app);
    }

    final String garminAppId = "0d79495d-c7f0-4040-b897-ecc61b3b5a6d";

    private final boolean interactive;

    private final Context context;
    private final LocationManager locationManager;
    private ConnectIQ mConnectIQ;
    private IQApp mApp;
    private IQDevice mDevice;

    public ConnectIqHelper(Context context, boolean interactive) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.interactive = interactive;
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    static boolean requestedLocationUpdates = false;

    void sendLocation(final IQDevice device, final IQApp app, final ConnectIQ connectIQ) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 1000, 1000, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationManager.removeUpdates(this);
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                Location location = getLastKnownLocation();
                if (location == null) {
                    return;
                }
                Log.d("GWW", "Got location: " + location.toString());
                List<Float> toSend = Arrays.asList((float) location.getLatitude(), (float) location.getLongitude());
                try {
                    Log.d("GWW", "Sending " + toSend);
                    connectIQ.sendMessage(device, app, toSend, new ConnectIQ.IQSendMessageListener() {
                        @Override
                        public void onMessageStatus(IQDevice iqDevice, IQApp iqApp, ConnectIQ.IQMessageStatus iqMessageStatus) {
                            Log.d("GWW", "Send message status: " + iqMessageStatus.name());
                            if (iqMessageStatus == ConnectIQ.IQMessageStatus.SUCCESS) {
                                Shared.lastSuccessfulSend.postValue(new Date());
                            }
//                            try {
//                                connectIQ.shutdown(context);
//                            } catch (InvalidStateException e) {
//                                e.printStackTrace();
//                            }
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

    void setStatusIfInteractive(String value) {
        setStatusIfInteractive(value, false);
    }

    void setStatusIfInteractive(String value, boolean error) {
        if (interactive) {
            status.postValue(value);
            isError.setValue(error);
        }
    }

    public void connect() {
        mUpdateStatusTo = Shared.isGarminConnected;
        setStatusIfInteractive("Initializing...");
        if (mConnectIQ != null) {
            try {
                mConnectIQ.shutdown(context);
            } catch (Exception e) {
            }
        }
        mConnectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS);
        try {
            mConnectIQ.shutdown(context);
        } catch (Exception e) {
        }
        mConnectIQ.initialize(context, interactive, new ConnectIQ.ConnectIQListener() {
            @Override
            public void onSdkReady() {
                setStatusIfInteractive("Garmin SDK ready");
                Log.d("GWW", "SDK ready");
                List<IQDevice> paired = null;
                try {
                    paired = mConnectIQ.getKnownDevices();
                    if (paired != null && paired.size() > 0) {
                        setStatusIfInteractive("Found " + paired.size() + " device(s)");
                        for (final IQDevice device : paired) {
                            mConnectIQ.registerForDeviceEvents(device, (ConnectIQ.IQDeviceEventListener) (iqDevice, iqDeviceStatus) -> {
                                Log.d("GWW", "Device " + iqDevice.getFriendlyName() + " is now in status " + iqDeviceStatus.name());

                                if (iqDeviceStatus == IQDevice.IQDeviceStatus.CONNECTED) {
                                    try {
                                        mConnectIQ.getApplicationInfo(garminAppId, iqDevice, new ConnectIQ.IQApplicationInfoListener() {
                                            @Override
                                            public void onApplicationInfoReceived(IQApp iqApp) {
                                                Log.d("GWW", "Have app info for " + iqApp.getDisplayName());
                                                try {
                                                    mConnectIQ.registerForAppEvents(iqDevice, iqApp, new ConnectIQ.IQApplicationEventListener() {
                                                        @Override
                                                        public void onMessageReceived(IQDevice iqDevice, IQApp iqApp, List<Object> list, ConnectIQ.IQMessageStatus iqMessageStatus) {
                                                            Log.d("GWW", "Have message: " + list.get(0).toString());
                                                            sendLocation(iqDevice, iqApp, mConnectIQ);
                                                        }
                                                    });
                                                    mApp = iqApp;
                                                    mDevice = iqDevice;
                                                    Log.d("GWW", "SDK is fully OK");
                                                    Shared.isGarminConnected.postValue(true);
                                                    if (mConnectedCallback != null) {
                                                        mConnectedCallback.onGotData(mConnectIQ, iqDevice, mApp);
                                                    }
                                                    setStatusIfInteractive("Found gWeatherWatch app - all OK");
                                                } catch (InvalidStateException e) {
                                                    setStatusIfInteractive("Invalid state", true);
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onApplicationNotInstalled(String s) {
                                                Log.d("GWW", "Have NO application, requesting install " + s);
                                                setStatusIfInteractive("No gWeatherWatch - opening ConnectIq");
                                                try {
                                                    if (interactive) {
                                                        mConnectIQ.openStore(garminAppId);
                                                    }
                                                } catch (InvalidStateException e) {
                                                    setStatusIfInteractive("Invalid state", true);
                                                    e.printStackTrace();
                                                } catch (ServiceUnavailableException e) {
                                                    setStatusIfInteractive("Service unavailable", true);
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } catch (InvalidStateException e) {
                                        setStatusIfInteractive("Invalid state", true);
                                        e.printStackTrace();
                                    } catch (ServiceUnavailableException e) {
                                        setStatusIfInteractive("Service unavailable", true);
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        setStatusIfInteractive("No Garmin devices", true);
                    }
                } catch (InvalidStateException e) {
                    setStatusIfInteractive("Invalid state", true);
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                } catch (ServiceUnavailableException e) {
                    setStatusIfInteractive("Service unavailable", true);
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                }
            }


            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
                Log.d("GWW", "Init error: " + iqSdkErrorStatus.toString());

                setStatusIfInteractive("Init error: " + iqSdkErrorStatus.toString(), true);
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

    ICIQRunner mConnectedCallback = null;

    public void connectAndSend() {
        mConnectedCallback = new ICIQRunner() {
            @Override
            public void onGotData(ConnectIQ connectIQ, IQDevice device, IQApp app) {
                sendLocation(device, app, connectIQ);
            }
        };
        connect();
    }
}
