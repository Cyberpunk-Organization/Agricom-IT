package com.example.agricom_it.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.adapter.MessageAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.repo.ChatRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import com.example.agricom_it.model.Message;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private String chatId;
    private int otherUserId;
    private int currentUserId = -1;
    private ChatRepository repo;
    private ListenerRegistration messagesListener;
    private RecyclerView rvMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private MessageAdapter adapter;

    //------------------------------------------------------------------------------------[onCreate]
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent i = getIntent();
        currentUserId = getIntent().getIntExtra("userID", -1);
        chatId = getIntent().getStringExtra("chatId");
        otherUserId = getIntent().getIntExtra("otherUserId", -1);

        rvMessages = findViewById(R.id.chat_recycler);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        repo = new ChatRepository(getApplicationContext());

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
