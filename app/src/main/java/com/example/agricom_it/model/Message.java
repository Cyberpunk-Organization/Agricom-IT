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

    //-------------------------------------------------------------------------------------[Message]
    public Message() { }

    //-------------------------------------------------------------------------------------[Message]
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

    //-------------------------------------------------------------------------------------[Message]
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

    //-------------------------------------------------------------------------------------[Message]
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

    //--------------------------------------------------------------------------------[parseIntSafe]
    private int parseIntSafe(Object o, int defaultVal) {
        if (o == null) return defaultVal;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    //--------------------------------------------------------------------------------[getMessageId]
    public int getMessageId() { return messageId; }

    //--------------------------------------------------------------------------------[setMessageId]
    public void setMessageId(int messageId) { this.messageId = messageId; }

    //---------------------------------------------------------------------------------[getSenderId]
    public int getSenderId() { return senderId; }

    //---------------------------------------------------------------------------------[setSenderId]
    public void setSenderId(int senderId) { this.senderId = senderId; }

    //---------------------------------------------------------------------------------------[getId]
    public int getId() { return id; }

    //---------------------------------------------------------------------------------------[setId]
    public void setId(int id ) { this.id = id; }

    //-------------------------------------------------------------------------------------[getText]
    public String getText() { return text; }

    //-------------------------------------------------------------------------------------[setText]
    public void setText(String text ) { this.text = text; }

    //---------------------------------------------------------------------------------[getDateSent]
    public String getDateSent() { return dateSent; }

    //---------------------------------------------------------------------------------[setDateSent]
    public void setDateSent(String dateSent) { this.dateSent = dateSent; }

    //--------------------------------------------------------------------------------[getTimestamp]
    public Date getTimestamp() { return timestamp; }

    //--------------------------------------------------------------------------------[setTimestamp]
    public void setTimestamp(Date timestamp ) { this.timestamp = timestamp; }

    //------------------------------------------------------------------------------------[toString]
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

