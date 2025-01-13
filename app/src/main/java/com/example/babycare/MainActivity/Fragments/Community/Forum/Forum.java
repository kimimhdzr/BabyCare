package com.example.babycare.MainActivity.Fragments.Community.Forum;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Cache.PostCache;
import com.example.babycare.DataBinding.Model.PostModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.ImagesGridAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.PublicPostsAdapter;

import android.Manifest;

import com.example.babycare.MainActivity.Fragments.Community.Services.NewPosts;
import com.example.babycare.MainActivity.Fragments.Community.Services.PostCreationCallback;
import com.example.babycare.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Forum extends Fragment {

    RecyclerView post_recycler_view, images_recycler_view;
    PublicPostsAdapter publicPostsAdapter;
    FirebaseFirestore db;
    PostCache postCache;
    ShapeableImageView profile_image;
    Button post_button;
    EditText context_edit_txt;
    NewPosts newPosts;
    List<String> selectimageUrls;
    LinearProgressIndicator linearProgressIndicator;
    ImageButton images_add_icon;
    ImagesGridAdapter imagesGridAdapter;
    AppBarLayout writepostcontainer;
    MaterialToolbar toolbar;
    private List<PostModel> postsList;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    MyDatabaseHelper dbHelper;
    // Constants
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_PICK_IMAGE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        dbHelper = new MyDatabaseHelper(getContext(), "CurrentUser.db");

        Cursor cursor = dbHelper.getCurrentUserByDocumentID(uid);
        String currentUsername = "";
        String currentProfilePicURL = "";
        if (cursor != null && cursor.moveToFirst()) {
            currentUsername = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            currentProfilePicURL = cursor.getString(cursor.getColumnIndexOrThrow("profilePic"));
        }
        final String final_currentUsername = currentUsername;
        final String final_currentProfilePicURL = currentProfilePicURL;



        //setup posts adapter for retrieved posts
        postsList = new ArrayList<>();
        post_recycler_view = view.findViewById(R.id.post_recycler_view);
        publicPostsAdapter = new PublicPostsAdapter(
                getContext(),
                postsList
        );
        post_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        post_recycler_view.setItemAnimator(new DefaultItemAnimator());
        post_recycler_view.setAdapter(publicPostsAdapter);

        //setup images adapter for selected images
        selectimageUrls = new ArrayList<>();

        images_recycler_view = view.findViewById(R.id.images_recycler_view);// Initialize the adapter and set it to the RecyclerView
        imagesGridAdapter = new ImagesGridAdapter(
                getContext(),
                selectimageUrls,
                2
        );
        images_recycler_view.setLayoutManager(new GridLayoutManager(
                getContext(),
                2)
        ); // 3 columns in the grid
        images_recycler_view.setAdapter(imagesGridAdapter);

        //attach the profile pic
        profile_image = view.findViewById(R.id.profile_image);
        if (currentProfilePicURL != null) {
            Glide.with(this)
                    .load(currentProfilePicURL) // Replace with your drawable resource
                    .into(profile_image);
        }


        //collapse toolbar when scrolling animation
        writepostcontainer = view.findViewById(R.id.writepostcontainer);
        toolbar = view.findViewById(R.id.toolbar);
        writepostcontainer.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Animate the toolbar based on scroll offset
                float percentage = Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange());

                // Example of changing alpha or scaling based on scroll
                appBarLayout.setAlpha(1 - percentage);
                appBarLayout.setScaleX(1 - 0.5f * percentage);
                appBarLayout.setScaleY(1 - 0.5f * percentage);
            }
        });

        // Fetch chats from Firestore
        postCache = PostCache.getInstance();  // Get the instance of PostCa
        if (postCache.isEmpty()) {
            Log.e("FetchPosts", "fetch new");
            fetchPosts();
        } else {
            Log.e("FetchPosts", "fetch cache");
            publicPostsAdapter.setPosts(postCache.getPostsList()); // Use cached posts3
        }

        //post button logic
        post_button = view.findViewById(R.id.post_button);
        context_edit_txt = view.findViewById(R.id.context_edit_txt);
        linearProgressIndicator = view.findViewById(R.id.linearprogressindicator);
        newPosts = new NewPosts();
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = context_edit_txt.getText().toString();
                if (TextUtils.isEmpty(description)) {
                    context_edit_txt.setError("It seems you forgot to write something");
                    return;
                }

                post_button.setEnabled(false);
                post_button.setAlpha(0.5f);
                linearProgressIndicator.setVisibility(View.VISIBLE);
                newPosts.createPost(
                        getContext(),
                        description,
                        selectimageUrls,
                        new PostCreationCallback() {
                            @Override
                            public void onPostCreated(boolean isSuccess, List<String> uploadimageUrls, String documentID) {
                                post_button.setEnabled(true);
                                post_button.setAlpha(1.0f);
                                linearProgressIndicator.setVisibility(View.INVISIBLE);
                                context_edit_txt.setText("");
                                imagesGridAdapter.clearData();
                                selectimageUrls.clear();
                                if (isSuccess) {
                                    attachNewPost(documentID, description, uploadimageUrls, uid, final_currentProfilePicURL, final_currentUsername);
                                    Log.e("Posts", "Post created successfully!");
                                } else {
                                    Log.e("Posts", "Failed to create post.");
                                }
                            }

                            @Override
                            public void onImagesUploaded(boolean isSuccess) {
                                if (isSuccess) {
                                    Log.e("Images", "Images uploaded successfully!");
                                } else {
                                    post_button.setEnabled(true);
                                    post_button.setAlpha(1.0f);
                                    linearProgressIndicator.setVisibility(View.INVISIBLE);
                                    Log.e("Images", "Failed to upload pictures.");
                                }
                            }
                        }
                );
            }
        });


        //pick images logic
        images_add_icon = view.findViewById(R.id.images_add_icon);
        images_add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    openImagePicker();
                }
            }
        });

        return view;
    }

    public void attachNewPost(
            String documentID,
            String description,
            List<String> uploadimageUrls,
            String userId,
            String profilePic,
            String userName
    ) {
        String formattedtime = formatTimestamp(new Date());
        PostModel newpost = new PostModel(
                documentID,
                description,
                uploadimageUrls,

                userId,
                profilePic,
                userName,

                formattedtime
        );
        postCache.addPost(newpost);
        publicPostsAdapter.setPosts(postCache.getPostsList());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Enable multiple image selection
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                // Handle multiple images
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectimageUrls.add(imageUri.toString()); // Store URI as String
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    selectimageUrls.add(imageUri.toString()); // Store URI as String
                }

                // Notify your adapter that the data has changed
                imagesGridAdapter.notifyDataSetChanged();
            }
        }
    }

    public void fetchPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the posts collection
        db.collection("Forums")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp descending
                .limit(10) // Limit to 10 posts
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PostModel> postsList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostModel post = new PostModel();
                            // Extract post data

                            post.setDocumentID(document.getId());
                            post.setDescription(document.getString("description"));
                            List<String> images = (List<String>) document.get("attachments");
                            post.setAttachments(images != null ? images : new ArrayList<>());


                            post.setUserId(document.getString("sender.userId"));
                            post.setProfilePic(document.getString("sender.profilePic"));
                            post.setUserName(document.getString("sender.userName"));


                            Long commentCount = document.getLong("reactions.commentCounts");
                            Long likeCount = document.getLong("reactions.likeCounts");
//                            post.setCommentCount(commentCount != null ? commentCount.intValue() : 0);
//                            post.setLikeCount(likeCount != null ? likeCount.intValue() : 0);


                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String formattedTime = formatTimestamp(timestamp.toDate());
                            post.setTimestamp(formattedTime);

                            postsList.add(post);
                            Log.e("Posts", document.getString("description"));

                        }
                        PostCache.getInstance().setPostsList(postsList);
                        publicPostsAdapter.setPosts(postsList); // Assuming your adapter has a setPosts method
                        Log.e("Posts", "state: " + "success");

                    } else {
                        // Handle errors
                        Log.e("FetchPosts", "Error getting documents: ", task.getException());
                        Log.e("Posts", "state: " + "fail");
                    }
                });
    }

    private String formatTimestamp(Date timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timestamp);

    }
}