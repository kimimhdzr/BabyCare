package com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.babycare.DataBinding.Model.BabyProfileModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Profile.Adapter.ChildrenAdapter;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ManageChildren extends Fragment {

    ArrayList<BabyProfileModel> babies;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MyDatabaseHelper dbHelper;
    FirebaseUser currentUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_children, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");


        Button addChildBtn= view.findViewById(R.id.add_baby);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_to_AddChildren);
            }
        });

        // Inflate the layout for this fragment

        RecyclerView children_list = view.findViewById(R.id.child_list);
        ChildrenAdapter childrenAdapter = new ChildrenAdapter(
                dbHelper.getBabyProfilesByParentID(uid),
                getContext()
        );

        children_list.setAdapter(childrenAdapter);
        children_list.setLayoutManager(new LinearLayoutManager(getContext()));



        return view;
    }
}