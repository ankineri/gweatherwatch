package com.ankineri.gwwcompanion.ui.main;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.widget.Toast;

import com.ankineri.gwwcompanion.R;
import com.ankineri.gwwcompanion.databinding.FragmentSetupBinding;

import java.util.Arrays;

public class SetupFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private SetupViewModel setupViewModel;
    private FragmentSetupBinding binding;

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
        setupViewModel.error.setValue (Arrays.stream(grantResults).anyMatch(x -> x == PackageManager.PERMISSION_DENIED));
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
        return Arrays.stream(permissions).map(permission -> ContextCompat.checkSelfPermission(this.getContext(), permission )
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
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

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
        final TextView textView = binding.sectionLabel;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}