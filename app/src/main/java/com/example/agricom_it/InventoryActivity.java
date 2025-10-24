package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.InventoryItem;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.ui.InventoryFragment;
import android.util.Log;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.agricom_it.model.LoginResponse;


import com.example.agricom_it.api.ApiClient;

public class InventoryActivity extends AppCompatActivity {

    private static final String TAG = "InventoryActivity";
    private final AuthApiService apiService = ApiClient.getService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: InventoryActivity started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory); // we'll create this next

        int userID = -1;
        Intent intent = getIntent();



        if ( intent == null )
        {
            Log.e(TAG, "Intent is null");
        }
        else
        {
            Log.d(TAG, "Intent received successfully");
        }

        if (intent != null && intent.hasExtra("login_id"))
        {
            Object extra = intent.getSerializableExtra( "login_id");
            int loginID = -1;
            loginID = intent.getIntExtra("login_id", -1);

            Log.d(TAG, "FINALLY: " + loginID);

            LoginResponse loginResponse = null;

            if( extra instanceof LoginResponse )
                loginResponse = (LoginResponse) extra;

            else
            {
                try
                {
                    loginResponse = intent.getParcelableExtra("login_id");
                }
                catch ( Exception e )
                {
                    Log.e(TAG, "Failed to retrieve LoginResponse from intent", e);
                }
            }

            if (loginResponse != null)
            {
                userID = loginResponse.getID();
//                userID = loginResponse.getID();
                Log.d(TAG, "Retrieved userID: " + userID);
            }
            else
            {
                Log.e(TAG, "LoginResponse extra is null or of unexpected type");
            }
        }
        else
        {
            Log.e(TAG, "Intent or extra 'activity_main' is null");
        }


        InventoryFragment iFragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putInt("userID", userID);
        iFragment.setArguments(args);

        if (savedInstanceState == null)
        {
            Log.d(TAG, "onCreate: Inventory Fragment called");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.inventoryContainer, iFragment )
                    .commit();
        }




    }
}
