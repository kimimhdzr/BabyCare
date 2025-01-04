package com.example.babycareconnect;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class ManageChildViewHolder extends RecyclerView.ViewHolder {

    ShapeableImageView babyProfileView;
    TextView babyNameView;
    ImageButton editBtn, deleteBtn;
    public ManageChildViewHolder(@NonNull View itemView) {
        super(itemView);
        babyProfileView=itemView.findViewById(R.id.babyProfilePicture);
        babyNameView=itemView.findViewById(R.id.babyNameTV);
        editBtn=itemView.findViewById(R.id.editBtn);
        deleteBtn=itemView.findViewById(R.id.deleteBtn);
    }
}
