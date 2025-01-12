package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;

import java.util.ArrayList;

public class AllergyAdapter extends RecyclerView.Adapter<AllergyAdapter.MyViewHolder> {

    private ArrayList<String> allergies;

    // Constructor
    public AllergyAdapter(ArrayList<String> allergies) {
        this.allergies = allergies;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.allergy_item);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergy, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the TextView
        String item = allergies.get(position);
        holder.itemText.setText(item);
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }
}
