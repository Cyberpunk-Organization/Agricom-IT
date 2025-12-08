package com.example.agricom_it.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.MainActivity;
import com.example.agricom_it.adapter.GifAdapter;
import com.example.agricom_it.adapter.MessageAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.ChatSummary;
import com.example.agricom_it.model.GifItem;
import com.example.agricom_it.repo.ChatRepository;
import com.example.agricom_it.ui.ChatListFragment;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import com.example.agricom_it.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ImageView btn_back;
    private String chatId;
    private int otherUserId;
    private int currentUserId = -1;
    private ChatRepository repo;
    private ListenerRegistration messagesListener;
    private RecyclerView rvMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private MessageAdapter adapter;
    private TextView otherUsername;

    //------------------------------------------------------------------------------------[onCreate]
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent i = getIntent();

        currentUserId = getIntent().getIntExtra("userID", -1);
        chatId = getIntent().getStringExtra("chatId");
        otherUserId = getIntent().getIntExtra("otherUserId", -1);

        btn_back = findViewById(R.id.btn_back);
        rvMessages = findViewById(R.id.chat_recycler);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        otherUsername =findViewById(R.id.other_chat_username);

        ImageButton btnGIF = findViewById(R.id.btnGIF);

        ChatSummary cs = new ChatSummary(chatId, List.of(currentUserId, otherUserId), currentUserId);
        adapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);
        AuthApiService apiService = ApiClient.getService();

        if (currentUserId == otherUserId) {
            otherUsername.setText("Me");
        } else {
            cs.fetchOtherUsername(apiService, currentUserId, otherUsername::setText);
        }

        repo = new ChatRepository(getApplicationContext());

        btn_back.setOnClickListener( v -> {
            finish();
        });

        // Start listening for messages
        messagesListener = repo.listenForMessages(chatId, (snapshots, e) ->
        {
            if (e != null) {
                Log.e(TAG, "listen error", e);
                return;
            }
            if (snapshots == null) return;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        String text = dc.getDocument().getString("text");
                        Long sender = dc.getDocument().getLong("senderId");
                        Object ts = dc.getDocument().get("timestamp");
                        Message m = new Message(sender != null ? sender.intValue() : -1, text, ts);
                        runOnUiThread(() ->
                        {
                            adapter.add(m);
                            rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                        });
                        break;
                }
            }
        });

        btnSend.setOnClickListener(v ->
        {
            String text = editMessage.getText().toString().trim();

            if (text.isEmpty()) {
                Log.d(TAG, "Empty message, not sending");
                return;
            }

            if (chatId == null) {
                Log.e(TAG, "chatId is null, cannot send message");
                Toast.makeText(ChatActivity.this, "Cannot send: missing chatId", Toast.LENGTH_SHORT).show();
                return;
            }

            if (btnSend != null) btnSend.setEnabled(false);

            repo.sendMessage(chatId, currentUserId, text, success ->
            {
                try {
                    // callback may already run on main thread, but ensure UI updates are on main thread
                    runOnUiThread(() ->
                    {
                        boolean ok = Boolean.TRUE.equals(success);

                        if (isFinishing() || isDestroyed()) {
                            Log.w(TAG, "Activity finishing/destroyed, skipping UI updates");
                            return;
                        }

                        if (btnSend != null) btnSend.setEnabled(true);
                        if (ok) {
                            if (editMessage != null) editMessage.setText("");
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Exception in send callback UI thread", e);
                    // ensure button is re-enabled if possible
                    try {
                        runOnUiThread(() ->
                        {
                            if (btnSend != null) btnSend.setEnabled(true);
                        });
                    } catch (Exception ignored) {
                        Log.e(TAG, "Failed to re-enable send button after exception", ignored);
                    }
                }
            });
        });
        btnGIF.setOnClickListener(v -> openGifDialog());
    }

    private void openGifDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gif_search);

        EditText searchBox = dialog.findViewById(R.id.gifSearchBox);
        RecyclerView gifRecycler = dialog.findViewById(R.id.gifRecycler);

        gifRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        List<GifItem> gifList = new ArrayList<>();
        GifAdapter gifAdapter = new GifAdapter(gifList, url -> {
            sendGif(url);
            dialog.dismiss();
        });
        gifRecycler.setAdapter(gifAdapter);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count){
             if (s.length() > 1) fetchTenorGIFs(s.toString(), gifList, gifAdapter);
            }
        });
        dialog.show();
    }

    //-----------------------------------------------------------------------------------[fetchTenorGIFs]
    private static final String TENOR_API_KEY = "AIzaSyD9-zd1o-mG3n-2y2PDV14ubCBmmOPiL84";

    private void fetchTenorGIFs(String query, List<GifItem> gifList, GifAdapter gifAdapter) {
        try {
            URL url = new URL("https://tenor.googleapis.com/v2/search?q=" + query + "&key=" + TENOR_API_KEY + "&limit=20");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            JSONObject json = new JSONObject(sb.toString());
            JSONArray results = json.getJSONArray("results");

            gifList.clear();
            for (int i = 0; i < results.length(); i++){
                JSONObject media = results.getJSONObject(i).getJSONArray("media_format").getJSONObject(0);
                String gifUrl = media.getString("gif");
                gifList.add(new GifItem(gifUrl));
            }
            runOnUiThread(() -> gifAdapter.notifyDataSetChanged());

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }


    //-----------------------------------------------------------------------------------[sendGifs]
    private void sendGif (String gifUrl) {
        repo.sendMessage(chatId, currentUserId, gifUrl, success -> {
            runOnUiThread(() -> {
                if (Boolean.TRUE.equals(success)) {
                    Toast.makeText(this, "GIF sent", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    //-----------------------------------------------------------------------------------[onDestroy]
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }
}

