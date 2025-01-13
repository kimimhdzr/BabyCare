package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.R;
import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.PostModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class PublicPostsAdapter extends RecyclerView.Adapter<PublicPostsAdapter.CardViewHolder>  {

    private Context context;
    private List<PostModel> postsList;
    ImagesGridAdapter imagesGridAdapter;

    public PublicPostsAdapter(
            Context context,
            List<PostModel> postsList) {

        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_public_post, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PostModel post = postsList.get(position);

        Log.e("PostsAdapter", "Binding post: " + post.getDescription());  // Add this log

        holder.name_txt.setText(post.getUserName());
        holder.email_txt.setText("");
        holder.context_txt.setText(post.getDescription());
        holder.timestamp_txt.setText(post.getTimestamp());

        holder.comment_count_txt.setText("1");
        holder.like_count_txt.setText("1");

        if (post.getProfilePic() != null) {
            Glide.with(context)
                    .load(post.getProfilePic())
                    .into(holder.profile_image); // Replace with your ImageView reference
        }

        // Set up the RecyclerView for images
        List<String> postImages = post.getAttachments();  // Get images for the current post
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
        Log.e("PostsAdapter", "Item count: " + postsList.size());  // Add this log
        return postsList.size();
    }

    public void setPosts(List<PostModel> newPosts) {
        postsList.clear();
        postsList.addAll(newPosts);
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView name_txt;
        TextView email_txt;
        TextView context_txt;
        TextView comment_count_txt;
        TextView like_count_txt;
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

            comment_count_txt = itemView.findViewById(R.id.comment_count);
            like_count_txt = itemView.findViewById(R.id.like_count);

            profile_image = itemView.findViewById(R.id.profile_image);

            images_recycler_view = itemView.findViewById(R.id.images_recycler_view);
        }
    }
}