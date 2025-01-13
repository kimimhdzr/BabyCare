package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.CommentModel;
import com.example.babycare.DataBinding.Model.PostModel;
import com.example.babycare.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CardViewHolder>  {

    private Context context;
    private List<CommentModel> commentList;
    ImagesGridAdapter imagesGridAdapter;

    public CommentAdapter(
            Context context,
            List<CommentModel> commentList) {

        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CardViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);

        Log.e("PostsAdapter", "Binding post: " + comment.getDescription());  // Add this log

        holder.name_txt.setText(comment.getUserName());
        holder.email_txt.setText("");
        holder.context_txt.setText(comment.getDescription());
        holder.timestamp_txt.setText(comment.getTimestamp());

        if (comment.getProfilePic() != null) {
            Glide.with(context)
                    .load(comment.getProfilePic())
                    .into(holder.profile_image); // Replace with your ImageView reference
        }

        // Set up the RecyclerView for images
        List<String> postImages = comment.getAttachments();  // Get images for the current post
        imagesGridAdapter = new ImagesGridAdapter(
                holder.itemView.getContext(),
                postImages,
                1
        );

        // Set the RecyclerView to display in grid mode
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                holder.itemView.getContext(),
                2
        );
        holder.images_recycler_view.setLayoutManager(gridLayoutManager);
        holder.images_recycler_view.setAdapter(imagesGridAdapter);

    }

    @Override
    public int getItemCount() {
        Log.e("PostsAdapter", "Item count: " + commentList.size());  // Add this log
        return commentList.size();
    }

    public void setComment(List<CommentModel> newComment) {
        commentList.clear();
        commentList.addAll(newComment);
        notifyDataSetChanged();
    }

    public void appendComment(List<CommentModel> newComments) {
        int previousSize = this.commentList.size();
        this.commentList.addAll(newComments);  // Add new messages to the existing list
        notifyItemRangeInserted(previousSize, newComments.size()); // Notify for new range
    }
    public void appendComment(CommentModel newComment) {
        int previousSize = this.commentList.size();
        this.commentList.add(newComment);  // Add new messages to the existing list
        notifyItemRangeInserted(previousSize, 1); // Notify for new range
    }
    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView name_txt;
        TextView email_txt;
        TextView context_txt;
        TextView timestamp_txt;
        MaterialCardView postMaterialCard;
        ShapeableImageView profile_image;
        RecyclerView images_recycler_view;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            postMaterialCard = itemView.findViewById(R.id.postMaterialCard);

            name_txt = itemView.findViewById(R.id.name);
            email_txt = itemView.findViewById(R.id.email);
            context_txt = itemView.findViewById(R.id.context);
            timestamp_txt = itemView.findViewById(R.id.postDate);

            profile_image = itemView.findViewById(R.id.profile_image);

            images_recycler_view = itemView.findViewById(R.id.images_recycler_view);
        }
    }
}