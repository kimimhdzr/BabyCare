package com.example.babycare.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.AdapterInt;
import com.example.babycare.MainActivity.Fragments.Home.BabyCardAdapter;
import com.example.babycare.MainActivity.Fragments.Home.Home;
import com.example.babycare.MainActivity.Fragments.Home.TipsAdapter;
import com.example.babycare.MainActivity.Fragments.Home.TipsCircle;
import com.example.babycare.MainActivity.Fragments.Profile.Fragments.ManageChildren.ManageChildren;
import com.example.babycare.MainActivity.Fragments.Profile.MainProfile;
import com.example.babycare.Objects.Baby;
import com.example.babycare.Objects.Tip;
import com.example.babycare.Objects.User;
import com.example.babycare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.bundle.BundledQueryOrBuilder;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TipsCircle tipsCircle;


    private String UID;
    private SharedUserModel sharedUserModel;
    public User session_user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    //MyDatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedUserModel = new ViewModelProvider(this).get(SharedUserModel.class);



        Bundle bundle = getIntent().getExtras();
        UID = bundle.getString("session_id");
        sharedUserModel.fetchUserData(UID);
        // Initialize profileManager (example, adjust as needed)

        bottomNavigationView = findViewById(R.id.bottomNavView);




        // Set up NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        NavController navController = navHostFragment.getNavController();

        Bundle bundle2 = new Bundle();
        bundle2.putString("UID", UID);

        navController.setGraph(R.navigation.main_navigation, bundle2);

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);




    }


    public void fetchSessionUser(SharedUserModel sharedUserModel){
        db= FirebaseFirestore.getInstance();
        db.collection("users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        String usename = doc.getString("username");
                        String type = doc.getString("type");

                        User user = new User();
                        user.setStatus(type);
                        user.setUsername(usename);

                        updateUIWithUser(user,sharedUserModel);

                    }
                }else {

                }
            }
        });


    }

    public void fetchSessionChildren(User user,SharedUserModel sharedUserModel){
        db.collection("babies")
                .whereEqualTo("parent", session_user.getUsername()) // Filter by parent's name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Baby> fetchChildren = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot) {
                                // Retrieve baby data here
                                String babyName = document.getString("name");
                                String parentName = document.getString("parent");
                                String bloodType = document.getString("bloodtype");
                                String birthDate = document.getString("birthday");
                                String height = document.getString("height");
                                String weight = document.getString("weight");
                                ArrayList<String> allergies = (ArrayList<String>) document.get("allergies");

                                Baby baby = new Baby(parentName, babyName, bloodType, birthDate,height,weight);
                                baby.setAllergies(allergies);
                                fetchChildren.add(baby);


                           }

                            user.setChildren(fetchChildren);

                            sharedUserModel.setSharedData(user);
                            updateUIWithUser2(user);
                        } else {
                            Log.d("Firestore", "No babies found for the parent.");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching babies", task.getException());
                    }
                });
    }

    private void updateUIWithUser2(User user) {
        RecyclerView child_list = findViewById(R.id.MyChildrenRecycler);
        BabyCardAdapter babyCardAdapter = new BabyCardAdapter(user.getChildren());
        child_list.setAdapter(babyCardAdapter);
        child_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if(sharedUserModel == null){
            sharedUserModel = new ViewModelProvider(this).get(SharedUserModel.class);
            sharedUserModel.setSharedData(user);
        }
    }

    private void updateUIWithUser(User user, SharedUserModel sharedUserModel) {

        session_user = new User();
        session_user.setUsername(user.getUsername());
        session_user.setStatus(user.getStatus());
        TextView sessionusername = findViewById(R.id.sessionusername);
        sessionusername.setText(user.getUsername());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        NavController navController = navHostFragment.getNavController();

        Bundle bundle = new Bundle();
        bundle.putSerializable("session_user", session_user);

        if(sharedUserModel == null){
            sharedUserModel = new ViewModelProvider(this).get(SharedUserModel.class);
            sharedUserModel.setSharedData(user);
        }

        fetchSessionChildren(user,sharedUserModel);


    }




/*
    private void fetchLocalOrRemoteData(MyDatabaseHelper dbHelper) {
        // Call readAllDataCurrentUser from DatabaseHelper
        //Cursor cursor = dbHelper.readAllDataCurrentUser();

        if (cursor != null && cursor.moveToFirst()) {
            // Table is not empty, read data from SQLite
            Log.d(TAG, "Data exists in SQLite table. Fetching locally...");
            do {
                String documentID = cursor.getString(cursor.getColumnIndexOrThrow("documentID"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                // Process the data as needed
                Log.d(TAG, "DocumentID: " + documentID + ", Username: " + username);
            } while (cursor.moveToNext());

            cursor.close(); // Close the cursor (avoid memory leaks)
        } else {
            // Table is empty, fetch data from Firestore
            Log.d(TAG, "SQLite table is empty. Fetching from Firestore...");
            fetchUserData();
        }
    }

    private void fetchUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    String username = documentSnapshot.getString("username");
                                    String profilePic = documentSnapshot.getString("profilePic");
                                    String phoneNumber = documentSnapshot.getString("phoneNumber");
                                    String email = documentSnapshot.getString("email");
                                    String type = documentSnapshot.getString("type");

                                    Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");
                                    String createdAtformattedTime = formatTimestamp(createdAt.toDate());

                                    // Add a Current User in SQL
                                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this, "CurrentUser.db");
                                    dbHelper.deleteAllData();
                                    dbHelper.addCurrentUser(
                                            uid,
                                            username,
                                            profilePic,
                                            phoneNumber,
                                            email,
                                            createdAtformattedTime,
                                            type
                                    );
                                    Log.d(TAG, "Into SQL");

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                                Log.e(TAG, "state: " + "success");
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }
    }

    private String formatTimestamp(Date timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timestamp);

    }*/
}