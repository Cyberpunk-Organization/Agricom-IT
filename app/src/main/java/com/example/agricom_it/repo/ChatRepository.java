// java
package com.example.agricom_it.repo;

import androidx.annotation.NonNull;

import com.example.agricom_it.model.ChatSummary;
import com.example.agricom_it.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository
{
    private final DatabaseReference chatsRef;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesListener;

    // new fields for chat-list listener
    private ChildEventListener chatsListener;

    public ChatRepository()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        chatsRef = db.getReference("chats");
    }

    public interface MessageCallback
    {
        void onMessage(Message message);
    }

    public interface CreateChatCallback
    {
        void onCreated(boolean success);
    }

    public interface ChatsCallback
    {
        void onChatAdded(ChatSummary chat);
    }

    public void createChat(String chatId, List<Integer> participantIds, CreateChatCallback callback)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("participants", participantIds);
        chatsRef.child(chatId).updateChildren(payload)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        callback.onCreated(task.isSuccessful());
                    }
                });
    }

    public void addMessageListener(String chatId, MessageCallback cb)
    {
        // remove any previous listener first
        removeListener();

        messagesRef = chatsRef.child(chatId).child("messages");
        messagesListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName)
            {
                Message m = snapshot.getValue(Message.class);
                if (m != null) {
                    cb.onMessage(m);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        messagesRef.addChildEventListener(messagesListener);
    }

    public void sendMessage(String chatId, Message message)
    {
        DatabaseReference msgRef = chatsRef.child(chatId).child("messages").push();
        msgRef.setValue(message);
    }

    public void removeListener() {
        if (messagesRef != null && messagesListener != null)
        {
            messagesRef.removeEventListener(messagesListener);
            messagesListener = null;
            messagesRef = null;
        }
    }

    // --- new: listen for chats that include a given user id ---
    public void listenForUserChats(int userId, ChatsCallback cb)
    {
        removeChatsListener();

        chatsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                DataSnapshot pSnap = snapshot.child("participants");
                List<Integer> parts = new ArrayList<>();
                if (pSnap.exists()) {
                    // participants may be stored as a list or map; iterate children
                    for (DataSnapshot child : pSnap.getChildren()) {
                        Object val = child.getValue();
                        if (val instanceof Long) {
                            parts.add(((Long) val).intValue());
                        } else if (val instanceof Integer) {
                            parts.add((Integer) val);
                        } else if (val instanceof String) {
                            try {
                                parts.add(Integer.parseInt((String) val));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }

                if (parts.contains(userId)) {
                    ChatSummary cs = new ChatSummary(snapshot.getKey(), parts);
                    cb.onChatAdded(cs);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        chatsRef.addChildEventListener(chatsListener);
    }

    public void removeChatsListener()
    {
        if (chatsListener != null) {
            chatsRef.removeEventListener(chatsListener);
            chatsListener = null;
        }
    }
}
