// java
package com.example.agricom_it.model;

import java.util.List;

public class ChatSummary {
    public String chatId;
    public List<Integer> participants;

    public ChatSummary() {}

    public ChatSummary(String chatId, List<Integer> participants)
    {
        this.chatId = chatId;
        this.participants = participants;
    }
}
