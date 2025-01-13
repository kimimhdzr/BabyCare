package com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.TipModel;
import com.example.babycare.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.MyViewHolder> {
    private AdapterInt listener;
    private ArrayList<TipModel> tips;

    // Constructor
    public TipsAdapter(ArrayList<TipModel> tips, AdapterInt listener) {
        this.tips = tips;
        this.listener = listener;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TitleText, DescText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TitleText = itemView.findViewById(R.id.tip_title);
            DescText = itemView.findViewById(R.id.tip_desc);
        }
    }

    @NonNull
    @Override
    public TipsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tips_card, parent, false);
        return new TipsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsAdapter.MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemSelected(position);
            }
        });


        holder.DescText.setText(tips.get(position).getDesc());
        holder.TitleText.setText(tips.get(position).getTitle());


    }

    @Override
    public int getItemCount() {
        return tips.size();
    }
}