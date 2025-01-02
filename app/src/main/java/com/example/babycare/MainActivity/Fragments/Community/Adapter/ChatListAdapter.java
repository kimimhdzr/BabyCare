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

import com.example.babycare.DataBinding.Model.ChatModel;
//import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Services.ChatManager;
import com.example.babycare.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.CardViewHolder> {

    private Context context;
    private List<ChatModel> chatList;
    //MyDatabaseHelper dbHelper;
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

        /*userProfile = new CurrentUserSharedPreference(context);
        String role = userProfile.getRole();

        String name = "";
        if (role.equals("user")) {
            name = chat.getCompanyName();
        } else {
            name = chat.getUserName();
        }

        holder.nametxt.setText(name);
        holder.lastmessagetimestamptxt.setText(chat.getLastMessageTimestamp());
        holder.lastmessagetxt.setText(chat.getLastMessageContent());

        chatManager = ChatManager.getInstance();
        final String nameinner = name;
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatManager.setChatID(chat.getChatId());
                chatManager.setChatName(nameinner);

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_to_Pe);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

//    public void updateChatList(List<ChatModel> newChatList) {
//        this.chatList = newChatList;
//        notifyDataSetChanged(); // Refresh the list
//    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView nametxt;
        TextView lastmessagetimestamptxt;
        TextView lastmessagetxt;
        MaterialCardView materialCardView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            nametxt = itemView.findViewById(R.id.name);
            lastmessagetimestamptxt = itemView.findViewById(R.id.lastmessagetimestamp);
            lastmessagetxt = itemView.findViewById(R.id.lastmessage);
            materialCardView = itemView.findViewById(R.id.chatMaterialCard);
        }
    }
}
