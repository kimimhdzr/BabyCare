package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments.BabyProfile;
import com.example.babycare.R;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BabyCardAdapter extends RecyclerView.Adapter<BabyCardAdapter.MyViewHolder> {

    private Context context;
    private AdapterInt listener;
    private ArrayList<BabyProfileModel> children;

    // Constructor
    public BabyCardAdapter(Context context, ArrayList<BabyProfileModel> children) {
        this.context = context;
        this.children = children;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView BabyNameText;
        Button toBabyHome;
        ShapeableImageView profileImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BabyNameText = itemView.findViewById(R.id.Childname);
            toBabyHome = itemView.findViewById(R.id.BtnToBabyHome);
            profileImageView = itemView.findViewById(R.id.baby_pfp);
        }
    }

    @NonNull
    @Override
    public BabyCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_children_home, parent, false);
        return new BabyCardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BabyCardAdapter.MyViewHolder holder, int position) {
        holder.BabyNameText.setText(children.get(position).getName());


        holder.toBabyHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("session_baby", children.get(holder.getAdapterPosition()));
                Navigation.findNavController(view).navigate(R.id.nav_to_BabyHome,bundle);
            }
        });

        // ImageView in your layout

        // Use Picasso to load the image from the URL into the ImageView
        Glide.with(context)
                .load(children.get(position).getProfilePic())  // Pass the image URL
                .into(holder.profileImageView);  // Set the ImageView to display the image


    }

    @Override
    public int getItemCount() {
        return children.size();
    }
}