//package com.example.agricom_it.model;
//
//// This class details the information for a user to send a single message
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//
//public class Message {
//    private int MessageID;
//    private int SenderID;
//    private int ReceiverID;
//    private LocalDate DateSent;
//    private LocalTime TimeSent;
//    private String MessageContent;
//
//    public Message(){};
//
//    public Message(int MessageID,int SenderID,int ReceiverID,String MessageContent){
//        this.MessageID = MessageID;
//        this.SenderID = SenderID;
//        this.ReceiverID = ReceiverID;
//        this.MessageContent = MessageContent;
//        this.DateSent = LocalDate.now();
//        this.TimeSent = LocalTime.now();
//
//    }
//
//    public int GetMessageID() {return MessageID;}
//    public int GetReceiverID() {return ReceiverID;}
//    public int GetSenderID() {return SenderID;}
//    public String GetMessageContent() {return MessageContent;}
//    public LocalDate GetDateSent() {return DateSent;}
//    public String GetTimeSent() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        return TimeSent.format(formatter);
//    }
//
//}


// Java
package com.example.agricom_it.model;

public class Message {
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

