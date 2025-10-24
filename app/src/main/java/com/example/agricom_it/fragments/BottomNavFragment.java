package com.example.agricom_it.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.agricom_it.R;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.model.User;
import com.example.agricom_it.ui.ChatFragment;
import com.example.agricom_it.ui.HomeFragment;
import com.example.agricom_it.ui.InventoryFragment;
import com.example.agricom_it.ui.SettingsFragment;
import com.example.agricom_it.ui.ToDoListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavFragment extends Fragment {

    private int userID = -1;
    private static final String TAG = "bNavFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_nav, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment parentFragment = getParentFragment();
            FragmentManager fm = getParentFragmentManager();
            int id = item.getItemId();

            Bundle args = getArguments();

//            Log.d(TAG, "Bottom Nav Created" );

            if ( args != null && args.containsKey("userID") )
            {
//                Log.d(TAG, "Received userID: " + args.getInt("userID"));
                userID = args.getInt("userID", -1);
            }

            Log.d(TAG, "UserID in BottomNavFragment: " + userID );

            Fragment inventoryF = new InventoryFragment();
            Bundle inventoryArgs = new Bundle();
            inventoryArgs.putInt("userID", userID);

            if (id == R.id.nav_home) {
                fm.beginTransaction().replace(R.id.contentFrame, new HomeFragment()).commit();
            } else if (id == R.id.nav_inventory) {
                fm.beginTransaction().replace(R.id.contentFrame, inventoryF).commit();
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