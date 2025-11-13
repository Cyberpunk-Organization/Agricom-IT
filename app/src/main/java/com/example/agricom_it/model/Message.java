package com.example.agricom_it.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Message
{
    private int messageId;
    private int senderId;
    private int id;
    private String text;
    private String dateSent; // yyyy-MM-dd
    private Date timestamp; // HH:mm:ss

    public Message() { } // required for Firebase deserialization

    // Defensive constructor from DocumentSnapshot
    public Message( DocumentSnapshot snap)
    {
        if (snap == null) return;

        this.id = Integer.parseInt(snap.getId());

        Object s = snap.get("senderId");
        this.senderId = parseIntSafe(s, 0);

        String t = snap.contains("text") ? snap.getString("text") : null;
        this.text = (t != null) ? t : "";

        // Firestore stores timestamps as com.google.firebase.Timestamp
        Object tsObj = snap.get("timestamp");
        if (tsObj instanceof Timestamp ) {
            this.timestamp = ((Timestamp) tsObj).toDate();
        } else if (tsObj instanceof Date ) {
            this.timestamp = (Date) tsObj;
        } else {
            this.timestamp = null;
        }
    }

    public Message(Object senderIdObj, Object textObj, Object timestampObj) {
        this.senderId = parseIntSafe(senderIdObj, 0);
        this.text = (textObj != null) ? textObj.toString() : "";
        if (timestampObj instanceof Timestamp) {
            this.timestamp = ((Timestamp) timestampObj).toDate();
        } else if (timestampObj instanceof Date) {
            this.timestamp = (Date) timestampObj;
        } else {
            this.timestamp = null;
        }
    }

    private int parseIntSafe(Object o, int defaultVal) {
        if (o == null) return defaultVal;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public Message(int messageId, int senderId, int receiverId, String messageContent, String dateSent, Date timeSent) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.id = receiverId;
        this.text = messageContent;
        this.dateSent = dateSent;
        this.timestamp = timeSent;
    }

    public Message( int i, String text, Date ts )
    {
        this.messageId = i;
        this.text = text;
        this.timestamp = ts;
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getId() { return id; }
    public void setId(int id ) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text ) { this.text = text; }

    public String getDateSent() { return dateSent; }
    public void setDateSent(String dateSent) { this.dateSent = dateSent; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp ) { this.timestamp = timestamp; }

    @NonNull
    @Override
    public String toString() {
        return "Message{id=" + id +
                ", senderId=" + senderId +
                ", text=\"" + (text != null ? text : "") + "\"" +
                ", timestamp=" + (timestamp != null ? timestamp.toString() : "null") +
                '}';
    }
}

