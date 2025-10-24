package com.example.agricom_it;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        RecyclerView messagesRecycler = findViewById(R.id.messages_recycler);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

        //TODO: Load messages between users here
    }



}