package com.ankineri.gwwcompanion;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;

public class Shared {
    static <T> MutableLiveData<T> createLiveData(T value) {
        MutableLiveData<T> rv = new MutableLiveData<>();
        rv.setValue(value);
        return rv;
    }
    public static MutableLiveData<Boolean> isServiceStarted = createLiveData(Boolean.FALSE);
    public static MutableLiveData<Boolean> isGarminConnected = createLiveData(Boolean.FALSE);

    public static MutableLiveData<Boolean> isSetupComplete = createLiveData(Boolean.FALSE);

    public static MutableLiveData<Date> lastSuccessfulSend = createLiveData(null);
    public static MutableLiveData<Date> lastServiceRun = createLiveData(null);

    public static long serviceNoTouchUntil = 0;

}
