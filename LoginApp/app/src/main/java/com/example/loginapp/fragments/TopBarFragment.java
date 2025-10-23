package com.example.loginapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.loginapp.databinding.FragmentTopBarBinding;

public class TopBarFragment extends Fragment {
    private FragmentTopBarBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTopBarBinding.inflate(inflater, container, false);

        // uncomment when we set up the profile page click listener on profile icon
//        binding.profileIcon.setOnClickListener(v -> {
//
//        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
