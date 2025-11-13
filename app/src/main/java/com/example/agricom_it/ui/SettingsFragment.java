package com.example.agricom_it.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.agricom_it.MainActivity;
import com.example.agricom_it.R;

import java.io.File;

public class SettingsFragment extends Fragment {
    private SwitchCompat switchDarkMode;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    //--------------------------------------------------------------------------------[onCreateView]
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize SharedPreferences
        preferences = requireContext().getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = preferences.edit();

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        boolean isDarkMode = preferences.getBoolean("DarkMode", false);
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(getContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.logout_layout).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
            clearCacheOnly();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            requireActivity().finishAffinity();
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

    //------------------------------------------------------------------------------[clearCacheOnly]
    private void clearCacheOnly() {
        try {
            File cacheDir = requireContext().getCacheDir();
            deleteDir(cacheDir);

            File externalCache = requireContext().getExternalCacheDir();
            deleteDir(externalCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------[deleteDir]
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
            }
        }
        return dir != null && dir.delete();
    }
}
