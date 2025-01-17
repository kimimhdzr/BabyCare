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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class Chat extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ImageButton close_icon;
    CollectionReference messagesCollection;  // Declare this here
    RecyclerView recyclerView;
    public MessagesAdapter messagesAdapter;
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
                uid,
                recyclerView
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

        fetchMessagesFromFirestore2(chatId);


        return view;
    }

    private void fetchMessagesFromFirestore1(String chatId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats") // Access the chats collection
                .document(chatId) // Specify the document (chat) with the given chat ID
                .collection("messages") // Access the messages subcollection
                .orderBy("timestamp", Query.Direction.ASCENDING) // Order messages by timestamp descending
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Process the retrieved messages
                        List<MessageModel> messagesToInsert = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            // Map Firestore document to MessageModel2
                            String messageId = document.getId();
                            String message = document.getString("message");
                            String senderId = document.getString("senderId");
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String readabletimestamp = formatTimestamp(timestamp.toDate());

                            // Create a MessageModel2 object
                            MessageModel messageModel = new MessageModel(
                                    messageId,
                                    readabletimestamp,
                                    senderId,
                                    message,
                                    new ArrayList<>(),
                                    chatId
                            );
                            messagesToInsert.add(messageModel);  // Add to list
                        }
                    messagesAdapter.setMessages(messagesToInsert);
                    } else {
                        Log.w("RetrieveMessages", "Error getting messages", task.getException());
                    }
                });

    }

    private void fetchMessagesFromFirestore2(String chatId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add a real-time listener
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("RetrieveMessages", "Listen failed", error);
                        return;
                    }

                    if (value != null) {
                        List<MessageModel> messagesToInsert = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            String messageId = document.getId();
                            String message = document.getString("message");
                            String senderId = document.getString("senderId");
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String readableTimestamp = "";
                            if (timestamp != null) {
                                readableTimestamp = formatTimestamp(timestamp.toDate());
                            } else {
                                Log.w("Firestore", "Timestamp is null for document: " + document.getId());
                                readableTimestamp = "N/A"; // or provide a default value
                            }
                            // Map Firestore document to MessageModel
                            MessageModel messageModel = new MessageModel(
                                    messageId,
                                    readableTimestamp,
                                    senderId,
                                    message,
                                    new ArrayList<>(),
                                    chatId
                            );
                            messagesToInsert.add(messageModel);
                        }

                        // Update the RecyclerView adapter with new messages
                        messagesAdapter.setMessages(messagesToInsert);
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