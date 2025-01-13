package com.example.babycare.MainActivity.Fragments.Profile.Fragments.Setting.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.babycare.MainActivity.Fragments.Profile.Services.SupportWriter;
import com.example.babycare.MainActivity.Fragments.Profile.Services.SupportWriterCallback;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Support extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private ImageButton close_icon, images_add_icon, send_icon;
    private EditText body_edit_txt, problem_edit_text;
    private Spinner items_spinner;
    ChipGroup recipient_chip_group;
    ProgressBar progress_circle;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 200;
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private List<Uri> imageSelectUriList = new ArrayList<>();
    private List<String> imageUploadUrlsList = new ArrayList<>();
    List<String> supportList;
    String supportselected;
    SupportWriter supportWriter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        close_icon = view.findViewById(R.id.close_icon);
        images_add_icon = view.findViewById(R.id.images_add_icon);
        send_icon = view.findViewById(R.id.send_icon);

        fab = view.findViewById(R.id.fab);

        items_spinner = view.findViewById(R.id.items_spinner);
        recipient_chip_group = view.findViewById(R.id.recipient_chip_group);

        problem_edit_text = view.findViewById(R.id.problem_edit_text);
        body_edit_txt = view.findViewById(R.id.body_edit_txt);

        progress_circle = view.findViewById(R.id.progress_circle);

        supportWriter = new SupportWriter(getContext());

        supportList = new ArrayList<>(Arrays.asList(
                "Bug Report",          // Option 1
                "Feature Request",     // Option 2
                "Account Issue",       // Option 3
                "Other"               // Option 4
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                supportList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        items_spinner.setAdapter(adapter);

        // Set up the listener for when an item is selected
        items_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSupport = parentView.getItemAtPosition(position).toString();
                if (!selectedSupport.isEmpty()) {
                    supportselected = selectedSupport;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

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


        send_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = problem_edit_text.getText().toString().trim();
                String context = body_edit_txt.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    problem_edit_text.setError("Please enter problem title");
                    return;
                }
                if (TextUtils.isEmpty(context)) {
                    body_edit_txt.setError("Please include context");
                    return;
                }

                send_icon.setVisibility(View.GONE);      // Hide the send icon
                progress_circle.setVisibility(View.VISIBLE); // Show the progress circle
                send_icon.setClickable(false);           // Disable the button
                uploadImages(title, context);

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });


        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });



        bottomNavigationView = ((MainActivity)getActivity()).findViewById(R.id.bottomNavView);
        if(bottomNavigationView != null){
            bottomNavigationView.setVisibility(View.GONE);
        }

        return view;
    }


    private void uploadImages(String title, String description) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String uid = currentUser.getUid();

        if (imageSelectUriList == null || imageSelectUriList.isEmpty()) {
            writeSupport(title, description);
            return;
        }
        // Counter to keep track of completed uploads
        int totalImages = imageSelectUriList.size();
        AtomicInteger uploadCount = new AtomicInteger(0); // Using AtomicInteger to handle the count safely

        for (int i = 0; i < totalImages; i++) {
            Uri file = imageSelectUriList.get(i);
            StorageReference imageRef = storageRef.child("images/supports/" + uid + "/" + file.getLastPathSegment());

            imageRef.putFile(file).addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUploadUrlsList.add(uri.toString());

                    // Increment the upload count
                    if (uploadCount.incrementAndGet() == totalImages) {
                        // All images uploaded, now write the report
                        writeSupport(title, description);
                    }
                }).addOnFailureListener(e -> {
                    // Handle failure to get the download URL
                    Log.e("ImageUpload", "Failed to get download URL: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                // Handle failed uploads
                Log.e("ImageUpload", "Failed to upload image: " + e.getMessage());
            });
        }
    }

    private void writeSupport(String title, String description) {

        String uid = currentUser.getUid();

        supportWriter.writeSupport(
                getContext(), uid, title, description, supportselected,
                imageUploadUrlsList,
                new SupportWriterCallback() {
                    @Override
                    public void onSuccess() {

                        // Report successfully sent, revert the UI back to the send icon
                        send_icon.setVisibility(View.VISIBLE);   // Show the send icon
                        progress_circle.setVisibility(View.GONE); // Hide the progress circle
                        send_icon.setClickable(true);            // Re-enable the button
                        // Reset UI elements on success
                        imageSelectUriList.clear();
                        imageUploadUrlsList.clear();
                        recipient_chip_group.removeAllViews();
                        problem_edit_text.setText("");
                        body_edit_txt.setText("");
                        body_edit_txt.setText("");
                        // Show a success message
                        Snackbar.make(getView(), "Report successfully sent!", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Failed to send report, revert the UI back to the send icon
                        send_icon.setVisibility(View.VISIBLE);   // Show the send icon
                        progress_circle.setVisibility(View.GONE); // Hide the progress circle
                        send_icon.setClickable(true);            // Re-enable the button

                        // Handle failure (optional)
                        Snackbar.make(getView(), "Failed to send report: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
        );
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Enable multiple image selection
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
                        imageSelectUriList.add(imageUri);
                        addChipForImage(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    imageSelectUriList.add(imageUri);
                    addChipForImage(imageUri);
                }
            }
        }
    }

    private void addChipForImage(Uri imageUri) {
        Chip chip = new Chip(getContext());
        chip.setText(imageUri.getLastPathSegment());  // You can customize this text
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipient_chip_group.removeView(chip);  // Remove the chip on close icon click
            }
        });
        recipient_chip_group.addView(chip);  // Add the chip to the ChipGroup
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(bottomNavigationView!=null){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}