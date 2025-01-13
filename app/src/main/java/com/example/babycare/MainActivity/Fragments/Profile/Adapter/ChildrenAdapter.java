package com.example.babycare.MainActivity.Fragments.Profile.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.R;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.MyViewHolder> {

    private ArrayList<BabyProfileModel> children;
    private Context context;

    MyDatabaseHelper dbHelper;

    public ChildrenAdapter(ArrayList<BabyProfileModel> children, Context context) {
        this.children = children;
        this.context = context;
        dbHelper = new MyDatabaseHelper(context, "CurrentUser.db");
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton edit, delete;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.editBaby);
            delete = itemView.findViewById(R.id.deleteChild);
            name = itemView.findViewById(R.id.baby_name);
        }
    }

    @NonNull
    @Override
    public ChildrenAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_children_manage_children, parent, false);
        return new ChildrenAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildrenAdapter.MyViewHolder holder, int position) {
        holder.name.setText(children.get(position).getName());

        holder.edit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session_baby", children.get(position));
            Navigation.findNavController(v).navigate(R.id.nav_to_EditBabyProfile, bundle);
        });

        holder.delete.setOnClickListener(v -> {
            if (children.size() > 0) {
                BabyProfileModel childToDelete = children.get(position);
                children.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, children.size());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Log.d("BABY", "RF" + childToDelete.getName());
                Log.d("BABY", "RF" + childToDelete.getParent());
                Log.d("BABY", "RF" + childToDelete.getDob());

                // Get the document ID to delete

                // Delete the document from Firestore
                db.collection("BabyProfile")
                        .document(children.get(position).getDocumentID()) // Match by document ID
                        .delete() // Directly delete the document by its ID
                        .addOnSuccessListener(aVoid -> {
                            // Successfully deleted from Firestore
                            dbHelper.deleteBabyProfile(children.get(position).getDocumentID());
                            Log.d("Firestore", "DocumentSnapshot successfully deleted!");

                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Firestore", "Error deleting document", e);
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return children.size();
    }


}
