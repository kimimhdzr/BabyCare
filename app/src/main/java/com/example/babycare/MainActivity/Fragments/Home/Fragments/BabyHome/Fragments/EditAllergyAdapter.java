package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;

import java.util.ArrayList;

public class EditAllergyAdapter extends RecyclerView.Adapter<EditAllergyAdapter.MyViewHolder> {

    private ArrayList<String> allergies;
    private OnItemDeleteListener deleteListener;

    // Constructor
    public EditAllergyAdapter(ArrayList<String> allergies,OnItemDeleteListener listener) {
        this.allergies = allergies;
        this.deleteListener = listener;
    }

    public ArrayList<String> getAllergies() {
        return allergies;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        ImageButton delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.allergysubtitle);
            delete = itemView.findViewById(R.id.deleteAllergy);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergy_edit, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the TextView
        String item = allergies.get(position);
        holder.itemText.setText(item);

        holder.delete.setOnClickListener(v -> {
            // Notify the listener
            if (deleteListener != null) {
                deleteListener.onItemDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }

    public void updateList(ArrayList<String> updatedList) {
        this.allergies = updatedList;
        notifyDataSetChanged();
    }

    public void addItem(String newAllergy) {
        allergies.add(newAllergy); // Add the new item to the list
        notifyItemInserted(allergies.size() - 1); // Notify the adapter about the new item
    }
}
