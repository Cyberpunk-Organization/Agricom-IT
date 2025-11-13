// java
package com.example.agricom_it.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;
import com.example.agricom_it.model.Message;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private final List<Message> items;
    private final int currentUserId;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    //------------------------------------------------------------------------------[MessageAdapter]
    public MessageAdapter(List<Message> items, int currentUserId) {
        this.items = items != null ? items : new ArrayList<>();
        this.currentUserId = currentUserId;
    }

    //-----------------------------------------------------------------------------[getItemViewType]
    @Override
    public int getItemViewType(int position) {
        Message m = items.get(position);
        return (m != null && m.getSenderId() == currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    //--------------------------------------------------------------------------[onCreateViewHolder]
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            View v = inf.inflate(R.layout.item_message_sent, parent, false);
            return new SentHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedHolder(v);
        }
    }

    //----------------------------------------------------------------------------[onBindViewHolder]
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = items.get(position);
        String text = m.getText() != null ? m.getText() : "";
        String time = formatTimestamp(m.getTimestamp());
        if (holder instanceof SentHolder) {
            ((SentHolder) holder).txtMessage.setText(text);
            ((SentHolder) holder).txtTime.setText(time);
        } else if (holder instanceof ReceivedHolder) {
            ((ReceivedHolder) holder).txtMessage.setText(text);
            ((ReceivedHolder) holder).txtTime.setText(time);
        }
    }

    //--------------------------------------------------------------------------------[getItemCount]
    @Override
    public int getItemCount() {
        return items.size();
    }

    //-----------------------------------------------------------------------------------------[add]
    public void add(Message m) {
        items.add(m);
        notifyItemInserted(items.size() - 1);
    }

    //--------------------------------------------------------------------------------------[addAll]
    public void addAll(List<Message> list) {
        if (list == null || list.isEmpty()) return;
        int start = items.size();
        items.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    //---------------------------------------------------------------------------------------[clear]
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    //-----------------------------------------------------------------------------[formatTimestamp]
    private String formatTimestamp(Object tsObj) {
        if (tsObj == null) return "";
        Date d = null;
        if (tsObj instanceof Timestamp) {
            d = ((Timestamp) tsObj).toDate();
        } else if (tsObj instanceof Date) {
            d = (Date) tsObj;
        } else if (tsObj instanceof Long) {
            d = new Date((Long) tsObj);
        } else {
            try {
                d = new Date(Long.parseLong(tsObj.toString()));
            } catch (Exception ignored) {
            }
        }
        return d != null ? timeFormat.format(d) : "";
    }

    //----------------------------------------------------------------------------------[SentHolder]
    static class SentHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        TextView txtTime;

        SentHolder(@NonNull View v) {
            super(v);
            txtMessage = v.findViewById(R.id.textMessage);
            txtTime = v.findViewById(R.id.textTime);
        }
    }

    //------------------------------------------------------------------------------[ReceivedHolder]
    static class ReceivedHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        TextView txtTime;

        ReceivedHolder(@NonNull View v) {
            super(v);
            txtMessage = v.findViewById(R.id.textMessage);
            txtTime = v.findViewById(R.id.textTime);
        }
    }
}
