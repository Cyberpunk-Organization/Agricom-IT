package com.example.agricom_it.adapter;

import android.text.format.DateUtils;
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

    private final List<ChatSummary> items = new ArrayList<>();
    private final int currentUserId;
    private final OnChatClick click;
    private final String TAG = "ChatListAdapter";

    //-----------------------------------------------------------------------------[ChatListAdapter]
    public ChatListAdapter(int currentUserId, OnChatClick click) {
        this.currentUserId = currentUserId;
        this.click = click;
    }

    //---------------------------------------------------------------------------------[OnChatClick]
    public interface OnChatClick {
        void onClick(ChatSummary chat);
    }

    //-----------------------------------------------------------------------------------------[add]
    public void add(ChatSummary chat) {
        for (ChatSummary c : items) {
            if (c.chatId != null && c.chatId.equals(chat.chatId))
                return;
        }
        items.add(0, chat);

        notifyItemInserted(items.size() - 1);
    }

    //--------------------------------------------------------------------------[onCreateViewHolder]
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_summary, parent, false);
        return new VH(v);
    }

    //----------------------------------------------------------------------------[onBindViewHolder]
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatSummary s = items.get(position);

        String title = s.getOtherUsername() != null ? s.getOtherUsername() : (s.chatId != null ? s.chatId : "Unknown");
        holder.txtTitle.setText(title);

        holder.txtLastMessage.setText(s.lastMessageText != null ? s.lastMessageText : "");

        if (s.lastMessageTimestamp != null) {
            CharSequence rel = DateUtils.getRelativeTimeSpanString(
                    s.lastMessageTimestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            holder.txtTimestamp.setText(rel);
        } else {
            holder.txtTimestamp.setText("");
        }

        holder.itemView.setOnClickListener(v -> click.onClick(s));
    }

    //--------------------------------------------------------------------------------[getItemCount]
    @Override
    public int getItemCount() {
        return items.size();
    }

    //------------------------------------------------------------------------------------------[VH]
    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtLastMessage;
        TextView txtTimestamp;

        VH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtChatTitle);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
        }
    }

    //--------------------------------------------------------------------------------------[setAll]
    public void setAll(List<ChatSummary> chats) {
        items.clear();
        if (chats != null && !chats.isEmpty()) {
            // sort by lastMessageTimestamp descending (null -> treated as 0)
            chats.sort((a, b) -> {
                long ta = a.lastMessageTimestamp != null ? a.lastMessageTimestamp : 0L;
                long tb = b.lastMessageTimestamp != null ? b.lastMessageTimestamp : 0L;
                return Long.compare(tb, ta);
            });
            items.addAll(chats);
        }
        notifyDataSetChanged();
    }
}
