package com.example.babycare.MainActivity.Fragments.Home;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Model.TipModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.AdapterInt;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.BabyCardAdapter;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.TipsAdapter;
import com.example.babycare.MainActivity.Fragments.Home.Fragments.BabyHome.Adapter.TipsCircle;
import com.example.babycare.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class Home extends Fragment implements AdapterInt {
    private TipsCircle tipsCircle;
    private RecyclerView MyChildrenRecycler;

    ShapeableImageView profileImage;
    TextView usernametoolbar, usernameheader, total_kids;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MyDatabaseHelper dbHelper;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        usernametoolbar = view.findViewById(R.id.sessionusername);
        usernameheader = view.findViewById(R.id.usernameHeader);
        total_kids = view.findViewById(R.id.total_kids);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        Cursor cursor = dbHelper.getCurrentUserByDocumentID(uid);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            usernametoolbar.setText(username);
            usernameheader.setText(username);

            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            total_kids.setText(email);

            String profilePicURL = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));
            Glide.with(getContext())
                    .load(profilePicURL) // Replace with your drawable resource
                    .into(profileImage);
        }



        MyChildrenRecycler = view.findViewById(R.id.MyChildrenRecycler);
        BabyCardAdapter babyCardAdapter = new BabyCardAdapter(
                getContext(),
                dbHelper.getBabyProfilesByParentID(uid)
        );
        MyChildrenRecycler.setAdapter(babyCardAdapter);
        MyChildrenRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));



        ViewPager2 tips_cards = view.findViewById(R.id.tipsViewPager);
        RecyclerView circles = view.findViewById(R.id.circle_recycler);

        TipModel tip1 = new TipModel("START YOUR DAY","start your day with a good night sleep and a good morning routine and a healthy breakfast");
        TipModel tip2 = new TipModel("Healthy Nutrient","A healthy nutrient is a very vital aspect in ensuring your child's growth");

        ArrayList<TipModel> tips = new ArrayList<>();
        tips.add(tip1);
        tips.add(tip2);

        TipsAdapter tipsAdapter = new TipsAdapter(tips, this);
        tipsCircle = new TipsCircle(tips);

        tips_cards.setAdapter(tipsAdapter);
        circles.setAdapter(tipsCircle);

        circles.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        tips_cards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the second adapter's selected position
                tipsCircle.updateSelectedPosition(position);
            }
        });


        RelativeLayout toCalendar = view.findViewById(R.id.toCalendarPage);
        toCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_to_Calendar);
            }
        });





        return view;
    }
    @Override
    public void onItemSelected(int position) {
        // Update the second adapter's selected position when an item is selected in the first adapter
        tipsCircle.updateSelectedPosition(position);
    }
}