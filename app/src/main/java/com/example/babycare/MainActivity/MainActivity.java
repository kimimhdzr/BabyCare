package com.example.babycare.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Home.AdapterInt;
import com.example.babycare.MainActivity.Fragments.Home.TipsAdapter;
import com.example.babycare.MainActivity.Fragments.Home.TipsCircle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterInt {
    private static final String TAG = "MainActivity";

    private TipsCircle tipsCircle;

    private String UID;
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

        Bundle bundle = getIntent().getExtras();
        UID = bundle.getString("session_id");
        fetchSessionUser();
        // Initialize profileManager (example, adjust as needed)

        bottomNavigationView = findViewById(R.id.bottomNavView);

        // Set up NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        NavController navController = navHostFragment.getNavController();

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);





        //dbHelper = new MyDatabaseHelper(MainActivity.this, "CurrentUser.db");
        // Check data locally or fetch from Firestore
        //fetchLocalOrRemoteData(dbHelper);
    }


    public void fetchSessionUser(){

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e !=null)
                {

                }

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges())
                {
                    String   userID = String.valueOf(documentChange.getDocument().getId());
                    if(userID.equals(UID)){
                        User test = new User();

                        test.setUsername(documentChange.getDocument().getData().get("username").toString());
                        test.setStatus(documentChange.getDocument().getData().get("type").toString());

                        updateUIWithUser(test);
                        return;
                    }
                }
            }
        });


    }

    private void updateUIWithUser(User user) {
        TextView usernameTextView = findViewById(R.id.sessionusername);
        session_user = new User();
        session_user.setUsername(user.getUsername());
        session_user.setStatus(user.getStatus());

        usernameTextView.setText(session_user.getUsername());


        ViewPager2 tips_cards = findViewById(R.id.tipsViewPager);
        RecyclerView circles = findViewById(R.id.circle_recycler);

        Tip tip1 = new Tip("START YOUR DAY","start your day with a good night sleep and a good morning routine and a healthy breakfast");
        Tip tip2 = new Tip("Healthy Nutrient","A healthy nutrient is a very vital aspect in ensuring your child's growth");

        ArrayList<Tip> tips = new ArrayList<>();
        tips.add(tip1);
        tips.add(tip2);

        TipsAdapter tipsAdapter = new TipsAdapter(tips, this);
        tipsCircle = new TipsCircle(tips);

        tips_cards.setAdapter(tipsAdapter);
        circles.setAdapter(tipsCircle);

        circles.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        tips_cards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the second adapter's selected position
                tipsCircle.updateSelectedPosition(position);
            }
        });

    }

    @Override
    public void onItemSelected(int position) {
        // Update the second adapter's selected position when an item is selected in the first adapter
        tipsCircle.updateSelectedPosition(position);
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