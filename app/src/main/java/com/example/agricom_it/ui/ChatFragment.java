package com.example.agricom_it.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.ChatListAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chat_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<User> mockUsers = new ArrayList<>();
//        mockUsers.add(new User(1, "alice@example.com", "Alice", "Smith", "AliceS", "cool"));
//        mockUsers.add(new User(2, "bob@example.com", "Bob",  "Jones", "BobJ", "epic"));
//        mockUsers.add(new User(3, "carol@example.com", "Carol",  "White", "CarolW", "awesome"));
//        mockUsers.add(new User(4, "dave@example.com", "Dave",  "Brown", "DaveB", "test"));

        recyclerView.setAdapter(new ChatListAdapter(getContext(), mockUsers));

        return view;
    }
}
