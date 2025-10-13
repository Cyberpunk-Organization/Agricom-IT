package com.example.loginapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loginapp.R;
import com.example.loginapp.databinding.FragmentBottomNavBinding;

public class BottomNavFragment extends Fragment {
    private FragmentBottomNavBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBottomNavBinding.inflate(inflater, container, false);

        // Handle nav item clicks
//        binding.bottomNav.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//
//            if (id == R.id.nav_home) {
//                requireActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.contentFrame, new HomeFragment())
//                        .commit();
//                return true;
//
//            } else if (id == R.id.nav_settings) {
//                requireActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.contentFrame, new SettingsFragment())
//                        .commit();
//                return true;
//
//            } else if (id == R.id.nav_profile) {
//                requireActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.contentFrame, new ProfileFragment())
//                        .commit();
//                return true;
//            }
//
//            return false;
//        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
