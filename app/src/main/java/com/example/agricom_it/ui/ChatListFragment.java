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

import com.example.agricom_it.activity.ChatActivity;
import com.example.agricom_it.adapter.ChatListAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.repo.ChatRepository;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {
    private final String TAG = "ChatListFragment";
    private ChatRepository repo;
    private ChatListAdapter adapter;
    private int currentUserId = -1;
    private Button btnStartChat;
    private final AuthApiService apiService = ApiClient.getService();
    private ListenerRegistration chatsListener;

    //--------------------------------------------------------------------------------[onCreateView]
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        currentUserId = getArguments() != null ? getArguments().getInt("userID", -1) : -1;
        return inflater.inflate(R.layout.activity_chat_list, container, false);
    }

    //-------------------------------------------------------------------------------[onViewCreated]
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.recyclerChatLists);
        btnStartChat = view.findViewById(R.id.btnStartChat);

        adapter = new ChatListAdapter(currentUserId, chatSummary ->
        {
            Intent i = new Intent(requireContext(), ChatActivity.class);
            i.putExtra("chatId", chatSummary.chatId);
            int other = -1;
            for (int p : chatSummary.participants) {
                if (p != currentUserId) {
                    other = p;
                    break;
                }
            }
            i.putExtra("otherUserId", other);
            i.putExtra("userID", currentUserId);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        repo = new ChatRepository(getContext());

        if (currentUserId == -1) {
            Log.w(TAG, "currentUserId not set. Pass userID in fragment arguments.");
        } else {
            chatsListener = repo.listenForUserChatsFirestore(currentUserId, new ChatRepository.ChatsCallback() {
                @Override
                public void onChatAdded(com.example.agricom_it.model.ChatSummary chat) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> adapter.add(chat));
                    }
                }

                @Override
                public void onChatsUpdated(java.util.List<com.example.agricom_it.model.ChatSummary> chats) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> adapter.setAll(chats));
                    }
                }
            });
        }

        btnStartChat.setOnClickListener(v -> showNewChatDialog());
    }

    //---------------------------------------------------------------------------[showNewChatDialog]
    private void showNewChatDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Start new chat")
                .setMessage("Enter other username or email:")
                .setView(input)
                .setPositiveButton("Start", (d, which) ->
                {
                    String inputIdentifier = input.getText().toString().trim();
                    if (inputIdentifier.isEmpty()) {
                        return;
                    }

                    // If numeric, treat as direct user id
                    boolean parsedNumber = false;
                    try {
                        int other = Integer.parseInt(inputIdentifier);
                        parsedNumber = true;
                        startNewChatWith(other);
                        //To show a message to prevent a user to chat with themselve
                        if (other == currentUserId) {
                            showToastOnUiThread("You cannot start a chat with yourself.");
                        } else {
                            startNewChatWith(other);
                        }
                    } catch (NumberFormatException ignored) {
                        Log.e(TAG, "Identifier is not a numeric user id, treating as username/email");
                    }

                    if (!parsedNumber) {
                        // NOTE: adjust the action string ("getUserId") if your backend expects a different action name
                        Call<ResponseBody> call = apiService.GetUserIdByUsernameOrEmail("GetUserIdByUsernameOrEmail", inputIdentifier);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (!response.isSuccessful() || response.body() == null) {
                                    Log.e(TAG, "GetUserIdByUsernameOrEmail failed: " + (response != null ? response.code() : "null"));
                                    return;
                                }

                                String json = null;
                                try {
                                    json = response.body().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                Gson gson = new GsonBuilder().create();
                                JsonObject root = gson.fromJson(json, JsonObject.class);

                                if (root == null) {
                                    Log.e(TAG, "User JSON root null");
                                    return;
                                }

                                JsonElement dataElem = root.get("data");

                                try {
                                    int otherUserId = dataElem.getAsInt();
                                    Log.d(TAG, "Resolved user id: " + otherUserId);
                                    startNewChatWith(otherUserId);
                                    if (otherUserId == currentUserId) {
                                        showToastOnUiThread("You cannot start a chat with yourself.");
                                    } else {
                                        startNewChatWith(otherUserId);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing user ID", e);
                                    showToastOnUiThread("User not found");
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                if (!isAdded()) return;
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

    //----------------------------------------------------------------------------[startNewChatWith]
    private void startNewChatWith(int otherUserId) {
        // Prevent self-chat
        if (otherUserId == currentUserId) {
            showToastOnUiThread("You cannot start a chat with yourself.");
            return;
        }

        int a = Math.min(currentUserId, otherUserId);
        int b = Math.max(currentUserId, otherUserId);

        String newChatId = "chat_" + a + "_" + b;

        repo.createChat(newChatId, java.util.Arrays.asList(currentUserId, otherUserId), success ->
        {
            if (!isAdded()) return;// safe to call; for Realtime fallback for selfchat
            if (success) {
                Intent i = new Intent(requireContext(), ChatActivity.class);

                i.putExtra("chatId", newChatId);
                i.putExtra("otherUserId", otherUserId);
                i.putExtra("userID", currentUserId);

                requireActivity().startActivity(i);
            } else {
                Log.e(TAG, "Chat Creation Failed");
            }
        });
    }

    //-------------------------------------------------------------------------[showToastOnUiThread]
    private void showToastOnUiThread(String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    //-------------------------------------------------------------------------------[onDestroyView]
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (repo != null) {
            repo.removeChatsListener(); // safe to call; for Realtime fallback
        }
        if (chatsListener != null) {
            chatsListener.remove();
            chatsListener = null;
        }
    }
}
