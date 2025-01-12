package com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.babycare.MainActivity.SharedUserModel;
import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.User;
import com.example.babycare.R;

import java.util.ArrayList;

public class ManageChildren extends Fragment {
    ArrayList<Baby> babies;
    User session_user;

    SharedUserModel sharedUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedUserModel = new ViewModelProvider(requireActivity()).get(SharedUserModel.class);
        session_user = (User) sharedUserModel.getSharedData().getValue();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_children, container, false);

        Button addChildBtn= view.findViewById(R.id.add_baby);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("session_user",session_user);
                Navigation.findNavController(view).navigate(R.id.nav_to_AddChildren,bundle);
            }
        });

        // Inflate the layout for this fragment


        RecyclerView children_list = view.findViewById(R.id.child_list);
        ChildrenAdapter childrenAdapter = new ChildrenAdapter(session_user.getChildren(),getContext());

        children_list.setAdapter(childrenAdapter);
        children_list.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }
}