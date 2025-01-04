package com.example.babycareconnect;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class addAllergyViewHolder extends RecyclerView.ViewHolder {

    EditText allergyInput;
    ImageButton removeButton;
    public addAllergyViewHolder(@NonNull View itemView) {
        super(itemView);
        allergyInput=itemView.findViewById(R.id.allergyInput);
        removeButton=itemView.findViewById(R.id.removeBtn);
    }
}
