package com.example.babycare.MainActivity.Fragments.Community.Personal;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.MessageModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.MessagesAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Services.ChatManager;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class Chat extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ImageButton close_icon;
    CollectionReference messagesCollection;  // Declare this here
    RecyclerView recyclerView;
    MessagesAdapter messagesAdapter;
    TextView headerTxtView;
    ChatManager chatManager;
    ImageButton button_send;
    EditText message_edit_txt;
    List<MessageModel> messageList = new ArrayList<>();
    MyDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        chatManager = ChatManager.getInstance();
        String chatId = chatManager.getChatID();

        headerTxtView = view.findViewById(R.id.header);
        headerTxtView.setText(chatManager.getChatName());

        // Set up the adapter
        recyclerView = view.findViewById(R.id.recycler_view_messages);
        messagesAdapter = new MessagesAdapter(
                messageList,
                uid
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator()); //add animation to the cards
        recyclerView.setAdapter(messagesAdapter);

        message_edit_txt = view.findViewById(R.id.edit_text_message);
        button_send = view.findViewById(R.id.btn_send);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_text = message_edit_txt.getText().toString();
                String messageType = "text";
                if (TextUtils.isEmpty(input_text)) {
                    message_edit_txt.setError("Empty");
                    return;
                }
                chatManager.sendMessage(
                        getContext(),
                        input_text,
                        uid
                );
                message_edit_txt.setText("");
            }
        });
        // Find the FAB and set the onClickListener to handle back navigation
        close_icon = view.findViewById(R.id.close_icon);
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger back navigation
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });


        // Fetch existing messages from SQLite
        loadMessagesFromSQLite(chatId);


        // Fetch new messages from Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        messagesCollection = firestore.collection("chats")
                .document(chatId)
                .collection("messages");
        fetchMessagesFromFirestore(chatId);


        return view;
    }
    private void loadMessagesFromSQLite(String chatId) {
        List<MessageModel> messagesFromSQLite = dbHelper.getAllMessagesForChat(chatId);
        if (!messagesFromSQLite.isEmpty()) {
            messagesAdapter.appendMessages(messagesFromSQLite);
        }
    }

    private void fetchMessagesFromFirestore(String chatId) {
        // Get the latest timestamp from SQLite
        String latestTimestamp = dbHelper.getLatestTimestampForChat(chatId);

        Query query = messagesCollection;

        if (latestTimestamp != null) {
            try {
                // Convert ISO 8601 string to Date
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure the timezone is consistent
                Date latestDate = isoFormat.parse(latestTimestamp);

                // Convert Date to Firestore Timestamp
                Timestamp latestFirestoreTimestamp = new Timestamp(latestDate);

                // Query Firestore for messages after the latest timestamp
                query = messagesCollection.whereGreaterThan("timestamp", latestFirestoreTimestamp);
            } catch (ParseException e) {
                Log.e("Timestamp", "Error parsing timestamp: " + latestTimestamp, e);
            }
        } else {
            // If no messages exist in SQLite, fetch all messages
            query = messagesCollection;
        }

        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                List<MessageModel> messagesToInsert = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    // Map Firestore document to MessageModel2
                    String messageId = document.getId();
                    String message = document.getString("message");
                    String senderId = document.getString("senderId");
                    Timestamp timestamp = document.getTimestamp("timestamp");

                    // Format timestamp for storage in SQLite
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    String isoFormattedTimestamp = isoFormat.format(timestamp.toDate());

                    // Create a MessageModel2 object
                    MessageModel messageModel = new MessageModel(
                            messageId,
                            isoFormattedTimestamp,
                            senderId,
                            message,
                            new ArrayList<>(),
                            chatId
                    );
                    messagesToInsert.add(messageModel);  // Add to list
                }
                // Insert all messages into SQL db

                if (!messagesToInsert.isEmpty()) {
                    messagesAdapter.appendMessages(messagesToInsert);
                    dbHelper.addMessageList(messagesToInsert);
                }
            }
        });
    }

    private String formatTimestamp(Date timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timestamp);

    }
}