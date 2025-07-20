package com.ankineri.gwwcompanion.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {
    public MutableLiveData<Boolean> isGarminOkay = new MutableLiveData<>();
}