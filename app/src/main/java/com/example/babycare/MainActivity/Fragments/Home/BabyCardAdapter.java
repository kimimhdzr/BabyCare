package com.example.babycare.MainActivity.Fragments.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.Tip;
import com.example.babycare.R;

import java.util.ArrayList;

public class BabyCardAdapter extends RecyclerView.Adapter<BabyCardAdapter.MyViewHolder> {
    private AdapterInt listener;
    private ArrayList<Baby> children;

    // Constructor
    public BabyCardAdapter(ArrayList<Baby> children) {
        this.children = children;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView BabyNameText;
        Button toBabyHome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BabyNameText = itemView.findViewById(R.id.Childname);
            toBabyHome = itemView.findViewById(R.id.BtnToBabyHome);
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
                bundle.putSerializable("session_baby",children.get(holder.getAdapterPosition()));
                Navigation.findNavController(view).navigate(R.id.nav_to_BabyHome,bundle);
            }
        });


    }

    @Override
    public int getItemCount() {
        return children.size();
    }
}