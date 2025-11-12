// java
package com.example.agricom_it.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.ChatListAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.repo.ChatRepository;
import com.example.agricom_it.ui.ChatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment
{

    private final String TAG = "ChatListFragment";
    private ChatRepository repo;
    private ChatListAdapter adapter;
    private int currentUserId = 1; // set from auth/session
    private Button btnStartChat;
    private final AuthApiService apiService = ApiClient.getService();

    @Nullable
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState )
    {
        // reuse the existing layout; consider creating a fragment-specific layout if preferred
        return inflater.inflate(R.layout.activity_chat_list, container, false);
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState )
    {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Fragment created with currentUserId: "+currentUserId);

        RecyclerView rv = view.findViewById(R.id.recyclerChatLists);
        btnStartChat = view.findViewById(R.id.btnStartChat);

        adapter = new ChatListAdapter(currentUserId, chatSummary->
        {
            Intent i = new Intent(requireContext(), ChatActivity.class);
            i.putExtra("chatId", chatSummary.chatId);
            int other = -1;
            for( int p : chatSummary.participants )
            {
                if( p!=currentUserId )
                {
                    other = p;
                    break;
                }
            }
            i.putExtra("otherUserId", other);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        repo = new ChatRepository();

        repo.listenForUserChats(currentUserId, chat->
        {
            if( getActivity()!=null )
            {
                getActivity().runOnUiThread(()->adapter.add(chat));
            }
        });

        btnStartChat.setOnClickListener(v->showNewChatDialog());
    }

    private void showNewChatDialog()
    {
        Log.d(TAG, "Showing new chat dialog");

        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Start new chat")
                .setMessage("Enter other username or email:")
                .setView(input)
                .setPositiveButton("Start", ( d, which )->
                {
                    String inputIdentifier = input.getText().toString().trim();
                    if( inputIdentifier.isEmpty() )
                    {
                        Log.d(TAG, "No identifier entered");
                        return;
                    }

                    // If numeric, treat as direct user id
                    boolean parsedNumber = false;
                    try
                    {
                        int other = Integer.parseInt(inputIdentifier);
                        parsedNumber = true;
                        Log.d(TAG, "Starting new chat with numeric user id: "+other);
                        startNewChatWith(other);
                    }
                    catch( NumberFormatException ignored )
                    {
                        // not numeric -> resolve by username/email
                    }

                    if( !parsedNumber )
                    {
                        Log.d(TAG, "Resolving identifier to UserID: "+inputIdentifier);

                        // NOTE: adjust the action string ("getUserId") if your backend expects a different action name
                        Call<ResponseBody> call = apiService.GetUserIdByUsernameOrEmail("GetUserIdByUsernameOrEmail", inputIdentifier);
                        call.enqueue(new Callback<ResponseBody>()
                        {
                            @Override
                            public void onResponse( Call<ResponseBody> call, Response<ResponseBody> response )
                            {
                                if( !response.isSuccessful() || response.body()==null )
                                {
                                    Log.e(TAG, "GetUserIdByUsernameOrEmail failed: "+(response!=null ? response.code() : "null"));
                                    return;
                                }

                                String json = null;
                                try
                                {
                                    json = response.body().string();
                                }
                                catch( IOException e )
                                {
                                    throw new RuntimeException(e);
                                }

                                Gson gson = new GsonBuilder().create();
                                Log.d(TAG, "User JSON: "+json);

                                JsonObject root = gson.fromJson(json, JsonObject.class);
                                if( root==null )
                                {
                                    Log.e(TAG, "User JSON root null");
                                    return;
                                }

                                JsonElement dataElem = root.get("data");

                                Log.d(TAG, "Data element: "+dataElem.toString());

                                try
                                {
                                    int otherUserId = dataElem.getAsInt();
                                    Log.d(TAG, "Resolved user id: "+otherUserId);
                                    startNewChatWith(otherUserId);
                                }
                                catch( Exception e )
                                {
                                    Log.e(TAG, "Error parsing user ID", e);
                                    showToastOnUiThread("User not found");
                                }

                            }

                            @Override
                            public void onFailure( Call<ResponseBody> call, Throwable t )
                            {
                                if( !isAdded() ) return;
                                Log.e(TAG, "API call failed", t);
                                showToastOnUiThread("Network error resolving user");
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void startNewChatWith( int otherUserId )
    {

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        if( auth.getCurrentUser()==null )
//        {
//            Log.e(TAG, "User not authenticated");
//            showToastOnUiThread("You must be logged in to start a chat");
//            return;
//        }


        Log.d(TAG, "Starting new chat between "+currentUserId+" and "+otherUserId);

        int a = Math.min(currentUserId, otherUserId);
        int b = Math.max(currentUserId, otherUserId);

        String newChatId = "chat_"+a+"_"+b;

        repo.createChat(newChatId, java.util.Arrays.asList(currentUserId, otherUserId), success->
        {
            if( success )
            {
                Log.d(TAG, "Create chat "+ newChatId +" success: "+success);
                //TODO: GOT HERE. REMOVE THIS COMMENT WHEN DONE.

                Intent i = new Intent(requireContext(), ChatActivity.class);

//                Intent i = new Intent( ChatActivity, ChatActivity.class)

                i.putExtra("chatId", newChatId);
                i.putExtra("otherUserId", otherUserId);

                Log.d(TAG, "Extra: " + i.getExtras() );

                requireActivity().startActivity(i);

            }
            else
            {
                Log.d(TAG, "Chat Creation Failed");
                // failure handling omitted for brevity
            }
        });
    }

    private void showToastOnUiThread( String message )
    {
        if( getActivity()==null ) return;
        getActivity().runOnUiThread(()->Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if( repo!=null )
        {
            repo.removeChatsListener();
        }
    }
}
