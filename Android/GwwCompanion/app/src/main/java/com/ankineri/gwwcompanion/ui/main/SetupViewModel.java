package com.ankineri.gwwcompanion.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SetupViewModel extends ViewModel {
    public MutableLiveData<Boolean> hasBkgndLocationPermission = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasLocationPermission = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
}