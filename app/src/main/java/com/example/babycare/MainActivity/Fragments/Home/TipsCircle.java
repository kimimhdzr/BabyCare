package com.example.babycare.MainActivity.Fragments.Home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.Objects.Tip;
import com.example.babycare.R;

import java.util.ArrayList;

public class TipsCircle extends RecyclerView.Adapter<TipsCircle.MyViewHolder> {
    private int selectedPosition = -1;
    private ArrayList<Tip> tips;

    // Constructor
    public TipsCircle(ArrayList<Tip> tips) {
        this.tips = tips;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout frame;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            frame = itemView.findViewById(R.id.circle);
        }
    }

    public void updateSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TipsCircle.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_circle, parent, false);
        return new TipsCircle.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsCircle.MyViewHolder holder, int position) {
        if (position == selectedPosition) {
            holder.frame.setBackgroundResource(R.drawable.circle_for_tip_indicator);  // Example: Highlight with a yellow background
        } else {
            holder.frame.setBackgroundResource(R.drawable.circle_for_tip_indicator2);  // No highlight for other positions
        }
    }



    @Override
    public int getItemCount() {
        return tips.size();
    }
}
