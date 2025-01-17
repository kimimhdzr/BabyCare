package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.ChatModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Services.ChatManager;
import com.example.babycare.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.CardViewHolder> {

    private Context context;
    private List<ChatModel> chatList;
    ChatManager chatManager;

    public ChatListAdapter(Context context,
                           List<ChatModel> chatList
    ) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list_personal, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ChatModel chat = chatList.get(position);
        String userName = (String) chat.getParticipants().get("userName");
        String profilePic = (String) chat.getParticipants().get("profilePic");
        String messageContent = (String) chat.getLastMessage().get("messageContent");
        String timestamp = (String) chat.getLastMessage().get("timestamp");
        String chatID = chat.getDocumentID();

        holder.nametxt.setText(userName);
        holder.lastmessagetimestamptxt.setText(timestamp);
        holder.lastmessagetxt.setText(messageContent);
        Glide.with(context)
                .load(profilePic) // Replace with your drawable resource
                .into(holder.profile_image);

        chatManager = ChatManager.getInstance();
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatManager.setChatID(chatID);
                chatManager.setChatName(userName);

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_to_Chat);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setChatList(List<ChatModel> newChatList) {
        this.chatList.clear();
        this.chatList.addAll(newChatList);
        notifyDataSetChanged(); // Refresh the list
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView nametxt;
        TextView lastmessagetimestamptxt;
        TextView lastmessagetxt;
        MaterialCardView materialCardView;
        ShapeableImageView profile_image;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nametxt = itemView.findViewById(R.id.name);
            lastmessagetimestamptxt = itemView.findViewById(R.id.lastmessagetimestamp);
            lastmessagetxt = itemView.findViewById(R.id.lastmessage);
            materialCardView = itemView.findViewById(R.id.chatMaterialCard);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
