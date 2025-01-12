package com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Fragments.EditAllergyAdapter;
import com.example.babycare.Objects.Baby;
import com.example.babycare.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenAdapter.MyViewHolder> {

    private ArrayList<Baby> children;
    private Context context;

    public ChildrenAdapter(ArrayList<Baby> children, Context context) {
        this.children = children;
        this.context = context;
    }

    // ViewHolder Class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton edit,delete;
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

        holder.edit.setOnClickListener(v->{
            Bundle bundle=new Bundle();
            bundle.putSerializable("session_baby",children.get(position));
            Navigation.findNavController(v).navigate(R.id.Nav_to_EditBabyProfile2,bundle);
        });

        holder.delete.setOnClickListener(v->{
            if(children.size()>0){
                Baby childToDelete = children.get(position);
                children.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,children.size());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Log.d("BABY","RF"+childToDelete.getName());
                Log.d("BABY","RF"+childToDelete.getParent());
                Log.d("BABY","RF"+childToDelete.getBirthday());

                   // Get the document ID to delete

                // Delete the document from Firestore
                db.collection("babies")
                        .whereEqualTo("name", childToDelete.getName())  // Match name field
                        .whereEqualTo("birthday", childToDelete.getBirthday())
                        .whereEqualTo("parent",childToDelete.getParent())// Match birthday field
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Delete the matched document(s)
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete()
                                            .addOnSuccessListener(aVoid -> {
                                                // Successfully deleted from Firestore
                                                Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure
                                                Log.e("Firestore", "Error deleting document", e);
                                            });
                                }
                            } else {
                                Log.d("Firestore", "No matching documents found to delete.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error querying documents", e);
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return children.size();
    }


}
