package com.example.babycare.MainActivity.Fragments.Community.Forum;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.babycare.DataBinding.Cache.PostCache;
import com.example.babycare.DataBinding.Model.CommentModel;
import com.example.babycare.DataBinding.Model.PostModel;
import com.example.babycare.DataBinding.SQLite.MyDatabaseHelper;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.CommentAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Adapter.ImagesGridAdapter;
import com.example.babycare.MainActivity.Fragments.Community.Services.NewComment;
import com.example.babycare.MainActivity.Fragments.Community.Services.NewPosts;
import com.example.babycare.MainActivity.Fragments.Community.Services.PostCreationCallback;
import com.example.babycare.R;
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

public class Comment extends Fragment {

    PostModel postModel;
    ImagesGridAdapter imagesGridAdapter;


    //parent post
    TextView sender_name;
    TextView sender_email;
    TextView sender_context;
    TextView comment_count_txt;
    TextView like_count_txt;
    TextView sender_postDate;
    ShapeableImageView sender_profile_image;
    RecyclerView sender_images_recycler_view;


    //send comment

    CommentAdapter commentAdapter;
    FirebaseFirestore db;
    ShapeableImageView commment_profile_image;
    Button post_button;
    EditText commment_context_edit_txt;
    RecyclerView comment_recycler_view, images_recycler_view;
    NewComment newComment;
    List<String> selectimageUrls;
    LinearProgressIndicator linearProgressIndicator;
    ImageButton images_add_icon;
    private List<CommentModel> commentsList;
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
        View view = inflater.inflate(R.layout.fragment_comment, container, false);


        //the parent post

        Bundle bundle = getArguments();
        postModel = (PostModel) bundle.getSerializable("post");


        sender_name = view.findViewById(R.id.sender_name);
        sender_email = view.findViewById(R.id.sender_email);
        sender_context = view.findViewById(R.id.sender_context);
        sender_postDate = view.findViewById(R.id.sender_postDate);
        comment_count_txt = view.findViewById(R.id.comment_count);
        like_count_txt = view.findViewById(R.id.like_count);
        sender_profile_image = view.findViewById(R.id.sender_profile_image);
        sender_images_recycler_view = view.findViewById(R.id.sender_images_recycler_view);


        sender_name.setText(postModel.getUserName());
        sender_email.setText("");
        sender_context.setText(postModel.getDescription());
        sender_postDate.setText(postModel.getTimestamp());

        comment_count_txt.setText("1");
        like_count_txt.setText("1");

        if (postModel.getProfilePic() != null) {
            Glide.with(getContext())
                    .load(postModel.getProfilePic())
                    .into(sender_profile_image); // Replace with your ImageView reference
        }

        List<String> postImages = postModel.getAttachments();  // Get images for the current post
        imagesGridAdapter = new ImagesGridAdapter(
                getContext(),
                postImages,
                1
        );

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(),
                2
        );
        sender_images_recycler_view.setLayoutManager(gridLayoutManager);
        sender_images_recycler_view.setAdapter(imagesGridAdapter);


        //comment part
        commment_profile_image = view.findViewById(R.id.profile_image);
        commment_context_edit_txt = view.findViewById(R.id.context_edit_txt);
        images_recycler_view = view.findViewById(R.id.images_recycler_view);
        post_button = view.findViewById(R.id.post_button);
        comment_count_txt = view.findViewById(R.id.comment_count);
        linearProgressIndicator = view.findViewById(R.id.linearprogressindicator);
        comment_recycler_view = view.findViewById(R.id.comment_recycler_view);


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
        commentsList = new ArrayList<>();
        comment_recycler_view = view.findViewById(R.id.comment_recycler_view);
        commentAdapter = new CommentAdapter(
                getContext(),
                commentsList
        );
        comment_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        comment_recycler_view.setItemAnimator(new DefaultItemAnimator());
        comment_recycler_view.setAdapter(commentAdapter);

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
        commment_profile_image = view.findViewById(R.id.profile_image);
        if (currentProfilePicURL != null) {
            Glide.with(this)
                    .load(currentProfilePicURL) // Replace with your drawable resource
                    .into(commment_profile_image);
        }


        fetchComments(postModel.getDocumentID());


        //post button logic
        post_button = view.findViewById(R.id.post_button);
        commment_context_edit_txt = view.findViewById(R.id.context_edit_txt);
        linearProgressIndicator = view.findViewById(R.id.linearprogressindicator);
        newComment = new NewComment();
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = commment_context_edit_txt.getText().toString();
                if (TextUtils.isEmpty(description)) {
                    commment_context_edit_txt.setError("It seems you forgot to write something");
                    return;
                }

                post_button.setEnabled(false);
                post_button.setAlpha(0.5f);
                linearProgressIndicator.setVisibility(View.VISIBLE);
                newComment.createComment(
                        getContext(),
                        postModel.getDocumentID(),
                        description,
                        selectimageUrls,
                        new PostCreationCallback() {
                            @Override
                            public void onPostCreated(boolean isSuccess, List<String> uploadimageUrls, String documentID) {
                                post_button.setEnabled(true);
                                post_button.setAlpha(1.0f);
                                linearProgressIndicator.setVisibility(View.INVISIBLE);
                                commment_context_edit_txt.setText("");
                                imagesGridAdapter.clearData();
                                selectimageUrls.clear();
                                if (isSuccess) {
                                    attachNewComment(documentID, description, uploadimageUrls, uid, final_currentProfilePicURL, final_currentUsername);
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


    public void attachNewComment(
            String documentID,
            String description,
            List<String> uploadimageUrls,
            String userId,
            String profilePic,
            String userName
    ) {
        String formattedtime = formatTimestamp(new Date());
        CommentModel newComment = new CommentModel(
                documentID,
                description,
                uploadimageUrls,

                userId,
                profilePic,
                userName,

                formattedtime
        );
        commentAdapter.appendComment(newComment);
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

    public void fetchComments(String forumId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the posts collection
        db.collection("Forums")
                .document(forumId) // Replace forumId with the actual forum ID
                .collection("Comments") // Access the Comments subcollection
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp descending
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CommentModel> commentsList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommentModel comment = new CommentModel();
                            // Extract post data

                            comment.setDocumentID(document.getId());
                            comment.setDescription(document.getString("description"));
                            List<String> images = (List<String>) document.get("attachments");
                            comment.setAttachments(images != null ? images : new ArrayList<>());


                            comment.setUserId(document.getString("sender.userId"));
                            comment.setProfilePic(document.getString("sender.profilePic"));
                            comment.setUserName(document.getString("sender.userName"));

                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String formattedTime = formatTimestamp(timestamp.toDate());
                            comment.setTimestamp(formattedTime);

                            commentsList.add(comment);
                            Log.e("comment", document.getString("description"));

                        }
                        commentAdapter.setComment(commentsList); // Assuming your adapter has a setPosts method
                        Log.e("comment", "state: " + "success");

                    } else {
                        // Handle errors
                        Log.e("Fetchcomment", "Error getting documents: ", task.getException());
                        Log.e("comment", "state: " + "fail");
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