package com.example.babycareconnect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class add_baby_profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link add_baby_profile#newInstance} factory method to
     * create an instance of this fragment.
     */

    public add_baby_profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment add_baby_profile.
     */
    // TODO: Rename and change types and number of parameters
    public static add_baby_profile newInstance(String param1, String param2) {
        add_baby_profile fragment = new add_baby_profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private static final int PICK_IMAGE_REQUEST=1;

    private EditText username, dob, height, weight, bloodType;
    private List<allergyItem> allergiesList;
    private ShapeableImageView profileImageView;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        profileImageView.setImageURI(imageUri);
                    }
                }
            });
    private Button saveBtn;

    private String originalUsername=null;
    private boolean isEditing=false;

    private FirebaseFirestore addBabyProfileDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add_baby_profile, container, false);

        addBabyProfileDB=FirebaseFirestore.getInstance();
        username=view.findViewById(R.id.usernameTV);
        profileImageView=view.findViewById(R.id.babyPictureEdit);
        saveBtn=view.findViewById(R.id.saveBtn);

        Bundle bundle=getArguments();
        if(bundle!=null){
            isEditing=bundle.getBoolean("isEditing",false);
            if(isEditing){
                originalUsername=bundle.getString("username");
                String profileImageUri=bundle.getString("username");

                username.setText(originalUsername);

                if(profileImageUri!=null&&!profileImageUri.isEmpty()){
                    imageUri=Uri.parse(profileImageUri);
                    profileImageView.setImageURI(imageUri);
                }
            }
        }
        saveBtn.setOnClickListener(v->saveChildProfile());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username=view.findViewById(R.id.usernameTV);
        dob=view.findViewById(R.id.dobTV);
        height=view.findViewById(R.id.heightTV);
        weight=view.findViewById(R.id.weightTV);
        bloodType=view.findViewById(R.id.bloodTypeTV);
        profileImageView=view.findViewById(R.id.babyPictureEdit);

        RecyclerView recyclerView=view.findViewById(R.id.alergiesRV);

        allergiesList =new ArrayList<allergyItem>();
        allergiesList.add(new allergyItem());

        addAllergyAdapter adapter =new addAllergyAdapter(getContext(),allergiesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ImageButton editProfileBtn=view.findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        Button addMoreBtn =view.findViewById(R.id.addMoreBtn);
        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allergiesList.add(new allergyItem());
                recyclerView.getAdapter().notifyItemInserted(allergiesList.size()-1);
                recyclerView.smoothScrollToPosition(allergiesList.size()-1);
            }
        });

        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.manageChildDest);
            }
        });

        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChildProfile();
                Navigation.findNavController(view).navigate(R.id.manageChildDest);
            }
        });
    }

    private void openImageChooser(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent,"Select Picture"));

    }

    private void saveChildProfile() {

        Map<String,Object> babyProfile =new HashMap<>();
        babyProfile.put("username",username.getText().toString());
        babyProfile.put("date_of_birth",dob.getText().toString());
        babyProfile.put("height",height.getText().toString());
        babyProfile.put("weight",weight.getText().toString());
        babyProfile.put("blood_type",bloodType.getText().toString());

        if(imageUri!=null){
            babyProfile.put("profile_image",imageUri.toString());
        }

        String babyProfileDB="babyProfilesDB";

        if(isEditing){
            addBabyProfileDB.collection(babyProfileDB).whereEqualTo("username",originalUsername).get().addOnSuccessListener(queryDocumentSnapshots -> {
               for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                   addBabyProfileDB.collection(babyProfileDB).document(document.getId()).set(babyProfile).addOnSuccessListener(unused -> {
                       Toast.makeText(getContext(),"Profile updated successfully",Toast.LENGTH_SHORT).show();
                       requireActivity().getOnBackPressedDispatcher().onBackPressed();
                   }).addOnFailureListener(e->{
                       Toast.makeText(getContext(),"Error updating profile",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                   });
               }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(),"Error fetching profile for update",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            });
        }else{
            addBabyProfileDB.collection(babyProfileDB).add(babyProfile).addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(),"Profile added succesfully",Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(),"Error adding profile",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            });
        }

    }
}