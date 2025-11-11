// java
package com.example.agricom_it;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agricom_it.R;
import com.example.agricom_it.model.ChatSummary;
import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.VH> {

    private final String TAG = "ChatListAdapter";

    public interface OnChatClick { void onClick(ChatSummary chat); }

    private final List<ChatSummary> items = new ArrayList<>();
    private final int currentUserId;
    private final OnChatClick click;


    public ChatListAdapter(int currentUserId, OnChatClick click) {

        Log.d(TAG, "ChatListAdapter created with currentUserId: " + currentUserId );

        this.currentUserId = currentUserId;
        this.click = click;
    }

    public void add(ChatSummary chat)
    {
        // avoid duplicates by chatId
        for (ChatSummary c : items)
        {
            if (c.chatId != null && c.chatId.equals(chat.chatId))
                return;
        }
        items.add(chat);
        notifyItemInserted(items.size() - 1);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder called" );

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_summary, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position)
    {
        Log.d(TAG, "onBindViewHolder called for position: " + position );

        ChatSummary s = items.get(position);
        int other = -1;
        if (s.participants != null) {
            for (int p : s.participants) {
                if (p != currentUserId) { other = p; break; }
            }
        }
        String title = (other == -1) ? s.chatId : ("Chat with user " + other);
        holder.txtTitle.setText(title);
        holder.itemView.setOnClickListener(v -> click.onClick(s));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle;
        VH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtChatTitle);
        }
    }
}
