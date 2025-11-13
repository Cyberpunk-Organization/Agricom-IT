package com.example.agricom_it.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.agricom_it.api.AuthApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSummary {
    public String chatId;
    public List<Integer> participants;
    public String otherUsername;
    public String lastMessageText;
    public Long lastMessageTimestamp; // millis since epoch, nullable
    private final String TAG = "ChatSummary";

    //---------------------------------------------------------------------------------[ChatSummary]
    public ChatSummary() {
    }

    //---------------------------------------------------------------------------------[ChatSummary]
    public ChatSummary(String chatId, List<Integer> participants) {
        this.chatId = chatId;
        this.participants = participants;
        this.otherUsername = null;
    }

    //---------------------------------------------------------------------------------[ChatSummary]
    public ChatSummary(String chatId, List<Integer> participants, int currentUserId) {
        this.chatId = chatId;
        this.participants = participants;
        this.otherUsername = null;

        Integer otherId = null;
        if (participants != null) {
            for (Integer id : participants) {
                if (id != null && id != currentUserId) {
                    otherId = id;

                    break;
                }
            }
            if (otherId == null && !participants.isEmpty()) {
                otherId = participants.get(0); // fallback
            }
        }
    }

    //---------------------------------------------------------------------------------[ChatSummary]
    public ChatSummary(String chatId, List<Integer> participants, String otherUsername,
                       String lastMessageText, Long lastMessageTimestamp) {
        this.chatId = chatId;
        this.participants = participants;
        this.otherUsername = otherUsername;
        this.lastMessageText = lastMessageText;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    //--------------------------------------------------------------------------[fetchOtherUsername]
    public void fetchOtherUsername(AuthApiService apiService, Integer currentUserId, UsernameCallback cb) {
        if (apiService == null || participants == null) {
            if (cb != null) cb.onUsernameFetched(null);
            return;
        }

        Integer otherId = null;
        for (Integer id : participants) {
            if (id != null && !id.equals(currentUserId)) {
                otherId = id;
                break;
            }
        }
        if (otherId == null && !participants.isEmpty()) {
            otherId = participants.get(0);
        }
        if (otherId == null) {
            if (cb != null) cb.onUsernameFetched(null);
            return;
        }

        Call<ResponseBody> call = apiService.GetUserByID("GetUserByID", otherId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String username = null;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        JSONObject root = new JSONObject(body);
                        JSONObject data = root.optJSONObject("data");
                        username = (data != null) ? data.optString("Username", null) : null;
                        setOtherUsername(username);
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Failed to parse username response", e);
                    }
                }
                final String uname = username;
                // set field and invoke callback on main thread
                new Handler(Looper.getMainLooper()).post(() ->
                {
                    setOtherUsername(uname);
                    if (cb != null) {
                        cb.onUsernameFetched(uname);
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Username API call failed", t);
                new Handler(Looper.getMainLooper()).post(() ->
                {
                    if (cb != null) cb.onUsernameFetched(null);
                });
            }
        });
    }

    //----------------------------------------------------------------------------[getOtherUsername]
    public String getOtherUsername() {
        return otherUsername;
    }

    //----------------------------------------------------------------------------[setOtherUsername]
    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    //----------------------------------------------------------------------------[UsernameCallback]
    public interface UsernameCallback {
        void onUsernameFetched(String username);
    }
}
