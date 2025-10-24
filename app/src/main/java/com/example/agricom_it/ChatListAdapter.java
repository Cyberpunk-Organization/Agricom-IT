package com.example.agricom_it;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.model.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<User> userList;
    private Context context;

    public ChatListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_user, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.lastMessage.setText("Tap to view messages");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatConversationActivity.class);
            intent.putExtra("receiverID", user.getUserID());
            intent.putExtra("username", user.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chat_username);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
        }
    }
}
