package com.ankineri.gwwcompanion;

import android.Manifest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
//import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectIqHelper {
    static ConnectIqHelper mInstance = null;

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
    private IQApp mApp;
    private IQDevice mDevice;

    public ConnectIqHelper(Context context, boolean interactive) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.interactive = interactive;
    }

    synchronized private void freeConnectIq() {
        if (mConnectIq != null) {
            try {
                mConnectIq.shutdown(registrationContext);
            } catch (InvalidStateException e) {
                e.printStackTrace();
            }
            mConnectIq = null;
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
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

    void sendLocation(final IQDevice device, final IQApp app, final ConnectIQ connectIQ) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Shared.releaseWakeLock(context);
            freeConnectIq();
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
                    freeConnectIq();
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
                            freeConnectIq();
                            Shared.releaseWakeLock(context);
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                connectOnMainThread();
            }
        });
    }

    static Context registrationContext = null;

    synchronized ConnectIQ makeNewConnectIq(Context context) {
        if (mConnectIq != null) {
            try {
                mConnectIq.shutdown(registrationContext);
            } catch (InvalidStateException e) {
                throw new RuntimeException(e);
            }
        }
        registrationContext = context;
        mConnectIq = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS);
        return mConnectIq;
    }

    static ConnectIQ mConnectIq = null;

    public void connectOnMainThread() {
        final ConnectIQ connectIQ = makeNewConnectIq(context);
        mUpdateStatusTo = Shared.isGarminConnected;
        setStatusIfInteractive("Initializing...");
        connectIQ.initialize(context, interactive, new ConnectIQ.ConnectIQListener() {
            @Override
            public void onSdkReady() {
                setStatusIfInteractive("Garmin SDK ready");
                Log.d("GWW", "SDK ready");
                List<IQDevice> paired = null;
                Set<Long> processedDevices = new HashSet<Long>();
                try {
                    paired = connectIQ.getKnownDevices();
                    if (paired != null && paired.size() > 0) {
                        setStatusIfInteractive("Found " + paired.size() + " device(s)");
                        for (final IQDevice device : paired) {
                            connectIQ.registerForDeviceEvents(device, (ConnectIQ.IQDeviceEventListener) (iqDevice, iqDeviceStatus) -> {
                                Log.d("GWW", "Device " + iqDevice.getFriendlyName() + " is now in status " + iqDeviceStatus.name());
                                if (iqDeviceStatus == IQDevice.IQDeviceStatus.CONNECTED) {
                                    try {
                                        connectIQ.getApplicationInfo(garminAppId, iqDevice, new ConnectIQ.IQApplicationInfoListener() {
                                            @Override
                                            public void onApplicationInfoReceived(IQApp iqApp) {
                                                Log.d("GWW", "Have app info for " + iqApp.getApplicationId());
                                                if (processedDevices.contains(device.getDeviceIdentifier())) {
                                                    return;
                                                }
                                                processedDevices.add(device.getDeviceIdentifier());

                                                mApp = iqApp;
                                                mDevice = iqDevice;
                                                Log.d("GWW", "SDK is fully OK");
                                                Shared.isGarminConnected.postValue(true);
                                                if (mConnectedCallback != null) {
                                                    mConnectedCallback.onGotData(connectIQ, iqDevice, mApp);
                                                } else {
                                                    freeConnectIq();
                                                }
                                                setStatusIfInteractive("Found gWeatherWatch app - all OK");
                                            }

                                            @Override
                                            public void onApplicationNotInstalled(String s) {
                                                Log.d("GWW", "Have NO application, requesting install " + s);
                                                setStatusIfInteractive("No gWeatherWatch - opening ConnectIq");
                                                try {
                                                    if (interactive) {
                                                        connectIQ.openStore(garminAppId);
                                                        freeConnectIq();
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
                                        freeConnectIq();
                                    } catch (ServiceUnavailableException e) {
                                        setStatusIfInteractive("Service unavailable", true);
                                        e.printStackTrace();
                                        freeConnectIq();
                                    }

                                }
                            });
                        }
                    } else {
                        setStatusIfInteractive("No Garmin devices", true);
                        freeConnectIq();
                    }
                } catch (InvalidStateException e) {
                    setStatusIfInteractive("Invalid state", true);
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                    freeConnectIq();
                } catch (ServiceUnavailableException e) {
                    setStatusIfInteractive("Service unavailable", true);
                    e.printStackTrace();
                    if (mUpdateStatusTo != null) {
                        mUpdateStatusTo.setValue(false);
                    }
                    freeConnectIq();
                }
            }


            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
                Log.d("GWW", "Init error: " + iqSdkErrorStatus.toString());

                setStatusIfInteractive("Init error: " + iqSdkErrorStatus.toString(), true);
                if (mUpdateStatusTo != null) {
                    mUpdateStatusTo.setValue(false);
                }
                freeConnectIq();
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
