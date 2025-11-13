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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        View view = inflater.inflate(R.layout.activity_chat_list, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("userID"))
        {
            userID = args.getInt("userID", -1);
        }


        Log.d(TAG, "UserID in ChatFragment: " + userID );

        RecyclerView recyclerView = view.findViewById(R.id.recyclerChatLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<User> mockUsers = new ArrayList<>();
//        mockUsers.add(new User(1, "alice@example.com", "Alice", "Smith", "AliceS", "cool"));
//        mockUsers.add(new User(2, "bob@example.com", "Bob",  "Jones", "BobJ", "epic"));
//        mockUsers.add(new User(3, "carol@example.com", "Carol",  "White", "CarolW", "awesome"));
//        mockUsers.add(new User(4, "dave@example.com", "Dave",  "Brown", "DaveB", "test"));

//        recyclerView.setAdapter(new ChatListAdapter(getContext(), mockUsers));
        recyclerView.setAdapter(new ChatListAdapter( userID, chatSummary -> {

            // Handle chat item click
            Log.d(TAG, "Chat item clicked: " + "Hello There...");

        }));

        return view;
    }
}
