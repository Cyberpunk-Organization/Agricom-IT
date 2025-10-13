package com.example.loginapp.model;

// This class details the information for a user to send a single message

import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    private int MessageID;
    private int SenderID;
    private int ReceiverID;
    private LocalDate DateSent;
    private LocalTime TimeSent;
    private String MessageContent;

    public Message(){};

    public Message(int MessageID,int SenderID,int ReceiverIDID,String MessageContent){
        this.MessageID = MessageID;
        this.SenderID = SenderID;
        this.ReceiverID = ReceiverID;
        this.MessageContent = MessageContent;
        this.DateSent = LocalDate.now();
        this.TimeSent = LocalTime.now();
    }

    public int GetMessageID() {return MessageID;}
    public int GetReceiverID() {return ReceiverID;}
    public int GetSenderID() {return SenderID;}
    public String GetMessageContent() {return MessageContent;}
    public LocalDate GetDateSent() {return DateSent;}
    public LocalTime GetTimeSent() {return TimeSent;}

}
