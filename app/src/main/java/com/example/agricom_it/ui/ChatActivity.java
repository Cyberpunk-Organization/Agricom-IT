// File: app/src/main/java/com/example/agricom_it/ui/ChatActivity.java
package com.example.agricom_it.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;
import com.example.agricom_it.model.Message;
import com.example.agricom_it.repo.ChatRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.example.agricom_it.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    private ChatRepository repo;
    private MessageAdapter adapter;
    private int currentUserId = 1; // set from auth/session
    private String chatId = null; // set when creating/opening a chat
    private EditText edtMessage;
    private Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView rv = findViewById(R.id.recycler);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(currentUserId);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo = new ChatRepository();

        btnSend.setOnClickListener(v -> {
            String txt = edtMessage.getText().toString().trim();
            if (txt.isEmpty()) return;
            if (chatId == null) return; // require an open chat
            String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            Message m = new Message(0, currentUserId, /*receiverId*/ 2, txt, date, time);
            repo.sendMessage(chatId, m);
            edtMessage.setText("");
        });
    }

    private void startNewChat(int otherUserId) {
        // deterministic chat id between two users to avoid duplicates
        int a = Math.min(currentUserId, otherUserId);
        int b = Math.max(currentUserId, otherUserId);
        String newChatId = "chat_" + a + "_" + b;

        repo.createChat(newChatId, Arrays.asList(currentUserId, otherUserId), success -> {
            if (success) {
                this.chatId = newChatId;
                // attach listener for the newly created/opened chat
                repo.addMessageListener(chatId, message -> runOnUiThread(() -> adapter.add(message)));
            } else {
                // handle failure (omitted for brevity)
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repo.removeListener();
    }
}
