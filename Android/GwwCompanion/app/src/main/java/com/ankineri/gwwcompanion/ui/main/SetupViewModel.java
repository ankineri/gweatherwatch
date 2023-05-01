package com.ankineri.gwwcompanion.ui.main;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.net.HttpCookie;

public class SetupViewModel extends ViewModel {
    public MutableLiveData<Boolean> hasBkgndLocationPermission = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasLocationPermission = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
}