package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.MessageModel;
import com.example.babycare.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_OUTGOING = 1;
    private static final int VIEW_TYPE_INCOMING = 2;
    private List<MessageModel> messageList;
    private String uid;

    public MessagesAdapter(List<MessageModel> messageList, String uid) {
        this.messageList = messageList;
        this.uid = uid;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        // Check if the messageModel sender matches the current user role
        return message.getChatID().equals(uid) ? VIEW_TYPE_OUTGOING : VIEW_TYPE_INCOMING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_OUTGOING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_message_item, parent, false);
            return new OutgoingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_message_item, parent, false);
            return new IncomingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (holder instanceof OutgoingViewHolder) {
            ((OutgoingViewHolder) holder).bind(message);
        } else {
            ((IncomingViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        Log.e("MessagesAdapter", "Item count: " + messageList.size());  // Add this log
        return messageList.size();

    }

    public void setMessages(List<MessageModel> newMessages) {
        this.messageList.clear();        // Clear the old messages
        this.messageList.addAll(newMessages);  // Add new messages
        notifyDataSetChanged();          // Notify the adapter that data has changed
    }
    // Optionally, if you want to append new messages instead of replacing
    public void appendMessages(List<MessageModel> newMessages) {
        int previousSize = this.messageList.size();
        this.messageList.addAll(newMessages);  // Add new messages to the existing list
        notifyItemRangeInserted(previousSize, newMessages.size()); // Notify for new range
    }


    // ViewHolder for outgoing messages
    public static class OutgoingViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;

        public OutgoingViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_outgoing_message);
            timestamp = itemView.findViewById(R.id.text_outgoing_time);
        }

        public void bind(MessageModel message) {
            messageText.setText(message.getMessage()); // Set the actual messageModel text
            timestamp.setText(message.getTimestamp());
        }
    }

    // ViewHolder for incoming messages
    public static class IncomingViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;

        public IncomingViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_incoming_message);
            timestamp = itemView.findViewById(R.id.text_incoming_time);
        }

        public void bind(MessageModel message) {
            messageText.setText(message.getMessage()); // Set the actual messageModel text
            timestamp.setText(message.getTimestamp());
        }
    }
}
