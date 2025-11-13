// java
package com.example.agricom_it.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.adapter.MessageAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.repo.ChatRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import com.example.agricom_it.model.Message;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity
{

    private static final String TAG = "ChatActivity";
    private String chatId;
    private int otherUserId;
    private int currentUserId = -1;

    private ChatRepository repo;
    private ListenerRegistration messagesListener;

    private RecyclerView rvMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private MessageAdapter adapter; // implement a simple adapter for message items

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // ensure layout has recyclerMessages, editMessage, btnSend

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent i = getIntent();
        Log.d(TAG, "Intent received: " + i);
        Log.d(TAG, "Intent extras: " + (i != null ? i.getExtras() : "null"));
        Log.d(TAG, "List of extras: " + (i != null && i.getExtras() != null ? i.getExtras().keySet().toString() : "null"));

        currentUserId = getIntent().getIntExtra("userID", -1);


        chatId = getIntent().getStringExtra("chatId");
        otherUserId = getIntent().getIntExtra("otherUserId", -1);

        Log.i(TAG, "Current User ID: " + currentUserId);

        // If you didn't pass current user id, get from your auth/session
        // currentUserId = MySession.getCurrentUserId();

        rvMessages = findViewById(R.id.chat_recycler);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        repo = new ChatRepository( getApplicationContext() );

        // Start listening for messages
        messagesListener = repo.listenForMessages(chatId, ( snapshots, e )->
        {
            if( e!=null )
            {
                Log.e(TAG, "listen error", e);
                return;
            }
            if( snapshots==null ) return;
            for( DocumentChange dc : snapshots.getDocumentChanges() )
            {
                switch( dc.getType() )
                {
                    case ADDED:
                        // map fields to your Message model
                        String text = dc.getDocument().getString("text");
                        Long sender = dc.getDocument().getLong("senderId");
                        Object ts = dc.getDocument().get("timestamp");
                        Message m = new Message(sender!=null ? sender.intValue() : -1, text, ts);
                        runOnUiThread(()->
                        {
                            adapter.add(m);
                            rvMessages.smoothScrollToPosition(adapter.getItemCount()-1);
                        });
                        break;
                    // handle MODIFIED/REMOVED if needed
                }
            }
        });

        btnSend.setOnClickListener(v->
        {
            Log.d(TAG, "Send button clicked");
            String text = editMessage.getText().toString().trim();

            Log.d(TAG, "Message to send: "+text);

            if( text.isEmpty() )
            {
                Log.d(TAG, "Empty message, not sending");
                return;
            }

            if( chatId==null )
            {
                Log.e(TAG, "chatId is null, cannot send message");
                Toast.makeText(ChatActivity.this, "Cannot send: missing chatId", Toast.LENGTH_SHORT).show();
                return;
            }

            if( btnSend!=null ) btnSend.setEnabled(false);
//            btnSend.setEnabled(false);

            repo.sendMessage(chatId, currentUserId, text, success->
            {
                Log.d(TAG, "Inside send callback invoked");
                try
                {
                    // callback may already run on main thread, but ensure UI updates are on main thread
                    runOnUiThread(()->
                    {
                        Log.d(TAG, "Running UI thread code in send callback");
                        boolean ok = Boolean.TRUE.equals(success); // null-safe
                        Log.d(TAG, "Message send callback, success: "+ok);

                        // guard against activity being finished/destroyed
                        if( isFinishing() || isDestroyed() )
                        {
                            Log.w(TAG, "Activity finishing/destroyed, skipping UI updates");
                            return;
                        }

                        if( btnSend!=null ) btnSend.setEnabled(true);
                        if( ok )
                        {
                            Log.d(TAG, "Message sent successfully");
                            if( editMessage!=null ) editMessage.setText("");
                        }
                        else
                        {
                            Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch( Exception e )
                {
                    Log.e(TAG, "Exception in send callback UI thread", e);
                    // ensure button is re-enabled if possible
                    try
                    {
                        runOnUiThread(()->
                        {
                            if( btnSend!=null ) btnSend.setEnabled(true);
                        });
                    }
                    catch( Exception ignored )
                    {
                    }
                }
            });

//            repo.sendMessage(chatId, currentUserId, text, success -> {
//                Log.d(TAG, "Inside send callback invoked");
//                try {
//                    Log.d(TAG, "Inside send callback");
//                    runOnUiThread(() -> {
//                        Log.d(TAG, "Running UI thread code in send callback");
//                        // null-safe check (if callback used Boolean previously)
//                        boolean ok = Boolean.TRUE.equals(success);
//                        Log.d(TAG, "Message send callback, success: " + ok);
//
//                        // guard against activity being finished
//                        if (isFinishing() || isDestroyed()) {
//                            Log.w(TAG, "Activity finishing/destroyed, skipping UI updates");
//                            return;
//                        }
//
//                        btnSend.setEnabled(true);
//                        if (ok) {
//                            Log.d(TAG, "Message sent successfully");
//                            editMessage.setText("");
//                        } else {
//                            Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                catch (Exception e)
//                {
//                    Log.e(TAG, "Exception in send callback UI thread", e);
//                    // re-enable button to avoid stuck disabled state
//                    runOnUiThread(() -> btnSend.setEnabled(true));
//                }
//            });

//            repo.sendMessage(chatId, currentUserId, text, success->runOnUiThread(()->
//            {
//                boolean ok = Boolean.TRUE.equals(success);
//
//                Log.d(TAG, "Message send callback, success: "+ok);
//                btnSend.setEnabled(true);
//                if( ok )
//                {
//                    Log.d(TAG, "Message sent successfully");
//                    editMessage.setText("");
//                }
//                else
//                {
//                    Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show();
//                }
//            }));
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if( messagesListener!=null )
        {
            messagesListener.remove();
        }
    }

    // Minimal message model and adapter placeholders

}
