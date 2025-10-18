package com.example.loginapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loginapp.databinding.FragmentInventoryBinding;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the binding for fragment_inventory.xml
        binding = FragmentInventoryBinding.inflate(inflater, container, false);

        // Example: set text on a TextView in fragment_inventory.xml
        // Make sure you actually have a TextView with android:id="@+id/inventoryText"

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks by clearing the reference when the view is destroyed
        binding = null;
    }
}
