// java
package com.example.agricom_it.repo;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.agricom_it.MainActivity;
import com.example.agricom_it.model.ChatSummary;
import com.example.agricom_it.model.Message;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class ChatRepository
{
    private final DatabaseReference chatsRef;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesListener;

    // new fields for chat-list listener
    private ChildEventListener chatsListener;
    private final FirebaseFirestore db;
    private final Context appContext;
    private final String TAG = "ChatRepository";


    public ChatRepository( Context context )
    {
        appContext = context.getApplicationContext();

        try
        {
            if( FirebaseApp.getApps(appContext).isEmpty() )
            {
                FirebaseApp.initializeApp(appContext);
                Log.d(TAG, "FirebaseApp.initializeApp called from ChatRepository constructor");
            }
        }
        catch( Throwable t )
        {
            Log.w(TAG, "FirebaseApp check/initialize failed (ignored): "+t.getMessage());
        }

        db = FirebaseFirestore.getInstance();
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    public interface MessageCallback
    {
        void onMessage( Message message );
    }

    public interface CreateChatCallback
    {
        void onComplete( boolean success );
    }

    public interface SimpleCallback
    {
        void onComplete( boolean success );
    }

    public interface ChatsCallback
    {
        void onChatAdded( ChatSummary chat );
    }

    public void createChat( @NonNull String chatId, @NonNull List<Integer> participants, @NonNull CreateChatCallback cb )
    {
        Log.d(TAG, "Creating chat "+chatId+" with participants "+participants);

        final DocumentReference doc = db.collection("chats").document(chatId);

        try
        {
            Log.d(TAG, "Fetching document for chatId: "+chatId);

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete( @NonNull Task<DocumentSnapshot> task )
                {
                    if( task.isSuccessful() )
                    {
                        DocumentSnapshot snapshot = task.getResult();
                        if( snapshot!=null && snapshot.exists() )
                        {
                            Log.d(TAG, "Chat document already exists for chatId: "+chatId);
                            cb.onComplete(true);
                        }
                        else
                        {
                            Log.d(TAG, "Chat document does not exist. Creating new chat document for chatId: "+chatId);
                            Map<String, Object> data = new HashMap<>();
                            data.put("participants", participants);
                            data.put("createdAt", FieldValue.serverTimestamp());

                            doc.set(data)
                                    .addOnSuccessListener(aVoid->
                                    {
                                        Log.d(TAG, "Created chat document for chatId: "+chatId);
                                        cb.onComplete(true);
                                    })
                                    .addOnFailureListener(e->
                                    {
                                        Log.e(TAG, "Failed to create chat document for chatId: "+chatId, e);
                                        cb.onComplete(false);
                                    });

                        }
                    }
                    else
                    {
                        cb.onComplete(false);
                    }
                }
            }).addOnFailureListener(e->
            {
                // extra failure listener for redundancy
                Log.e(TAG, "get() failed for chatId: "+chatId, e);
                cb.onComplete(false);
            });
        }
        catch( Exception e )
        {
            Log.e(TAG, "Exception while creating chat: "+e.getMessage());
            cb.onComplete(false);
        }

    }

    public void sendMessage( @NonNull String chatId, int senderId, @NonNull String text, @NonNull SimpleCallback cb )
    {
        Log.d(TAG, "sendMessage: entry");
        try
        {
            try
            {
                if( FirebaseApp.getApps(appContext).isEmpty() )
                {
                    Log.e(TAG, "FirebaseApp not initialized");
                    safeCallback(false, cb);
                    return;
                }
                else
                {
                    Log.i(TAG, "FirebaseApp already initialized");
                }
            }
            catch( Throwable t )
            {
                Log.w(TAG, "FirebaseApp check failed (ignored): "+t.getMessage());
            }

            if( db==null )
            {
                Log.e(TAG, "Firestore instance is null. Make sure Firebase is initialized.");
                safeCallback(false, cb);
                return;
            }
            if( chatId==null )
            {
                Log.e(TAG, "chatId is null");
                safeCallback(false, cb);
                return;
            }

            Map<String, Object> msg = new HashMap<>();
            msg.put("senderId", senderId);
            msg.put("text", text);
            msg.put("timestamp", FieldValue.serverTimestamp());

            CollectionReference messagesRef = db.collection("chats")
                    .document(chatId)
                    .collection("messages");

            messagesRef.add(msg).addOnCompleteListener(task->
            {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(()->
                {
                    try
                    {
                        if( task.isSuccessful() )
                        {
                            DocumentReference docRef = task.getResult();
                            Log.d(TAG, "sendMessage: success id="+(docRef!=null ? docRef.getId() : "null"));
                            safeCallback(true, cb);
                        }
                        else
                        {
                            Exception e = task.getException();
                            Log.e(TAG, "sendMessage: add failed", e);
                            safeCallback(false, cb);
                        }
                    }
                    catch( Throwable t )
                    {
                        Log.e(TAG, "sendMessage: exception handling add result: "+Log.getStackTraceString(t));
                        safeCallback(false, cb);
                    }
                });
            }).addOnFailureListener(e->
            {
                Log.e(TAG, "sendMessage: addOnFailureListener", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(()->safeCallback(false, cb));
            });
        }
        catch( Throwable t )
        {
            Log.e(TAG, "sendMessage: CRASHED - "+Log.getStackTraceString(t));
            safeCallback(false, cb);
        }
    }

    // helper to safely call callback on main thread
    private void safeCallback( final boolean success, @NonNull final SimpleCallback cb )
    {
        try
        {
            if( Looper.myLooper()==Looper.getMainLooper() )
            {
                try
                {
                    cb.onComplete(success);
                }
                catch( Throwable cbt )
                {
                    Log.e(TAG, "Callback threw", cbt);
                }
            }
            else
            {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(()->
                {
                    try
                    {
                        cb.onComplete(success);
                    }
                    catch( Throwable cbt )
                    {
                        Log.e(TAG, "Callback threw", cbt);
                    }
                });
            }
        }
        catch( Throwable t )
        {
            Log.e(TAG, "safeCallback failed: "+Log.getStackTraceString(t));
        }
    }

    // listen to messages in real time, returns ListenerRegistration so caller can remove listener
    public ListenerRegistration listenForMessages( @NonNull String chatId, @NonNull EventListener<QuerySnapshot> listener )
    {
        return db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(listener);
    }


//    public void addMessageListener(String chatId, MessageCallback cb)
//    {
//        // remove any previous listener first
//        removeListener();
//
//        messagesRef = chatsRef.child(chatId).child("messages");
//        messagesListener = new ChildEventListener()
//        {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName)
//            {
//                Message m = snapshot.getValue(Message.class);
//                if (m != null) {
//                    cb.onMessage(m);
//                }
//            }
//
//            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
//            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
//            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
//            @Override public void onCancelled(@NonNull DatabaseError error) {}
//        };
//        messagesRef.addChildEventListener(messagesListener);
//    }

//    public void sendMessage(String chatId, Message message)
//    {
//        DatabaseReference msgRef = chatsRef.child(chatId).child("messages").push();
//        msgRef.setValue(message);
//    }

    public void removeListener()
    {
        if( messagesRef!=null && messagesListener!=null )
        {
            messagesRef.removeEventListener(messagesListener);
            messagesListener = null;
            messagesRef = null;
        }
    }

    // --- new: listen for chats that include a given user id ---
    public void listenForUserChats( int userId, ChatsCallback cb )
    {
        removeChatsListener();

        chatsListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded( @NonNull DataSnapshot snapshot, String previousChildName )
            {
                DataSnapshot pSnap = snapshot.child("participants");
                List<Integer> parts = new ArrayList<>();
                if( pSnap.exists() )
                {
                    // participants may be stored as a list or map; iterate children
                    for( DataSnapshot child : pSnap.getChildren() )
                    {
                        Object val = child.getValue();
                        if( val instanceof Long )
                        {
                            parts.add(((Long) val).intValue());
                        }
                        else if( val instanceof Integer )
                        {
                            parts.add((Integer) val);
                        }
                        else if( val instanceof String )
                        {
                            try
                            {
                                parts.add(Integer.parseInt((String) val));
                            }
                            catch( NumberFormatException ignored )
                            {
                            }
                        }
                    }
                }

                if( parts.contains(userId) )
                {
                    ChatSummary cs = new ChatSummary(snapshot.getKey(), parts);
                    cb.onChatAdded(cs);
                }
            }

            @Override
            public void onChildChanged( @NonNull DataSnapshot snapshot, String previousChildName )
            {
            }

            @Override
            public void onChildRemoved( @NonNull DataSnapshot snapshot )
            {
            }

            @Override
            public void onChildMoved( @NonNull DataSnapshot snapshot, String previousChildName )
            {
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error )
            {
            }
        };

        chatsRef.addChildEventListener(chatsListener);
    }

    public void removeChatsListener()
    {
        if( chatsListener!=null )
        {
            chatsRef.removeEventListener(chatsListener);
            chatsListener = null;
        }
    }
}
