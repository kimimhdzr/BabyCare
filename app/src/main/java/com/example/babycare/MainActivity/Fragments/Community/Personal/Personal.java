package com.example.babycare.MainActivity.Fragments.Community.Personal;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.ChatModel;
import com.example.babycare.DataBinding.Model.MessageModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.ChatListAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.MessagesAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Services.ChatManager;
import com.example.babycare.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class Personal extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MyDatabaseHelper dbHelper;
    ChatListAdapter chatListAdapter;
    private List<ChatModel> chatList2;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String uid = currentUser.getUid();

        chatList2 = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(
                getContext(),
                chatList2
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatListAdapter);

        db = FirebaseFirestore.getInstance();

        syncWithFirebase(uid);

        // Observe Room database

        return view;
    }

    public void syncWithFirebase(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String field = "";


        // Fetch the last message timestamp asynchronously

        Query query1;
        Query query2;
        if (true) {
            // First sync: retrieve all chats for the user (without timestamp filter)
            query1 = db.collection("chats").whereEqualTo("participants.user_1.userId", uid);
            query2 = db.collection("chats").whereEqualTo("participants.user_2.userId", uid);
        } else {
            // Subsequent syncs: retrieve only chats with updated messages
            query1 = db.collection("chats")
                    .whereEqualTo("participants.user_1", uid)
//                    .whereGreaterThan("last_message.timestamp", new Date(lastupdated))
            ;

            query2 = db.collection("chats")
                    .whereEqualTo("participants.user_2", uid)
//                    .whereGreaterThan("last_message.timestamp", new Date(lastupdated))
            ;

        }

        // Combined results list
        List<DocumentSnapshot> allResults = new ArrayList<>(); // Correct the type

        // Execute the first query
        query1.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                if (!task1.getResult().isEmpty()) {
                    allResults.addAll(task1.getResult().getDocuments());
                }

                // Execute the second query after the first completes
                query2.get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        if (!task2.getResult().isEmpty()) {
                            allResults.addAll(task2.getResult().getDocuments());
                        }

                        List<ChatModel> chatsToInsert = new ArrayList<>();
                        // Process the combined results
                        for (DocumentSnapshot documentSnapshot : allResults) {
                            String chatId = documentSnapshot.getId();

                            String lastMessage_senderId = (String) documentSnapshot.get("lastMessage.senderId");
                            String lastMessage_userName = (String) documentSnapshot.get("lastMessage.userName");
                            String lastMessage_messageId = (String) documentSnapshot.get("lastMessage.messageId");
                            String lastMessage_messageContent = (String) documentSnapshot.get("lastMessage.messageContent");
                            Timestamp lastMessage_timestamp = documentSnapshot.getTimestamp("lastMessage.timestamp");
                            String lastMessage_timestampFormattedTime = formatTimestamp(lastMessage_timestamp.toDate());

                            String user_1_userId = (String) documentSnapshot.get("participants.user_1.userId");
                            String user_2_userId = (String) documentSnapshot.get("participants.user_2.userId");

                            String participant_userId = "";
                            String participant_userName = "";
                            String participant_profilePic = "";

                            // Identify the participant details
                            if (uid.equals(user_1_userId)) {
                                participant_userId = (String) documentSnapshot.get("participants.user_2.userId");
                                participant_userName = (String) documentSnapshot.get("participants.user_2.userName");
                                participant_profilePic = (String) documentSnapshot.get("participants.user_2.profilePic");
                            } else {
                                participant_userId = (String) documentSnapshot.get("participants.user_1.userId");
                                participant_userName = (String) documentSnapshot.get("participants.user_1.userName");
                                participant_profilePic = (String) documentSnapshot.get("participants.user_1.profilePic");
                            }

                            Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");
                            String createdAtFormattedTime = formatTimestamp(createdAt.toDate());

                            // Create a ChatEntity instance
                            ChatModel chat = new ChatModel(
                                    chatId,

                                    lastMessage_senderId,
                                    lastMessage_userName,
                                    lastMessage_messageId,
                                    lastMessage_messageContent,
                                    lastMessage_timestampFormattedTime,

                                    participant_userId,
                                    participant_userName,
                                    participant_profilePic,

                                    createdAtFormattedTime
                            );
                            chatsToInsert.add(chat);

                            Log.d("ChatList", "Fetched chat: " + chatId);
                        }
                        chatListAdapter.setChatList(chatsToInsert);
                        dbHelper.addChatList(chatsToInsert);

                    } else {
                        Log.w("Firebase", "Error getting chats from query2.", task2.getException());
                    }
                });
            } else {
                Log.w("Firebase", "Error getting chats from query1.", task1.getException());
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