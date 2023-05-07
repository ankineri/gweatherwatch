package com.ankineri.gwwcompanion.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ankineri.gwwcompanion.ConnectIqHelper;
import com.ankineri.gwwcompanion.Shared;
import com.ankineri.gwwcompanion.databinding.FragmentStatusBinding;

import java.time.Duration;
import java.util.Date;

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
    public void onDestroy() {
        super.onDestroy();
        stopUpdates();
    }

    void setLabelFromDate(TextView label, Date date) {
        boolean isNever = date == null;
        int color = Color.RED;
        String text = "Never";
        if (!isNever) {
            long elapsedMinutes = Duration.between(date.toInstant(), new Date().toInstant()).toMinutes();
            text = elapsedMinutes + " minutes";
            color = elapsedMinutes < 60? Color.GREEN : Color.YELLOW;
        }
        label.setText(text);
        label.setBackgroundColor(color);

    }
    void refreshValues() {
        setLabelFromDate(binding.lblLastRunValue, Shared.lastServiceRun.getValue());
        setLabelFromDate(binding.lblLastSendValue, Shared.lastSuccessfulSend.getValue());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentStatusBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StatusViewModel.class);
        Shared.lastServiceRun.observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                refreshValues();
            }
        });
        Shared.lastSuccessfulSend.observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                refreshValues();
            }
        });
        startUpdates();
        return binding.getRoot();
    }
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the value of the view here
            updateValue();
            // Schedule the runnable to run again in 30 seconds
            mHandler.postDelayed(this, 30000);
        }
    };

    // Call this method to start the periodic updates
    private void startUpdates() {
        // Schedule the runnable to run for the first time after 30 seconds
        mHandler.postDelayed(mRunnable, 30000);
    }

    // Call this method to stop the periodic updates
    private void stopUpdates() {
        // Remove the runnable from the handler's queue
        mHandler.removeCallbacks(mRunnable);
    }

    // Method to update the value of the view
    private void updateValue() {
        refreshValues();
    }
}