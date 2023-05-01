package com.ankineri.gwwcompanion.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ankineri.gwwcompanion.ConnectIqHelper;
import com.ankineri.gwwcompanion.LocationProviderService;
import com.ankineri.gwwcompanion.PeriodicService;
import com.ankineri.gwwcompanion.R;
import com.ankineri.gwwcompanion.databinding.FragmentSetupBinding;
import com.ankineri.gwwcompanion.databinding.FragmentStatusBinding;

public class StatusFragment extends Fragment {

    private StatusViewModel mViewModel;
    private ConnectIqHelper mConnectIqHelper;
    private FragmentStatusBinding binding;

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentStatusBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StatusViewModel.class);
        mConnectIqHelper = new ConnectIqHelper(this.getContext());
        mConnectIqHelper.registerStatus(mViewModel.isGarminOkay);
        mConnectIqHelper.connect(true);
        mViewModel.isGarminOkay.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.lblStatus.setText(aBoolean ? "OK" : "NOT OK");
                if (aBoolean) {
                    Intent theIntent = new Intent(getActivity(), PeriodicService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(theIntent);
                    } else {
                        getActivity().startService(theIntent);
                    }
                }
            }
        });
        return binding.getRoot();

    }

}