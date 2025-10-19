package com.example.loginapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.loginapp.R;
import com.example.loginapp.ui.ChatFragment;
import com.example.loginapp.ui.HomeFragment;
import com.example.loginapp.ui.InventoryFragment;
import com.example.loginapp.ui.SettingsFragment;
import com.example.loginapp.ui.ToDoListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_nav, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment parentFragment = getParentFragment();
            FragmentManager fm = getParentFragmentManager();
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fm.beginTransaction().replace(R.id.contentFrame, new HomeFragment()).commit();
            } else if (id == R.id.nav_inventory) {
                fm.beginTransaction().replace(R.id.contentFrame, new InventoryFragment()).commit();
            } else if (id == R.id.nav_settings) {
                fm.beginTransaction().replace(R.id.contentFrame, new SettingsFragment()).commit();
            }else if (id == R.id.nav_list) {
                fm.beginTransaction().replace(R.id.contentFrame, new ToDoListFragment()).commit();
            }else if (id == R.id.nav_chat) {
            fm.beginTransaction().replace(R.id.contentFrame, new ChatFragment()).commit();
        }
            return true;
        });

        return view;
    }
}