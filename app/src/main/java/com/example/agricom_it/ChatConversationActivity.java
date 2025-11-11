package com.example.agricom_it;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        int receiverId = getIntent().getIntExtra("receiverID", -1);
        String username = getIntent().getStringExtra("username");

        // Mock current user
        int currentUserId = 1; // Replace with logged-in user ID later

        RecyclerView messagesRecycler = findViewById(R.id.messages_recycler);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

//        List<Message> mockMessages = new ArrayList<>();
//        mockMessages.add(new Message(1, currentUserId, receiverId, "Hey " + username + "!"));
//        mockMessages.add(new Message(2, receiverId, currentUserId, "Hey there!"));
//        mockMessages.add(new Message(3, currentUserId, receiverId, "How’s your day?"));
//        mockMessages.add(new Message(4, receiverId, currentUserId, "Pretty good, thanks!"));
//
//        MessageAdapter adapter = new MessageAdapter(mockMessages, currentUserId);
//        messagesRecycler.setAdapter(adapter);
    }
}
