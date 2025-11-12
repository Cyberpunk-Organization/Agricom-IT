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
import com.example.agricom_it.ui.ChatListFragment;
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

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("login_id"))
            {
                Object extra = intent.getSerializableExtra("login_id");
                LoginResponse loginResponse = null;
                userID = (int) extra;

//                intent.putExtra("login_id", userID);

                Log.d(TAG, "Intent: has login_id extra: " + extra);
            }
            else
            {
                Log.d(TAG, "Intent: no login_id extra found");
            }
//            Log.d(TAG, "Bottom Nav Created" );

            Log.d(TAG, "UserID in BottomNavFragment: " + userID );

            Fragment inventoryF = new InventoryFragment();
            Fragment todoF = new ToDoListFragment();
//            Fragment chatF = new ChatFragment();
            Fragment chatListF = new ChatListFragment();

//            Bundle inventoryArgs = new Bundle();
//            inventoryArgs.putInt("userID", userID);

            Bundle infoArgs = new Bundle();
            infoArgs.putInt("userID", userID);

            inventoryF.setArguments(infoArgs);
            todoF.setArguments(infoArgs);
            chatListF.setArguments(infoArgs);


            if (id == R.id.nav_home) {
                fm.beginTransaction().replace(R.id.contentFrame, new HomeFragment()).commit();
            } else if (id == R.id.nav_inventory) {
                fm.beginTransaction().replace(R.id.contentFrame, inventoryF).commit();
            } else if (id == R.id.nav_settings) {
                fm.beginTransaction().replace(R.id.contentFrame, new SettingsFragment()).commit();
            }else if (id == R.id.nav_list) {
                fm.beginTransaction().replace(R.id.contentFrame, todoF).commit();
            }else if (id == R.id.nav_chat) {
            fm.beginTransaction().replace(R.id.contentFrame, chatListF).commit();
        }
            return true;
        });

        return view;
    }
}