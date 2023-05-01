package com.ankineri.gwwcompanion.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {
    public MutableLiveData<Boolean> isGarminOkay = new MutableLiveData<>();
}