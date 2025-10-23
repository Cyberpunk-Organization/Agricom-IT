package com.example.loginapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loginapp.MainActivity;
import com.example.loginapp.R;


public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        view.findViewById(R.id.logout_layout).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
        });


        view.findViewById(R.id.about_layout).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrame, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}