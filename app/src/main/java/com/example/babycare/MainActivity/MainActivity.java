package com.example.babycare.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    MyDatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize profileManager (example, adjust as needed)

        bottomNavigationView = findViewById(R.id.bottomNavView);

        // Set up NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        NavController navController = navHostFragment.getNavController();

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        dbHelper = new MyDatabaseHelper(MainActivity.this, "CurrentUser.db");
        // Check data locally or fetch from Firestore
        fetchLocalOrRemoteData(dbHelper);
    }


    private void fetchLocalOrRemoteData(MyDatabaseHelper dbHelper) {
        // Call readAllDataCurrentUser from DatabaseHelper
        Cursor cursor = dbHelper.readAllDataCurrentUser();

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

            String acctype = "";
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
                                    fetchBabyProfile(type);

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

    private void fetchBabyProfile(String type) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            String uid = currentUser.getUid();

            String field = "";
            if ("Father".equals(type)) {
                field = "parent.fatherId";
            } else if ("Mother".equals(type)) {
                field = "parent.motherId";
            } else if ("Guardian".equals(type)) {
                field = "parent.guardianId";
            }

            final String field2 = field;

            Query query;
            query = db.collection("BabyProfile")
                    .whereEqualTo(field2, uid);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        String documentID = documentSnapshot.getId();
                                        String name = documentSnapshot.getString("name");
                                        Timestamp dob = documentSnapshot.getTimestamp("dob");
                                        String dobFormattedTime = formatTimestamp(dob.toDate());
                                        String height = documentSnapshot.getString("height");
                                        String weight = documentSnapshot.getString("weight");
                                        String bloodType = documentSnapshot.getString("bloodType");

                                        String parent_fatherId = documentSnapshot.getString("parent.fatherId");
                                        String parent_motherId = documentSnapshot.getString("parent.motherId");
                                        String parent_guardianId = documentSnapshot.getString("parent.guardianId");

                                        List<String> allergies = (List<String>)  documentSnapshot.get("allergies");
                                        String profilePic = documentSnapshot.getString("profilePic");


                                        // Add a Current User in SQL
                                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this, "CurrentUser.db");
                                        dbHelper.addBabyProfile(
                                                documentID,
                                                parent_fatherId,
                                                parent_motherId,
                                                parent_guardianId,
                                                name,
                                                dobFormattedTime,
                                                height,
                                                weight,
                                                bloodType,
                                                formatAllergies(allergies),
                                                profilePic
                                        );
                                        Log.d(TAG, "Into SQL"+ name + profilePic);
                                    }

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

    }
    public String formatAllergies(List<String> allergiesList) {
        String allergies ="";
        if (allergiesList != null && !allergiesList.isEmpty()) {
            // Convert the ArrayList into a comma-separated string
            allergies = String.join(", ", allergiesList);
        } else {
            // Set to null or empty string if the list is null or empty
            allergies = "";
        }
        return allergies;
    }
}