package com.example.agricom_it;

import android.app.ProgressDialog;
import android.content.Intent;
// import android.content.SharedPreferences; // No longer needed here
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import android.content.res.Configuration;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.api.ApiClient;
// No longer need LoginResponse, User, or Gson here
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.RegisterResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity
{

    private EditText nameInput, surnameInput, usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView tvBackToLogin;
    private Spinner roleSpinner;

    private final String TAG = "RegisterActivity_mine";

    private ProgressDialog progressDialog;

    // These are not needed in RegisterActivity anymore
    // private static final String PREFS_NAME = "app_prefs";
    // private static final String PREFS_USER_KEY = "user";
    // private static final String PREFS_TOKEN_KEY = "token";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.name_input);
        surnameInput = findViewById(R.id.surname_input);
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerBtn = findViewById(R.id.register_btn);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        roleSpinner = findViewById(R.id.role_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_roles,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // dropdown rows
        roleSpinner.setAdapter(adapter);

        Drawable popupBg = ContextCompat.getDrawable(this, R.drawable.spinner_popup_frame);
        if( popupBg!=null )
        {
            roleSpinner.setPopupBackgroundDrawable(popupBg);
        }

        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if( uiMode==Configuration.UI_MODE_NIGHT_YES )
        {
            roleSpinner.setPopupBackgroundResource(R.color.black);
        }
        else
        {
            roleSpinner.setPopupBackgroundResource(R.color.white);
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");

        registerBtn.setOnClickListener(v->
        {
            String name = nameInput.getText().toString().trim();
            String surname = surnameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem()!=null ? roleSpinner.getSelectedItem().toString() : "";


            if( username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty() )
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if( !password.equals(confirmPassword) )
            {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

//            RegisterRequest request = new RegisterRequest(name, surname, username, email, password, role);
            RegisterRequest request = new RegisterRequest(name, surname, username, email, password);

            registerWithRetrofit(request);
        });

        tvBackToLogin.setOnClickListener(v->
        {
            // This should go to the login screen, which is MainActivity
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private void registerWithRetrofit( RegisterRequest request )
    {
        progressDialog.show();

        AuthApiService apiService = ApiClient.getService();
        Call<RegisterResponse> call = apiService.register(request);

        // Corrected Callback to use RegisterResponse
        call.enqueue(new Callback<RegisterResponse>()
        {
            @Override
            public void onResponse( @NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response )
            {
                progressDialog.dismiss();
                if( response.isSuccessful() && response.body()!=null )
                {
                    RegisterResponse registerResponse = response.body();
                    Log.d("REGISTER", "Status: "+registerResponse.getStatus());
                    Log.d("REGISTER", "Message: "+registerResponse.getMessage());
                    // Display the message from the server (e.g., "Registration successful")
                    String message = registerResponse.getMessage();
                    if( message==null || message.trim().isEmpty() )
                    {
                        message = "Registration successful!";
                    }

                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    // Check the status from the response
                    if( "true".equalsIgnoreCase(registerResponse.getStatus()) )
                    {
                        // On success, redirect the user to the login screen to sign in
                        Toast.makeText(RegisterActivity.this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show();


                        //TODO CALL Firebase AUTH
                        createFirebaseUser(emailInput.getText().toString().trim(), passwordInput.getText().toString().trim(), usernameInput.getText().toString().trim(), roleSpinner.getSelectedItem().toString());

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish(); // Finish RegisterActivity so the user can't go back to it
                    }
                    else
                    {
                        // Handle cases where registration was not successful but the server responded
                        // For example, if the username or email is already taken
                        Toast.makeText(RegisterActivity.this, "Registration failed: "+registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    // Handle unsuccessful responses (e.g., 404, 500)
                    Toast.makeText(RegisterActivity.this, "Registration failed with code: "+response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure( @NonNull Call<RegisterResponse> call, @NonNull Throwable t )
            {
                progressDialog.dismiss();
                // Handle network errors or other failures
                Toast.makeText(RegisterActivity.this, "Network error: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createFirebaseUser( String email, String password, String displayName, String role )
    {
        progressDialog.setMessage("Creating Firebase user...");
        progressDialog.show();

        Log.d(TAG, "Creating Firebase user with email: "+email+", displayName: "+displayName+", role: "+role);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task->
                {
                    if( !task.isSuccessful() )
                    {
                        Log.d(TAG, "Firebase createUserWithEmailAndPassword failed", task.getException());
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,
                                "Firebase signup failed: "+(task.getException()!=null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                        Log.e("FirebaseAuth", "createUser failed", task.getException());
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if( user==null )
                    {
                        Log.d(TAG, "Firebase user is null after creation");
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Firebase user is null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = user.getUid();
                    // update display name in Auth profile
                    com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                            new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();
                    user.updateProfile(profileUpdates);

                    // prepare Firestore user doc
                    java.util.Map<String, Object> userData = new java.util.HashMap<>();
                    userData.put("uid", uid);
                    userData.put("displayName", displayName);
                    userData.put("email", email);
                    userData.put("role", role);
                    userData.put("username", usernameInput.getText().toString().trim());
                    userData.put("createdAt", FieldValue.serverTimestamp());

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener(aVoid->
                            {
                                progressDialog.dismiss();
                                Log.d(TAG, "User saved in Firestore: "+uid);
                                Toast.makeText(RegisterActivity.this, "Registration complete. Please log in.", Toast.LENGTH_SHORT).show();

                                // Navigate to login (MainActivity)
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e->
                            {
                                progressDialog.dismiss();
                                Log.e(TAG, "Failed saving user doc", e);
                                Toast.makeText(RegisterActivity.this, "Failed saving user data: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }).addOnFailureListener(e ->
                {
                    // extra failure listener for redundancy
                    Log.e(TAG, "Register with Firebase Failed: "+ e);
                });
    }


}
