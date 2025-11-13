package com.example.agricom_it.repo;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.ChatSummary;
import com.example.agricom_it.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {
    private final DatabaseReference chatsRef;
    private DatabaseReference messagesRef;
    private ChildEventListener messagesListener;
    private ChildEventListener chatsListener;
    private final FirebaseFirestore db;
    private final Context appContext;
    private final String TAG = "ChatRepository";

    //-------------------------------------------------------------------------------[ChatRepository]
    public ChatRepository(Context context) {
        appContext = context.getApplicationContext();
        try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext);
                Log.d(TAG, "FirebaseApp.initializeApp called from ChatRepository constructor");
            }
        } catch (Throwable t) {
            Log.w(TAG, "FirebaseApp check/initialize failed (ignored): " + t.getMessage());
        }

        db = FirebaseFirestore.getInstance();
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    //--------------------------------------------------------------------------[CreateChatCallback]
    public interface CreateChatCallback {
        void onComplete(boolean success);
    }

    //------------------------------------------------------------------------------[SimpleCallback]
    public interface SimpleCallback {
        void onComplete(boolean success);
    }

    //-------------------------------------------------------------------------------[ChatsCallback]
    public interface ChatsCallback {
        void onChatAdded(ChatSummary chat);

        void onChatsUpdated(List<ChatSummary> chats);
    }

    //----------------------------------------------------------------------------------[createChat]
    public void createChat(@NonNull String chatId, @NonNull List<Integer> participants, @NonNull CreateChatCallback cb) {
        final DocumentReference doc = db.collection("chats").document(chatId);

        try {
            Log.d(TAG, "Fetching document for chatId: " + chatId);

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Chat document already exists for chatId: " + chatId);
                            cb.onComplete(true);
                        } else {
                            Log.d(TAG, "Chat document does not exist. Creating new chat document for chatId: " + chatId);
                            Map<String, Object> data = new HashMap<>();
                            data.put("participants", participants);
                            data.put("createdAt", FieldValue.serverTimestamp());
                            data.put("lastMessageTimestamp", FieldValue.serverTimestamp());

                            doc.set(data)
                                    .addOnSuccessListener(aVoid ->
                                    {
                                        Log.d(TAG, "Created chat document for chatId: " + chatId);
                                        cb.onComplete(true);
                                    })
                                    .addOnFailureListener(e ->
                                    {
                                        Log.e(TAG, "Failed to create chat document for chatId: " + chatId, e);
                                        cb.onComplete(false);
                                    });
                        }
                    } else {
                        cb.onComplete(false);
                    }
                }
            }).addOnFailureListener(e ->
            {
                Log.e(TAG, "get() failed for chatId: " + chatId, e);
                cb.onComplete(false);
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating chat: " + e.getMessage());
            cb.onComplete(false);
        }
    }

    //---------------------------------------------------------------------------------[sendMessage]
    public void sendMessage(@NonNull String chatId, int senderId, @NonNull String text, @NonNull SimpleCallback cb) {
        Log.d(TAG, "sendMessage: entry");
        try {
            try {
                if (FirebaseApp.getApps(appContext).isEmpty()) {
                    Log.e(TAG, "FirebaseApp not initialized");
                    safeCallback(false, cb);
                    return;
                } else {
                    Log.i(TAG, "FirebaseApp already initialized");
                }
            } catch (Throwable t) {
                Log.w(TAG, "FirebaseApp check failed (ignored): " + t.getMessage());
            }
            if (db == null) {
                Log.e(TAG, "Firestore instance is null. Make sure Firebase is initialized.");
                safeCallback(false, cb);
                return;
            }
            if (chatId == null) {
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

            messagesRef.add(msg).addOnCompleteListener(task ->
            {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                {
                    try {
                        if (task.isSuccessful()) {
                            DocumentReference docRef = task.getResult();
                            Log.d(TAG, "sendMessage: success id=" + (docRef != null ? docRef.getId() : "null"));

                            db.collection("chats").document(chatId)
                                    .update("lastMessageTimestamp", FieldValue.serverTimestamp())
                                    .addOnSuccessListener(aVoid ->
                                    {
                                        Log.d(TAG, "Updated lastMessageTimestamp for chatId: " + chatId);
                                    })
                                    .addOnFailureListener(e ->
                                    {
                                        Log.w(TAG, "Failed to update lastMessageTimestamp for chatId: " + chatId, e);
                                    });

                            safeCallback(true, cb);
                        } else {
                            Exception e = task.getException();
                            Log.e(TAG, "sendMessage: add failed", e);
                            safeCallback(false, cb);
                        }
                    } catch (Throwable t) {
                        Log.e(TAG, "sendMessage: exception handling add result: " + Log.getStackTraceString(t));
                        safeCallback(false, cb);
                    }
                });
            }).addOnFailureListener(e ->
            {
                Log.e(TAG, "sendMessage: addOnFailureListener", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> safeCallback(false, cb));
            });
        } catch (Throwable t) {
            Log.e(TAG, "sendMessage: CRASHED - " + Log.getStackTraceString(t));
            safeCallback(false, cb);
        }
    }

    //-
    // helper to safely call callback on main thread
    private void safeCallback(final boolean success, @NonNull final SimpleCallback cb) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                try {
                    cb.onComplete(success);
                } catch (Throwable cbt) {
                    Log.e(TAG, "Callback threw", cbt);
                }
            } else {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                {
                    try {
                        cb.onComplete(success);
                    } catch (Throwable cbt) {
                        Log.e(TAG, "Callback threw", cbt);
                    }
                });
            }
        } catch (Throwable t) {
            Log.e(TAG, "safeCallback failed: " + Log.getStackTraceString(t));
        }
    }

    //---------------------------------------------------------------------------[listenForMessages]
    // listen to messages in real time, returns ListenerRegistration so caller can remove listener
    public ListenerRegistration listenForMessages(@NonNull String chatId, @NonNull EventListener<QuerySnapshot> listener) {
        return db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(listener);
    }

    //------------------------------------------------------------------------------[removeListener]
    public void removeListener() {
        if (messagesRef != null && messagesListener != null) {
            messagesRef.removeEventListener(messagesListener);
            messagesListener = null;
            messagesRef = null;
        }
    }

    //-------------------------------------------------------------------------[removeChatsListener]
    public void removeChatsListener() {
        if (chatsListener != null) {
            chatsRef.removeEventListener(chatsListener);
            chatsListener = null;
        }
    }

    //-----------------------------------------------------------------[listenForUserChatsFirestore]
    public ListenerRegistration listenForUserChatsFirestore(int userId, ChatsCallback cb) {
        return db.collection("chats")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) ->
                {
                    if (e != null || snapshots == null) {
                        Log.e(TAG, "listenForUserChatsFirestore: error or null snapshots", e);
                        return;
                    }

                    List<DocumentSnapshot> docs = snapshots.getDocuments();
                    if (docs.isEmpty()) {
                        // deliver empty list on main thread
                        new Handler(Looper.getMainLooper()).post(() -> cb.onChatsUpdated(new ArrayList<>()));
                        return;
                    }

                    List<ChatSummary> summaries = new ArrayList<>(docs.size());
                    final java.util.concurrent.atomic.AtomicInteger remaining = new java.util.concurrent.atomic.AtomicInteger(docs.size());

                    for (DocumentSnapshot ds : docs) {
                        List<Integer> parts = new ArrayList<>();
                        Object pObj = ds.get("participants");

                        if (pObj instanceof List) {
                            for (Object o : (List<?>) pObj) {
                                if (o instanceof Number)
                                    parts.add(((Number) o).intValue());
                                else if (o instanceof String) try {
                                    parts.add(Integer.parseInt((String) o));
                                } catch (Exception ignored) {
                                    Log.e(TAG, "listenForUserChatsFirestore: failed to parse participant ID: " + o);
                                }
                            }
                        } else if (pObj instanceof Map) {
                            for (Object o : ((Map<?, ?>) pObj).values()) {
                                if (o instanceof Number)
                                    parts.add(((Number) o).intValue());
                                else if (o instanceof String) try {
                                    parts.add(Integer.parseInt((String) o));
                                } catch (Exception ignored) {
                                    Log.e(TAG, "listenForUserChatsFirestore: failed to parse participant ID from map: " + o);
                                }
                            }
                        }

                        AuthApiService api = ApiClient.getService();
                        ChatSummary cs = new ChatSummary(ds.getId(), parts, userId);

                        // continuation that must run after username is available
                        Runnable continuation = () ->
                        {
                            // try to read lastMessageTimestamp directly from chat doc (denormalized value)
                            Object tsObj = ds.get("lastMessageTimestamp");
                            if (tsObj instanceof com.google.firebase.Timestamp) {
                                cs.lastMessageTimestamp = ((com.google.firebase.Timestamp) tsObj).toDate().getTime();
                            } else if (tsObj instanceof Number) {
                                cs.lastMessageTimestamp = ((Number) tsObj).longValue();
                            } else if (tsObj instanceof java.util.Date) {
                                cs.lastMessageTimestamp = ((java.util.Date) tsObj).getTime();
                            }

                            summaries.add(cs);

                            // fetch last message text (optional). On completion decrement counter.
                            db.collection("chats")
                                    .document(ds.getId())
                                    .collection("messages")
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(taskMsgs ->
                                    {
                                        if (taskMsgs.isSuccessful() && taskMsgs.getResult() != null && !taskMsgs.getResult().isEmpty()) {
                                            DocumentSnapshot mdoc = taskMsgs.getResult().getDocuments().get(0);
                                            cs.lastMessageText = mdoc.getString("text");
                                            Object mts = mdoc.get("timestamp");
                                            if (mts instanceof com.google.firebase.Timestamp)
                                                cs.lastMessageTimestamp = ((com.google.firebase.Timestamp) mts).toDate().getTime();
                                            else if (mts instanceof Number)
                                                cs.lastMessageTimestamp = ((Number) mts).longValue();
                                            else if (mts instanceof java.util.Date)
                                                cs.lastMessageTimestamp = ((java.util.Date) mts).getTime();
                                        }

                                        // fetch username (optional fallback from Firestore users collection)
                                        int otherId = parts.stream().mapToInt(p -> p).filter(p -> p != userId).findFirst().orElse(-1);
                                        if (otherId != -1) {
                                            db.collection("users").document(String.valueOf(otherId)).get()
                                                    .addOnCompleteListener(taskUser ->
                                                    {
                                                        if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                                                            DocumentSnapshot userDoc = taskUser.getResult();
                                                            String uname = userDoc.getString("username");
                                                            if (uname == null)
                                                                uname = userDoc.getString("displayName");
                                                            if (uname == null)
                                                                uname = userDoc.getString("name");
                                                            // only overwrite if api didn't supply one
                                                            if (cs.otherUsername == null)
                                                                cs.otherUsername = uname;
                                                        }
                                                        if (remaining.decrementAndGet() == 0) {
                                                            summaries.sort(ChatRepository::compareByTimestampDesc);
                                                            new Handler(Looper.getMainLooper()).post(() -> cb.onChatsUpdated(new ArrayList<>(summaries)));
                                                        }
                                                    })
                                                    .addOnFailureListener(f ->
                                                    {
                                                        if (remaining.decrementAndGet() == 0) {
                                                            summaries.sort(ChatRepository::compareByTimestampDesc);
                                                            new Handler(Looper.getMainLooper()).post(() -> cb.onChatsUpdated(new ArrayList<>(summaries)));
                                                        }
                                                    });
                                        } else {
                                            if (remaining.decrementAndGet() == 0) {
                                                summaries.sort(ChatRepository::compareByTimestampDesc);
                                                new Handler(Looper.getMainLooper()).post(() -> cb.onChatsUpdated(new ArrayList<>(summaries)));
                                            }
                                        }
                                    })
                                    .addOnFailureListener(fail ->
                                    {
                                        if (remaining.decrementAndGet() == 0) {
                                            summaries.sort(ChatRepository::compareByTimestampDesc);
                                            new Handler(Looper.getMainLooper()).post(() -> cb.onChatsUpdated(new ArrayList<>(summaries)));
                                        }
                                    });
                        };

                        // fetch username async, then run continuation (fetchOtherUsername posts callback on main thread)
                        if (api != null) {
                            cs.fetchOtherUsername(api, userId, username ->
                            {
                                continuation.run();
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(continuation);
                        }
                    }
                });
    }

    //----------------------------------------------------------------------[compareByTimestampDesc]
    private static int compareByTimestampDesc(com.example.agricom_it.model.ChatSummary a, com.example.agricom_it.model.ChatSummary b) {
        Long ta = a.lastMessageTimestamp;
        Long tb = b.lastMessageTimestamp;
        if (ta == null && tb == null) return 0;
        if (ta == null) return 1;
        if (tb == null) return -1;
        return Long.compare(tb, ta);
    }
}


