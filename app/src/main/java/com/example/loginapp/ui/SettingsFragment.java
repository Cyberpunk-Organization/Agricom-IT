package com.example.loginapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loginapp.R;


public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.logout_layout).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
            // Add logout logic here
        });


        view.findViewById(R.id.about_layout).setOnClickListener(v -> {
            Toast.makeText(getContext(), "App version 1.0.0", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Special Thanks to EvolutionAnywhere: https://evolutionanywhere.com/",Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Special Thanks to VirtuoCloud: https://virtuocloud.co.za/",Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}