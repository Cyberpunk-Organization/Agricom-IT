package com.example.agricom_it.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;

public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chat_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //recyclerView.setAdapter(new ChatListAdapter(chatData));

        return view;
    }
}
