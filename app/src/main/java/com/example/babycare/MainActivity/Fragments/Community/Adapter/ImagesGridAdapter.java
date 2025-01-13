package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter.ImageViewHolder> {

    private List<String> imagesList; // List of image URLs
    private Context context;
    private static final int VIEW_TYPE_POSTS = 1;
    private static final int VIEW_TYPE_NEW_IMAGES = 2;
    private int condition;

    public ImagesGridAdapter(Context context, List<String> imagesList, int condition) {
        this.context = context;
        this.imagesList = imagesList;
        this.condition = condition;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false); // Custom layout for each image
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imagesList.get(position);

        if (condition == VIEW_TYPE_POSTS) {
            holder.close_icon.setVisibility(View.GONE);
        }
        // Using Glide to load images
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView); // `holder.imageView` should reference the ImageView in your item layout
    }

    public void clearData() {
        imagesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton close_icon;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view); // ImageView in your item layout
            close_icon = itemView.findViewById(R.id.close_icon);
        }
    }
}
