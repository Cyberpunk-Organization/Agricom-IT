////package com.example.agricom_it;
////
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.TextView;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.agricom_it.model.Message;
////
////import java.util.List;
////
////public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
////
////    private List<Message> messages;
////    private int currentUserId;
////
////    public MessageAdapter(List<Message> messages, int currentUserId) {
////        this.messages = messages;
////        this.currentUserId = currentUserId;
////    }
////
////    @NonNull
////    @Override
////    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view;
////        if (viewType == 1) { // sent by current user
////            view = LayoutInflater.from(parent.getContext())
////                    .inflate(R.layout.item_message_sent, parent, false);
////        } else {
////            view = LayoutInflater.from(parent.getContext())
////                    .inflate(R.layout.item_message_received, parent, false);
////        }
////        return new MessageViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
////        Message message = messages.get(position);
////        holder.messageText.setText(message.GetMessageContent());
////        holder.timestamp.setText(message.GetTimeSent());
////    }
////
////    @Override
////    public int getItemViewType(int position) {
////        Message message = messages.get(position);
////        return message.GetSenderID() == currentUserId ? 1 : 0;
////    }
////
////    @Override
////    public int getItemCount() {
////        return messages.size();
////    }
////
////    static class MessageViewHolder extends RecyclerView.ViewHolder {
////        TextView messageText, timestamp;
////        public MessageViewHolder(@NonNull View itemView) {
////            super(itemView);
////            messageText = itemView.findViewById(R.id.message_text);
////            timestamp = itemView.findViewById(R.id.message_time);
////        }
////    }
////}
//
//
//// java
//package com.example.agricom_it;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.agricom_it.model.Message;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
//
//    private final List<Message> messages = new ArrayList<>();
//    private int currentUserId;
//
//    public MessageAdapter(int currentUserId) {
//        this.currentUserId = currentUserId;
//    }
//
//    public void setCurrentUserId(int id) {
//        this.currentUserId = id;
//        notifyDataSetChanged();
//    }
//
//    public void setMessages(List<Message> newMessages) {
//        messages.clear();
//        if (newMessages != null) messages.addAll(newMessages);
//        notifyDataSetChanged();
//    }
//
//    public void addMessage(Message message) {
//        if (message == null) return;
//        messages.add(message);
//        notifyItemInserted(messages.size() - 1);
//    }
//
//    @NonNull
//    @Override
//    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == 1) { // sent by current user
//            view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_message_sent, parent, false);
//        } else { // received
//            view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_message_received, parent, false);
//        }
//        return new MessageViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
//        Message message = messages.get(position);
//        String content = message.GetMessageContent() != null ? message.GetMessageContent() : "";
//        String time = message.GetTimeSent() != null ? message.GetTimeSent() : "";
//        holder.messageText.setText(content);
//        holder.timestamp.setText(time);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message message = messages.get(position);
//        return message != null && message.GetSenderID() == currentUserId ? 1 : 0;
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
//
//    static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageText, timestamp;
//        public MessageViewHolder(@NonNull View itemView) {
//            super(itemView);
//            messageText = itemView.findViewById(R.id.message_text);
//            timestamp = itemView.findViewById(R.id.message_time);
//        }
//    }
//}

// Java
package com.example.agricom_it;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agricom_it.R;
import com.example.agricom_it.model.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<Message> items = new ArrayList<>();
    private final int currentUserId;

    public MessageAdapter(int currentUserId) { this.currentUserId = currentUserId; }

    public void add(Message msg) {
        items.add(msg);
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message m = items.get(position);
        return m.getSenderId() == currentUserId ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new RecvHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = items.get(position);
        if (holder instanceof SentHolder) {
            ((SentHolder) holder).txt.setText(m.getMessageContent());
        } else {
            ((RecvHolder) holder).txt.setText(m.getMessageContent());
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class SentHolder extends RecyclerView.ViewHolder {
        TextView txt;
        SentHolder(View v) { super(v); txt = v.findViewById(R.id.txtMessage); }
    }
    static class RecvHolder extends RecyclerView.ViewHolder {
        TextView txt;
        RecvHolder(View v) { super(v); txt = v.findViewById(R.id.txtMessage); }
    }
}

