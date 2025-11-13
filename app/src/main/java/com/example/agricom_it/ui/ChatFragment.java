package com.example.agricom_it.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.adapter.ChatListAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private int userID = -1;
    private final String TAG = "ChatFragment";

    //--------------------------------------------------------------------------------[onCreateView]
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_list, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("userID")) {
            userID = args.getInt("userID", -1);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerChatLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new ChatListAdapter(userID, chatSummary -> {
            // Handle chat item click
            Log.d(TAG, "Chat item clicked: " + "Hello There...");

        }));

        return view;
    }
}
