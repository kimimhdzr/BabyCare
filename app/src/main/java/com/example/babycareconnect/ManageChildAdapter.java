package com.example.babycareconnect;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

public class ManageChildAdapter extends RecyclerView.Adapter<ManageChildViewHolder> {

    Context context;
    List<childItem> childItems;

    public ManageChildAdapter(Context context, List<childItem> childItems) {
        this.context = context;
        this.childItems = childItems;
    }

    @NonNull
    @Override
    public ManageChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageChildViewHolder(LayoutInflater.from(context).inflate(R.layout.child_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageChildViewHolder holder, int position) {
        holder.babyNameView.setText(childItems.get(position).getBabyName());

        String profileImageUri = childItems.get(position).babyProfilePictureUri;
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            holder.babyProfileView.setImageURI(Uri.parse(profileImageUri));
        }

        holder.editBtn.setOnClickListener(v->{
            Bundle bundle=new Bundle();
            bundle.putString("username",childItems.get(position).getBabyName());
            bundle.putString("profile_image",childItems.get(position).getBabyProfilePictureUri());
            bundle.putBoolean("isEditing",true);
            Navigation.findNavController(v).navigate(R.id.addBabyDest,bundle);
        });

        holder.deleteBtn.setOnClickListener(v->{
            childItems.remove(position);
            notifyItemRemoved(position);
            deleteChildFromJsonFile(childItems.get(position).getBabyName());
            Toast.makeText(context,"Child deleted sucessfully", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return childItems.size();
    }

    private void deleteChildFromJsonFile(String username){
        try{
            File file= new File(context.getFilesDir(),"children_details.json");
            if(file.exists()) {
                JSONArray jsonArray;
                String json = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject childObject = jsonArray.getJSONObject(i);
                    if (childObject.getString("username").equals(username)) {
                        jsonArray.remove(i);
                        break;
                    }
                }

                FileWriter writer = new FileWriter(file);
                writer.write(jsonArray.toString());
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageButton editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.babyNameTV);
            editBtn=itemView.findViewById(R.id.editBtn);
            deleteBtn=itemView.findViewById(R.id.deleteBtn);
        }
    }
}
