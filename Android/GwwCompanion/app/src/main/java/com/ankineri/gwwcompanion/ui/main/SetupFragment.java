package com.ankineri.gwwcompanion.ui.main;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.ankineri.gwwcompanion.ConnectIqHelper;
import com.ankineri.gwwcompanion.MainActivity;
import com.ankineri.gwwcompanion.PeriodicWorker;
import com.ankineri.gwwcompanion.R;
import com.ankineri.gwwcompanion.Shared;
import com.ankineri.gwwcompanion.databinding.FragmentSetupBinding;

import java.util.Arrays;

public class SetupFragment extends Fragment {
    private long canSwitchToStatusUntil = 0;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean isServiceSetupFromButton = false;
    private SetupViewModel setupViewModel;
    private FragmentSetupBinding binding;

    private ConnectIqHelper connectIqHelper;

    boolean isEverythingSetup() {
        return Shared.isGarminConnected.getValue() && setupViewModel.hasBkgndLocationPermission.getValue() && setupViewModel.hasLocationPermission.getValue();
    }

    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    boolean shouldRequestBackgroundLocation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    String[] getRequiredLocationPermissions() {
        return new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this.getContext(), "YES", Toast.LENGTH_LONG);
        setupViewModel.error.setValue(Arrays.stream(grantResults).anyMatch(x -> x == PackageManager.PERMISSION_DENIED));
        setupViewModel.hasLocationPermission.setValue(checkPermissions());
        setupViewModel.hasBkgndLocationPermission.setValue(hasBackgroundLocationPermission());
    }

    void doRequestPermissions() {
        requestPermissions(
                getRequiredLocationPermissions(),
                0
        );
    }

    void doRequestBkgndPermissions() {
        requestPermissions(
                new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"},
                1
        );
    }

    boolean checkPermissions() {
        String[] permissions = getRequiredLocationPermissions();
        return Arrays.stream(permissions).map(permission -> ContextCompat.checkSelfPermission(this.getContext(), permission)
                == PackageManager.PERMISSION_GRANTED).anyMatch(x -> x);
    }

    boolean hasBackgroundLocationPermission() {
        return ContextCompat.checkSelfPermission(this.getContext(), "android.permission.ACCESS_BACKGROUND_LOCATION") == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(SetupViewModel.class);
        connectIqHelper = new ConnectIqHelper(this.getContext(), true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        canSwitchToStatusUntil = System.currentTimeMillis() + 500;
        binding = FragmentSetupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupViewModel.hasLocationPermission.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasPermissions) {
                binding.btnRequestLocation.setBackgroundColor(hasPermissions ? Color.GREEN : Color.YELLOW);
                binding.btnRequestLocation.setEnabled(!hasPermissions);
                binding.btnRequestLocation.setText(hasPermissions ? R.string.btnWhenHavePermissions : R.string.btnWhenNoPermissions);
                binding.sectionLabel.setText(hasPermissions ? R.string.lbl_location_given : R.string.lbl_location);
                if (!hasPermissions) {
                    binding.btnRequestBackgroundLocation.setEnabled(false);
                }
            }
        });
        setupViewModel.hasLocationPermission.setValue(checkPermissions());

        setupViewModel.hasBkgndLocationPermission.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasPermissions) {
                boolean hasLocation = checkPermissions();
                if (!hasLocation) {
                    binding.btnRequestBackgroundLocation.setBackgroundColor(Color.GRAY);
                    binding.btnRequestBackgroundLocation.setEnabled(false);
                    binding.btnRequestBackgroundLocation.setText(R.string.btnWhenNoPermissions);
                    binding.sectionLabel2.setText(R.string.lbl_bkgnd_location);
                } else {
                    binding.btnRequestBackgroundLocation.setBackgroundColor(hasPermissions ? Color.GREEN : Color.YELLOW);
                    binding.btnRequestBackgroundLocation.setEnabled(!hasPermissions);
                    binding.sectionLabel2.setText(hasPermissions ? R.string.lbl_bkgnd_location_given : R.string.lbl_bkgnd_location);
                    binding.btnRequestBackgroundLocation.setText(hasPermissions ? R.string.btnWhenHavePermissions : R.string.btnWhenNoPermissions);
                }
            }
        });
        setupViewModel.hasBkgndLocationPermission.setValue(hasBackgroundLocationPermission());
        Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasService) {
                boolean isAllOkay = Shared.isGarminConnected.getValue();
                if (System.currentTimeMillis() < canSwitchToStatusUntil && isEverythingSetup()) {
                    ((MainActivity) getActivity()).SwitchToStatus();
                }
                binding.btnSetupService.setBackgroundColor(isAllOkay ? Color.GREEN : Color.YELLOW);
                binding.btnSetupService.setEnabled(!isAllOkay);
                binding.sectionLabel3.setText(isAllOkay ? R.string.lbl_foreground_service_done : R.string.lbl_foreground_service);
                binding.btnSetupService.setText(isAllOkay ? R.string.btn_service_given : R.string.btn_service_not_given);
                if (isAllOkay) {
                    binding.lblGarminStatus.setBackgroundColor(Color.GREEN);
                    if (isServiceSetupFromButton) {
                        Shared.serviceNoTouchUntil = 0;
                        doStartService();
                    }
                }

                isServiceSetupFromButton = false;
            }
        };
        Shared.isGarminConnected.observe(getViewLifecycleOwner(), observer);
        observer.onChanged(Shared.isGarminConnected.getValue());
//        Shared.isServiceStarted.observe(getViewLifecycleOwner(), observer);
        binding.btnSetupService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.serviceNoTouchUntil = System.currentTimeMillis() + 500000;
                isServiceSetupFromButton = true;
                doCheckService();
            }
        });
        binding.btnRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRequestPermissions();
            }
        });
        binding.btnRequestBackgroundLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRequestBkgndPermissions();
            }
        });
        setupViewModel.error.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                binding.lblError.setVisibility(isError ? View.VISIBLE : View.INVISIBLE);
            }
        });

        setupViewModel.error.setValue(false);
        connectIqHelper.getStatus().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                binding.lblGarminStatus.setText(s);
            }
        });
        connectIqHelper.getIsError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                binding.lblGarminStatus.setBackgroundColor(isError ? Color.rgb(255, 128, 64) : Color.TRANSPARENT);
            }
        });
        doStartService();
        PeriodicWorker.runOnce(getContext());
        return root;
    }

    private void doCheckService() {
        connectIqHelper.connect();
    }

    private void doStartService() {
        PeriodicWorker.schedule(getContext());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}