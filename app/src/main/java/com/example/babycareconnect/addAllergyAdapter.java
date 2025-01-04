package com.example.babycareconnect;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class addAllergyAdapter extends RecyclerView.Adapter<addAllergyViewHolder> {

    Context context;
    List<allergyItem> allergyItems;

    public addAllergyAdapter(Context context, List<com.example.babycareconnect.allergyItem> allergyItems) {
        this.context = context;
        this.allergyItems = allergyItems;
    }

    @NonNull
    @Override
    public addAllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new addAllergyViewHolder(LayoutInflater.from(context).inflate(R.layout.allergy_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull addAllergyViewHolder holder, int position) {
        allergyItem item = allergyItems.get(position);

        // Remove old TextWatcher if any
        if (holder.allergyInput.getTag() instanceof TextWatcher) {
            holder.allergyInput.removeTextChangedListener((TextWatcher) holder.allergyInput.getTag());
        }

        // Set current text
        holder.allergyInput.setText(item.getAllergyInput());

        // Add new TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setAllergyInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.allergyInput.addTextChangedListener(textWatcher);
        holder.allergyInput.setTag(textWatcher);

        // Remove button functionality
        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                allergyItems.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, allergyItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return allergyItems.size();
    }
}
