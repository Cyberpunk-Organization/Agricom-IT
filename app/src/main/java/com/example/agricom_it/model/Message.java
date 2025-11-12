package com.example.agricom_it.model;

public class Message
{
    private int messageId;
    private int senderId;
    private int receiverId;
    private String messageContent;
    private String dateSent; // yyyy-MM-dd
    private String timeSent; // HH:mm:ss

    public Message() { } // required for Firebase deserialization

    public Message(int messageId, int senderId, int receiverId, String messageContent, String dateSent, String timeSent) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.dateSent = dateSent;
        this.timeSent = timeSent;
    }

    public Message( int i, String text, Object ts )
    {
        this.messageId = i;
        this.messageContent = text;
        this.timeSent = ts.toString();
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public String getDateSent() { return dateSent; }
    public void setDateSent(String dateSent) { this.dateSent = dateSent; }

    public String getTimeSent() { return timeSent; }
    public void setTimeSent(String timeSent) { this.timeSent = timeSent; }
}

